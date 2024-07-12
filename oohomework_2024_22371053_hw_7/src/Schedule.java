import java.util.ArrayList;

import com.oocourse.elevator3.TimableOutput;

public class Schedule extends Thread {
    private final ScheduleRequestQueue waitQueue;
    private final ArrayList<ElevatorRequestQueue> elevatorWaitingQueues;
    private final BalancedRandom random = new BalancedRandom();
    private final ArrayList<Elevator> elevators;

    public Schedule(
            ScheduleRequestQueue waitQueue,
            ArrayList<ElevatorRequestQueue> processingQueues,
            ArrayList<Elevator> elevators
    ) {
        this.waitQueue = waitQueue;
        this.elevatorWaitingQueues = processingQueues;
        this.elevators = elevators;
        // System.err.println(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (int i = 0; i < elevatorWaitingQueues.size(); i++) {
                    elevatorWaitingQueues.get(i).setEnd(true);
                }
                /*
                for (int i = 0; i < elevatorResetQueues.size(); i++) {
                    elevatorResetQueues.get(i).setEnd(true);
                }
                */
                System.err.println("Schedule End");
                return;
            }

            MyRequest request = waitQueue.getOneRequestAndRemove();
            if (request == null) {
                continue;
            }
            // System.err.println(request);
            // elevatorWaitingQueues.get(request.getElevatorId() - 1).addRequest(request);

            if (request instanceof MyNormalResetRequest) {
                System.err.println("ERROR!!! Reset not here!!!");
                MyNormalResetRequest resetRequest = (MyNormalResetRequest) request;
                elevatorWaitingQueues.get(resetRequest.getElevatorId() - 1).addRequest(
                        resetRequest
                );
            } else if (request instanceof MyPersonRequest) {
                MyPersonRequest personRequest = (MyPersonRequest) request;
                int elevatorId; // TODO:更换成更优的分配算法 计划用影子电梯模拟
                if (!personRequest.hasElevator()) {
                    elevatorId = random.getNextNumber();
                    // elevatorId = 1;
                    personRequest.setElevatorId(elevatorId);
                } else {
                    elevatorId = personRequest.getElevatorId();
                }
                // 不要在这里输出，不然可能会寄
                synchronized (elevatorWaitingQueues.get(elevatorId - 1)) {
                    elevatorWaitingQueues.get(elevatorId - 1).addRequest(
                            personRequest
                    );
                    Elevator elevator = elevators.get(elevatorId - 1);
                    if (!elevator.isDoubleCar()) {
                        TimableOutput.println("RECEIVE-"
                                + personRequest.getPersonId()
                                + "-" + elevatorId);
                    } else {
                        TimableOutput.println("RECEIVE-"
                                + personRequest.getPersonId()
                                + "-" + elevatorId
                                + "-" + elevator.getCharCarId(
                                        personRequest.getDoubleCarId(
                                                elevator.getTransferFloor())));
                    }
                }
                // RECEIVE-乘客ID-电梯ID
            }
        }
    }
}
