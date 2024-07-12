import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

public class InputThread extends Thread {
    private final RequestQueue waitQueue;

    public InputThread(RequestQueue waitQueue) {
        this.waitQueue = waitQueue;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest personRequest = elevatorInput.nextPersonRequest();
            if (personRequest == null) {
                waitQueue.setEnd(true);
                // System.err.println("Input End");
                return;
            } else {
                Request request = new Request(
                        personRequest.getPersonId(),
                        personRequest.getFromFloor(),
                        personRequest.getToFloor(),
                        personRequest.getElevatorId()
                );
                synchronized (waitQueue) {
                    waitQueue.addRequest(request);
                    waitQueue.notifyAll();
                }
            }
        }
    }
}
