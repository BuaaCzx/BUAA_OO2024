import java.util.ArrayList;

public class RequestQueue {
    private final ArrayList<Request> requests;
    private boolean isEnd;

    public RequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addRequest(Request request) {
        // System.err.println("Added request: "+ request);
        requests.add(request);
        notifyAll();
    }

    public synchronized void removeRequest(Request request) {
        requests.remove(request);
        notifyAll();
    }

    public synchronized Request getOneRequestAndRemove() {
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
        Request request = requests.get(0);
        requests.remove(0);
        notifyAll();
        return request;
    }

    public synchronized Request getMainRequest(int currentFloor) {
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
        int maxDistance = -1;
        Request request = null;
        for (Request r : requests) {
            if (Math.abs(r.getBeginning() - currentFloor) > maxDistance) {
                maxDistance = Math.abs(r.getBeginning() - currentFloor);
                request = r;
            }
        }
        notifyAll();
        return request;
    }

    public synchronized Request getMainRequest2(int currentFloor) {
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
        int minDistance = Integer.MAX_VALUE;
        Request request = null;
        for (Request r : requests) {
            if (Math.abs(r.getBeginning() - currentFloor) < minDistance) {
                minDistance = Math.abs(r.getBeginning() - currentFloor);
                request = r;
            }
        }
        notifyAll();
        return request;
    }

    public synchronized Request getOneRequest() {
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
        Request request = requests.get(0);
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

    public synchronized ArrayList<Request> getRequestsAtFloor(int floor) {
        ArrayList<Request> requestsAtFloor = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (request.getBeginning() == floor) {
                requestsAtFloor.add(request);
            }
        }
        return requestsAtFloor;
    }

    @Override
    public String toString() {
        return requests.toString();
    }
}
