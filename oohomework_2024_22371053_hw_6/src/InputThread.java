import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.ResetRequest;

import java.util.ArrayList;
//import com.oocourse.elevator2.Request;

public class InputThread extends Thread {
    private final ScheduleRequestQueue waitQueue;
    private final ArrayList<ElevatorRequestQueue> elevatorRequestQueues;

    public InputThread(
            ScheduleRequestQueue waitQueue,
            ArrayList<ElevatorRequestQueue> elevatorRequestQueues
    ) {
        this.waitQueue = waitQueue;
        this.elevatorRequestQueues = elevatorRequestQueues;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            com.oocourse.elevator2.Request request = elevatorInput.nextRequest();
            if (request == null) {
                waitQueue.setEnd(true);
                // System.err.println("Input End");
                return;
            } else if (request instanceof PersonRequest) { // 是乘客，下传给schedule
                PersonRequest personRequest = (PersonRequest) request;
                MyPersonRequest r = new MyPersonRequest(
                        personRequest.getPersonId(),
                        personRequest.getFromFloor(),
                        personRequest.getToFloor()
                );
                synchronized (waitQueue) {
                    waitQueue.addRequest(r);
                    waitQueue.notifyAll();
                }
            } else if (request instanceof ResetRequest) { // 是reset，直接传给电梯的waitQueue
                ResetRequest request1 = (ResetRequest) request;
                MyResetRequest r = new MyResetRequest(
                        request1.getElevatorId(),
                        request1.getCapacity(),
                        request1.getSpeed()
                );
                synchronized (elevatorRequestQueues.get(r.getElevatorId() - 1)) {
                    elevatorRequestQueues.get(r.getElevatorId() - 1).addRequest(r);
                    elevatorRequestQueues.get(r.getElevatorId() - 1).notifyAll();
                }
            }
        }
    }
}
