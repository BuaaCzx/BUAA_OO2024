import java.util.ArrayList;

import com.oocourse.elevator2.TimableOutput;

public class Elevator extends Thread {

    private int maxPassengers = 6;
    private final int maxFloor = 11;
    private final int minFloor = 1;
    private long moveTimePerFloor = 400L; // 移动一层0.4s
    private final long doorTime = 200L; // 门打开关闭时间0.2s
    private final long resetTime = 1200L;

    private final ElevatorRequestQueue waitingQueue; // 等待队列
    private final int id; // 电梯编号
    private final Schedule schedule;
    private int currentFloor = minFloor; // 电梯当前所在的楼层
    private Status status = Status.WAITING; // 开门，关门 **中**，停留(门关着)，走 四种状态。这里假设开门后马上就关门。
    private ArrayList<MyPersonRequest> processingRequests = new ArrayList<>(); // 表示正在电梯里的人
    private MyPersonRequest mainRequest = null; // 表示电梯当前正在执行的主请求
    private Direction direction = Direction.UP; // 电梯当前运行方向
    private final Strategy strategy; // 策略类
    private RequestStatus mainRequestStatus = RequestStatus.NO_REQUEST;

    public Elevator(
            ElevatorRequestQueue processingQueue,
            Schedule schedule,
            int id
    ) {
        this.id = id;
        this.waitingQueue = processingQueue;
        this.strategy = new Strategy(this);
        this.schedule = schedule;
    }

    @Override
    public void run() {
        while (true) {
            // 电梯停靠，检查：是否有人可以下电梯？是否有人可以上电梯？当前电梯的主请求是什么？是否移动？
            strategy.tryRemovePassengers();

            // strategy.tryAddPassengers(); 放这里好像不太行，要先等 waitingQueue 更新，再上人

            // strategy.updateMainRequest(); 这个方法写的有 bug，可能导致主请求频繁切换，最终导致电梯卡死。

            // 这里实际上是默认了，电梯的处理队列空了，主请求就无了，才会尝试获取主请求，也就是等待新请求进入
            if (mainRequest == null) {
                mainRequest = (MyPersonRequest) waitingQueue.getOneRequest();
                // 选最早的 or 选一个最远的 or 选一个最近的？
                // 这里return的原因可能是因为获取到了新请求，也可能是因为获取到了Reset请求
                // return null 可能是因为没有新请求了，也可能是因为获取到了Reset请求
                if (mainRequest != null) { // 获取到了新的主请求，准备去接它
                    mainRequestStatus = RequestStatus.OUTSIDE; // 这个东西好像没什么用qwq
                }
            }

            // System.err.println(
            // "Elevator " + id + " is arrived, "
            // + "MainRequest: " + mainRequest + ", "
            // + "CurrentFloor: " + currentFloor + ", "
            // + "Direction: " + direction + ", "
            // + "ProcessingRequests: " + processingRequests);

            if (waitingQueue.hasResetRequest()) {
                // System.err.println("RESET!!!");
                MyResetRequest resetRequest = waitingQueue.getResetRequestAndRemove();
                handleResetRequest(resetRequest);
                continue;
            }

            if (mainRequest == null) { // 获取不到主请求了，结束
                doorClose();
                // System.err.println("Elevator " + id + " is empty and exit.");
                return;
            }

            strategy.tryAddPassengers();

            // 现在有一个问题，电梯准备去接主请求，到达主请求的Beginning楼层后，有可能由于过程中的捎带，导致电梯已经装不下主请求。
            // 这时候应该更换主请求！
            // 我的算法是，除了这种情况外，都不在主请求未送达的情况下更改主请求。
            // 这里应该特判一下。
            if (mainRequestStatus.equals(RequestStatus.OUTSIDE)
                    && mainRequest.getBeginning() == currentFloor) { // 如果明明到了，主请求还没上去，那么更换主请求
                strategy.changeMainRequest();
            }

            direction = strategy.getDirection();

            // System.err.println("Elevator " + id + " is running, "
            // + "MainRequest: " + mainRequest + ", " + "CurrentFloor: "
            // + currentFloor + ", " + "Direction: " + direction + ", "
            // + "ProcessingRequests: " + processingRequests);

            doorClose();
            move();
        }
    }

