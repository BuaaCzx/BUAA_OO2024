import java.util.ArrayList;

import com.oocourse.elevator3.TimableOutput;

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
    private Integer currentFloor = minFloor; // 电梯当前所在的楼层
    private Status status = Status.WAITING; // 开门，关门 **中**，停留(门关着)，走 四种状态。这里假设开门后马上就关门。
    private ArrayList<MyPersonRequest> processingRequests = new ArrayList<>(); // 表示正在电梯里的人
    private MyPersonRequest mainRequest = null; // 表示电梯当前正在执行的主请求
    private Direction direction = Direction.UP; // 电梯当前运行方向
    private final Strategy strategy; // 策略类
    private RequestStatus mainRequestStatus = RequestStatus.NO_REQUEST;
    private boolean isDoubleCar;
    private int currentCar = 0;
    private Integer[] currentCarFloor = new Integer[3];
    private int transferFloor;

    public Elevator(
            ElevatorRequestQueue processingQueue,
            Schedule schedule,
            int id
    ) {
        this.id = id;
        this.waitingQueue = processingQueue;
        this.strategy = new Strategy(this);
        this.schedule = schedule;
        this.isDoubleCar = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!isDoubleCar) {
                if (!normalRun()) {
                    return;
                }
            } else {
                if (!doubleCarRun()) {
                    return;
                }
            }
        }
    }

    public boolean normalRun() {
        strategy.tryRemovePassengers();
        if (mainRequest == null) {
            mainRequest = (MyPersonRequest) waitingQueue.getOneRequest();
            if (mainRequest != null) { // 获取到了新的主请求，准备去接它
                mainRequestStatus = RequestStatus.OUTSIDE; // 这个东西好像没什么用qwq
            }
        }
        if (waitingQueue.hasResetRequest()) {
            MyResetRequest resetRequest = waitingQueue.getResetRequestAndRemove();
            if (resetRequest instanceof MyNormalResetRequest) {
                handleNormalResetRequest((MyNormalResetRequest) resetRequest);
            } else if (resetRequest instanceof MyDoubleCarResetRequest) {
                handleDoubleCarResetRequest((MyDoubleCarResetRequest) resetRequest);
            }
            return true;
        }
        if (mainRequest == null) { // 获取不到主请求了，结束
            doorClose();
            // System.err.println("Elevator " + id + " is empty and exit.");
            return false;
        }
        strategy.tryAddPassengers();
        if (mainRequestStatus.equals(RequestStatus.OUTSIDE)
                && mainRequest.getBeginning() == currentFloor) { // 如果明明到了，主请求还没上去，那么更换主请求
            strategy.changeMainRequest();
        }
        direction = strategy.getDirection();
        doorClose();
        move();
        return true;
    }

    public boolean doubleCarRun() {
        if (currentCar != 0) {
            strategy.tryRemovePassengers();
            if (getEleCurrentFloor() == transferFloor) {
                tryTransfer();
            }
        } else {
            currentCar = 1;
        }
        if (mainRequest == null) {
            mainRequest = waitingQueue.getOneRequest(currentCar, transferFloor);
            if (mainRequest != null) { // 获取到了新的主请求，准备去接它
                mainRequestStatus = RequestStatus.OUTSIDE;
                if (currentCar != mainRequest.getDoubleCarId(transferFloor)
                        && currentCar != 0) {
                    doorClose();
                    if (getEleCurrentFloor() == transferFloor) {
                        move(currentCar == 1 ? Direction.DOWN : Direction.UP);
                    }
                }
                currentCar = mainRequest.getDoubleCarId(transferFloor);
            }
        }
        if (mainRequest == null) {
            doorClose();
            return false;
        }
        strategy.tryAddPassengers();
        if (mainRequestStatus.equals(RequestStatus.OUTSIDE)
                && mainRequest.getBeginning() == getEleCurrentFloor()
                && (!isDoubleCar
                || (mainRequest.getDoubleCarId(transferFloor) == currentCar))) {
            strategy.changeMainRequest();
        }
        direction = strategy.getDirection();
        doorClose();
        move();
        return true;
    }

    public void tryTransfer() {
        // [时间戳]RECEIVE-乘客ID-电梯ID[-(A|B)]
        ArrayList<MyPersonRequest> del = new ArrayList<>();
        for (int i = 0; i < processingRequests.size(); i++) {
            MyPersonRequest request = processingRequests.get(i);
            if ((currentCar == 1 && request.getDestination() > transferFloor)
                    || (currentCar == 2 && request.getDestination() < transferFloor)) {
                request.setBeginning(transferFloor);
                del.add(request);
            }
        }
        for (MyPersonRequest request : del) {
            doorOpen();
            removeRequest(request);
            TimableOutput.println("RECEIVE-" + request.getPersonId() + "-" + id
                    + "-" + getCharCarId(request.getDoubleCarId(transferFloor)));
            waitingQueue.addRequest(request);
        }
    }

    public void handleDoubleCarResetRequest(MyDoubleCarResetRequest resetRequest) {
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
                }
                doorClose();
            }
            doorClose(); // 先关门
            // [时间戳]RESET_BEGIN-电梯ID
            status = Status.RESETTING;
            waitingQueue.doubleCarReset(this, resetRequest);
            status = Status.WAITING;
            for (MyRequest request : waitingQueue.getRequests()) {
                MyPersonRequest r = (MyPersonRequest) request;
                TimableOutput.println("RECEIVE-" + r.getPersonId() + "-" + id + "-"
                        + getCharCarId(((MyPersonRequest) request).getDoubleCarId(transferFloor)));
            }
            currentCar = 0;
            mainRequest = null;
        }
        // System.err.println("Elevator" + id + " RESET END, MainReq is " + mainRequest);
    }

    public void handleNormalResetRequest(MyNormalResetRequest resetRequest) {
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
                }
                doorClose();
            }
            doorClose(); // 先关门
            // [时间戳]RESET_BEGIN-电梯ID
            status = Status.RESETTING;
            waitingQueue.normalReset(this, resetRequest);
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
        if (!isDoubleCar) {
            TimableOutput.println("IN" + "-" + request.getPersonId()
                    + "-" + currentFloor + "-" + id);
        } else {
            TimableOutput.println("IN" + "-" + request.getPersonId() + "-"
                    + getEleCurrentFloor() + "-" + id
                    + "-" + getCharCarId(currentCar));
        }

        // IN-乘客ID-所在层-电梯ID
    }

    public void removeRequest(MyPersonRequest request) {
        if (request.equals(mainRequest)) {
            mainRequest = null;
            mainRequestStatus = RequestStatus.NO_REQUEST;
        }
        processingRequests.remove(request);
        if (!isDoubleCar) {
            TimableOutput.println("OUT-" + request.getPersonId() + "-" + currentFloor + "-" + id);
        } else {
            TimableOutput.println("OUT-" + request.getPersonId() + "-" + getEleCurrentFloor() + "-"
                    + id + "-" + getCharCarId(currentCar));
        }
        // OUT-乘客ID-所在层-电梯ID
    }

    public void doorOpen() {
        if (status != Status.WAITING && status != Status.RESETTING) {
            return;
        }
        if (!isDoubleCar) {
            TimableOutput.println("OPEN" + "-" + currentFloor + "-" + id);
        } else {
            TimableOutput.println("OPEN" + "-" + getEleCurrentFloor() + "-" + id
                    + "-" + getCharCarId(currentCar));
        }
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
        if (!isDoubleCar) {
            TimableOutput.println("CLOSE" + "-" + currentFloor + "-" + id);
        } else {
            TimableOutput.println("CLOSE" + "-" + getEleCurrentFloor() + "-" + id
                    + "-" + getCharCarId(currentCar));
        }
        status = Status.WAITING;
    }

    private void moveUp() {
        status = Status.MOVING;
        try {
            sleep(moveTimePerFloor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!isDoubleCar) {
            currentFloor++;
        } else {
            currentCarFloor[currentCar]++;
        }
    }

    private void moveDown() {
        status = Status.MOVING;
        try {
            sleep(moveTimePerFloor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!isDoubleCar) {
            currentFloor--;
        } else {
            currentCarFloor[currentCar]--;
        }
    }

    public void move() {
        if (this.direction.equals(Direction.UP)) {
            moveUp();
        } else {
            moveDown();
        }
        status = Status.WAITING;
        if (!isDoubleCar) {
            TimableOutput.println("ARRIVE-" + currentFloor + "-" + id);
        } else {
            currentFloor = currentCarFloor[currentCar];
            TimableOutput.println("ARRIVE-" + getEleCurrentFloor() + "-" + id
                    + "-" + getCharCarId(currentCar));
        }
        // ARRIVE-所在层-电梯ID
    }

    public void move(Direction d) {
        if (d.equals(Direction.UP)) {
            moveUp();
        } else {
            moveDown();
        }
        status = Status.WAITING;
        if (!isDoubleCar) {
            TimableOutput.println("ARRIVE-" + currentFloor + "-" + id);
        } else {
            currentFloor = currentCarFloor[currentCar];
            TimableOutput.println("ARRIVE-" + getEleCurrentFloor() + "-" + id
                    + "-" + getCharCarId(currentCar));
        }
        // ARRIVE-所在层-电梯ID
    }

    public int getPassengerNum() {
        return processingRequests.size();
    }

    public ArrayList<MyPersonRequest> getFinishedRequests() {
        ArrayList<MyPersonRequest> finishedRequests = new ArrayList<>();
        for (int i = 0; i < processingRequests.size(); i++) {
            if (processingRequests.get(i).getDestination() == getEleCurrentFloor()) {
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

    public Integer getEleCurrentFloor() {
        if (!isDoubleCar) {
            return currentFloor;
        } else {
            if (currentCar == 0) {
                System.err.println("Car ERROR!!!");
            }
            return currentCarFloor[currentCar];
        }
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

    public char getCharCarId(int id) {
        return id == 1 ? 'A' : 'B';
    }

    public boolean isDoubleCar() {
        return isDoubleCar;
    }

    public int getCurrentCar() {
        return currentCar;
    }

    public int getCurrentCarFloor(int id) {
        return currentCarFloor[id];
    }

    public int getTransferFloor() {
        return transferFloor;
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

    public void setIsDoubleCar(boolean isDoubleCar) {
        this.isDoubleCar = isDoubleCar;
    }

    public void setCurrentCarFloor(int id, int currentFloor) {
        this.currentCarFloor[id] = currentFloor;
    }

    public void setTransferFloor(int transferFloor) {
        this.transferFloor = transferFloor;
    }
}
