import java.util.ArrayList;

// 对于电梯和调度器之间的交互的请求队列。这里的Request仅有PersonRequest。
// ResetRequest由于只有一个，直接用一个变量来承当盘子。
public class ScheduleRequestQueue implements RequestQueue {
    private final ArrayList<MyRequest> requests;
    private boolean isEnd;

    public ScheduleRequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addRequest(MyRequest request) {
        // System.err.println("Added request: "+ request);
        requests.add(request);
        notifyAll();
    }

    public synchronized void removeRequest(MyRequest request) {
        requests.remove(request);
        notifyAll();
    }

    public synchronized MyRequest getOneRequestAndRemove() {
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
        MyRequest request = requests.get(0);
        requests.remove(0);
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

    @Override
    public String toString() {
        return requests.toString();
    }
}