    public void handleResetRequest(MyResetRequest resetRequest) {
        ArrayList<MyPersonRequest> temp = new ArrayList<MyPersonRequest>();
        for (MyPersonRequest request : processingRequests) {
            temp.add(request);
        }
        synchronized (waitingQueue) { // 加锁下人
            if (!temp.isEmpty()) { // 电梯上有人
                doorOpen();
                for (MyPersonRequest request : temp) {
                    removeRequest(request);
                    request.setBeginning(currentFloor);
                    waitingQueue.addRequest(request);
                    /*
                        TODO:现在的做法是乘客从哪下，还从哪上。完善调度器后，可以把乘客全部传回给调度器，让调度器重新分配
                        可能存在的问题：如果调度器重新分配后，发现还是分配给现在这个电梯最好，那在这台电梯的静默期，是不可以输出RECEIVE的。
                        怎么解决？
                    */
                }
                doorClose();
            }
            doorClose(); // 先关门
            // [时间戳]RESET_BEGIN-电梯ID
            status = Status.RESETTING;
            waitingQueue.reset(this, resetRequest);
            status = Status.WAITING;
            for (MyRequest request : waitingQueue.getRequests()) {
                MyPersonRequest r = (MyPersonRequest) request;
                TimableOutput.println("RECEIVE-" + r.getPersonId() + "-" + id);
            }
        }
    }

    public void addPassenger(MyPersonRequest request) {
        if (request.equals(mainRequest)) {
            mainRequestStatus = RequestStatus.INSIDE;
        }
        processingRequests.add(request);
        TimableOutput.println("IN" + "-" + request.getPersonId() + "-" + currentFloor + "-" + id);
        // IN-乘客ID-所在层-电梯ID
    }

    public void removeRequest(MyPersonRequest request) {
        if (request.equals(mainRequest)) {
            mainRequest = null;
            mainRequestStatus = RequestStatus.NO_REQUEST;
        }
        processingRequests.remove(request);
        TimableOutput.println("OUT-" + request.getPersonId() + "-" + currentFloor + "-" + id);
        // OUT-乘客ID-所在层-电梯ID
    }

    public void doorOpen() {
        if (status != Status.WAITING && status != Status.RESETTING) {
            return;
        }
        TimableOutput.println("OPEN" + "-" + currentFloor + "-" + id);
        // OPEN-所在层-电梯ID
        status = Status.OPENING;
        try {
            sleep(doorTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void doorClose() {
        if (status != Status.OPENING) {
            return;
        }
        try {
            sleep(doorTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("CLOSE" + "-" + currentFloor + "-" + id);
        status = Status.WAITING;
    }

    private void moveUp() {
        status = Status.MOVING;
        try {
            sleep(moveTimePerFloor);
            currentFloor++;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveDown() {
        status = Status.MOVING;
        try {
            sleep(moveTimePerFloor);
            currentFloor--;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void move() {
        if (this.direction.equals(Direction.UP)) {
            moveUp();
        } else {
            moveDown();
        }
        status = Status.WAITING;
        TimableOutput.println("ARRIVE-" + currentFloor + "-" + id);
        // ARRIVE-所在层-电梯ID
    }

    public int getPassengerNum() {
        return processingRequests.size();
    }

    public ArrayList<MyPersonRequest> getFinishedRequests() {
        ArrayList<MyPersonRequest> finishedRequests = new ArrayList<>();
        for (int i = 0; i < processingRequests.size(); i++) {
            if (processingRequests.get(i).getDestination() == currentFloor) {
                finishedRequests.add(processingRequests.get(i));
            }
        }
        return finishedRequests;
    }

    public ArrayList<MyPersonRequest> getProcessingRequests() {
        return processingRequests;
    }

    public MyPersonRequest getMainRequest() {
        return mainRequest;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorRequestQueue getWaitingQueue() {
        return waitingQueue;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setMainRequest(MyPersonRequest mainRequest) {
        this.mainRequest = mainRequest;
    }

    public RequestStatus getMainRequestStatus() {
        return mainRequestStatus;
    }

    public long getResetTime() {
        return resetTime;
    }

    public int getElevatorId() {
        return id;
    }

    public void setMainRequestStatus(RequestStatus mainRequestStatus) {
        this.mainRequestStatus = mainRequestStatus;
    }

    public void setMaxPassengers(int maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public void setMoveTimePerFloor(long moveTimePerFloor) {
        this.moveTimePerFloor = moveTimePerFloor;
    }
}