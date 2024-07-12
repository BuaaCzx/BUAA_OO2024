import java.util.ArrayList;

public class Strategy {
    private Elevator elevator;

    public Strategy(Elevator elevator) {
        this.elevator = elevator;
    }

    /*
    public Direction getDirection() {
        if (elevator.getProcessingRequests().contains(elevator.getMainRequest())) {
            // 如果主请求在请求队列中，或者新进来的主请求起始楼层是同一楼层
            // 则电梯去送主请求
            return elevator.getMainRequest().getDestination() > elevator.getCurrentFloor()
                    ? Direction.UP
                    : Direction.DOWN;
        } else {
            // 否则，去接新的主请求
            return elevator.getMainRequest().getBeginning() > elevator.getCurrentFloor()
                    ? Direction.UP
                    : Direction.DOWN;
        }
    }
    */

    public Direction getDirection() {
        if (elevator.getMainRequestStatus().equals(RequestStatus.INSIDE)) {
            return elevator.getMainRequest().getDestination() > elevator.getCurrentFloor()
                    ? Direction.UP
                    : Direction.DOWN;
        } else {
            return elevator.getMainRequest().getBeginning() > elevator.getCurrentFloor()
                    ? Direction.UP
                    : Direction.DOWN;
        }
    }

    public void tryRemovePassengers() {
        ArrayList<MyPersonRequest> finishedRequests = elevator.getFinishedRequests();
        if (!finishedRequests.isEmpty()) { // 检查是否有人可以下电梯，如果有，就让他下，并修改主请求
            elevator.doorOpen(); // 开门
            for (int i = 0; i < finishedRequests.size(); i++) {
                elevator.removeRequest(finishedRequests.get(i)); // 把已经完成的人从processingRequests中移除
            }
        }
        setNextMainRequest();
    }

    /*
    public void tryAddPassengers() {
        RequestQueue waitingQueue = elevator.getWaitingQueue();
        Direction direction = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();
        int maxPassengers = elevator.getMaxPassengers();

        ArrayList<Request> floorRequests =
                waitingQueue.getRequestsAtFloor(currentFloor); // 获取停靠在当前楼层的请求
        for (int i = 0; i < floorRequests.size(); i++) {
            Request request = floorRequests.get(i);
            if (elevator.getPassengerNum() < maxPassengers
                    && request.getDirection().equals(direction)) {
                elevator.doorOpen();
                elevator.addPassenger(request);
                waitingQueue.removeRequest(request);
            }
        }
    }
    */

    public void tryAddPassengers() {
        int currentFloor = elevator.getCurrentFloor();
        int maxPassengers = elevator.getMaxPassengers();
        MyPersonRequest mainRequest = elevator.getMainRequest();
        Direction direction =
                mainRequest == null
                ? getMaxDirection(currentFloor)
                : mainRequest.getDirection();
        ElevatorRequestQueue waitingQueue = elevator.getWaitingQueue();
        ArrayList<MyPersonRequest> requests =
                waitingQueue.getRequestsAtFloor(currentFloor); // 获取停靠在当前楼层的请求
        // System.err.println("Try add passengers on floor "
        // + currentFloor + "to direction " + direction
        // + ", waitingQueue " + waitingQueue);
        for (int i = 0; i < requests.size(); i++) {
            MyPersonRequest request = requests.get(i);
            if (elevator.getPassengerNum() < maxPassengers
                    && request.getDirection().equals(direction)) {
                elevator.doorOpen();
                elevator.addPassenger(request);
                waitingQueue.removeRequest(request);
            }
        }
        setNextMainRequest();
    }

    public Direction getMaxDirection(int currentFloor) {
        ElevatorRequestQueue waitingQueue = elevator.getWaitingQueue();
        int up = 0;
        int down = 0;
        ArrayList<MyPersonRequest> requests = waitingQueue.getRequestsAtFloor(currentFloor);
        for (MyPersonRequest request : requests) {
            if (request.getDirection().equals(Direction.UP)) {
                up++;
            } else {
                down++;
            }
        }
        return up > down ? Direction.UP : Direction.DOWN;
    }

    public void setNextMainRequest() { // 仅在主请求为null时调用。
        if (elevator.getMainRequest() != null) {
            return;
        }
        if (elevator.getProcessingRequests().isEmpty()) {
            elevator.setMainRequest(null);
            elevator.setMainRequestStatus(RequestStatus.NO_REQUEST);
        } else {
            elevator.setMainRequest(elevator.getProcessingRequests().get(0));
            elevator.setMainRequestStatus(RequestStatus.INSIDE);
        }
    }

    public void changeMainRequest() {
        elevator.setMainRequest(elevator.getProcessingRequests().get(0));
        elevator.setMainRequestStatus(RequestStatus.INSIDE);
    }

}
