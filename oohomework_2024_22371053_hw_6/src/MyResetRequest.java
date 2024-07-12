public class MyResetRequest implements MyRequest {
    private final int elevatorId;
    private final int capacity;
    private final long speed;

    public MyResetRequest(int elevatorId, int capacity, double speed) {
        this.elevatorId = elevatorId;
        this.capacity = capacity;
        this.speed = (long) (speed * 1000);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getSpeed() {
        return speed;
    }
}
