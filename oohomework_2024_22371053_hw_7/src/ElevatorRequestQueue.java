import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

// 对于电梯和调度器之间的交互的请求队列。这里的Request仅有PersonRequest。
public class ElevatorRequestQueue implements RequestQueue {
    private final ArrayList<MyRequest> requests;
    private boolean isEnd;
    // private ElevatorResetQueue elevatorResetQueue = null;

    public ElevatorRequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addRequest(MyRequest request) {
        // System.err.println("Added request: " + request);
        requests.add(request);
        notifyAll();
    }

    public synchronized void removeRequest(MyRequest request) {
        requests.remove(request);
        notifyAll();
    }

    public synchronized MyRequest getOneRequest() {
        while (requests.isEmpty() && !isEnd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty() || hasResetRequest()) {
            return null;
        }
        MyPersonRequest request = (MyPersonRequest) requests.get(0);
        notifyAll();
        return request;
    }

    public synchronized MyPersonRequest getOneRequest(int currentCarId, int transferFloor) {
        while (requests.isEmpty() && !isEnd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty()) {
            return null;
        }
        if (requests.isEmpty()) {
            return null;
        }
        for (int i = 0; i < requests.size(); i++) {
            MyPersonRequest request = (MyPersonRequest) requests.get(i);
            if (request.getDoubleCarId(transferFloor) == currentCarId) {
                notifyAll();
                return request;
            }
        }
        MyPersonRequest request = (MyPersonRequest) requests.get(0);
        notifyAll();
        return request;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return requests.isEmpty();
    }

    public synchronized boolean hasResetRequest() {
        for (MyRequest request : requests) {
            if (request instanceof MyResetRequest) {
                return true;
            }
        }
        return false;
    }

    public synchronized MyResetRequest getResetRequestAndRemove() {
        MyResetRequest res = null;
        for (MyRequest request : requests) {
            if (request instanceof MyResetRequest) {
                res = (MyResetRequest) request;
            }
        }
        requests.remove(res);
        return res;
    }

    public synchronized ArrayList<MyPersonRequest> getRequestsAtFloor(int floor) {
        ArrayList<MyPersonRequest> requestsAtFloor = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i) instanceof MyNormalResetRequest) {
                continue;
            }
            MyPersonRequest request = (MyPersonRequest) requests.get(i);
            if (request.getBeginning() == floor) {
                requestsAtFloor.add(request);
            }
        }
        return requestsAtFloor;
    }

    public synchronized void normalReset(Elevator elevator, MyNormalResetRequest resetRequest) {
        TimableOutput.println("RESET_BEGIN-" + elevator.getElevatorId());
        elevator.setMaxPassengers(resetRequest.getCapacity());
        elevator.setMoveTimePerFloor(resetRequest.getSpeed());
        try {
            sleep(elevator.getResetTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // [时间戳]RESET_END-电梯ID
        TimableOutput.println("RESET_END-" + elevator.getElevatorId());
    }

    public synchronized void doubleCarReset(
            Elevator elevator,
            MyDoubleCarResetRequest resetRequest
    ) {
        TimableOutput.println("RESET_BEGIN-" + elevator.getElevatorId());
        elevator.setMaxPassengers(resetRequest.getCapacity());
        elevator.setMoveTimePerFloor(resetRequest.getSpeed());
        elevator.setIsDoubleCar(true);
        elevator.setTransferFloor(resetRequest.getTransferFloor());
        elevator.setCurrentCarFloor(1, resetRequest.getTransferFloor() - 1);
        elevator.setCurrentCarFloor(2, resetRequest.getTransferFloor() + 1);
        try {
            sleep(elevator.getResetTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // [时间戳]RESET_END-电梯ID
        TimableOutput.println("RESET_END-" + elevator.getElevatorId());
    }

    public synchronized ArrayList<MyRequest> getRequests() {
        return requests;
    }

    @Override
    public synchronized String toString() {
        return requests.toString();
    }
}
