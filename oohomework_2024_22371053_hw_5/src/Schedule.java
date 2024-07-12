import java.util.ArrayList;

public class Schedule extends Thread {
    private final RequestQueue waitQueue;
    private final ArrayList<RequestQueue> processingQueues;

    public Schedule(RequestQueue waitQueue, ArrayList<RequestQueue> processingQueues) {
        this.waitQueue = waitQueue;
        this.processingQueues = processingQueues;
    }

    @Override
    public void run() {
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (int i = 0; i < processingQueues.size(); i++) {
                    processingQueues.get(i).setEnd(true);
                }
                System.err.println("Schedule End");
                return;
            }

            Request request = waitQueue.getOneRequestAndRemove();
            if (request == null) {
                continue;
            }
            // System.err.println(request);
            processingQueues.get(request.getElevatorId() - 1).addRequest(request);
        }
    }
}
