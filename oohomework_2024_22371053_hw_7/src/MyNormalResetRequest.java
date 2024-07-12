public class MyNormalResetRequest extends MyResetRequest {
    private final int elevatorId;
    private final int capacity;
    private final long speed;

    public MyNormalResetRequest(int elevatorId, int capacity, double speed) {
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
