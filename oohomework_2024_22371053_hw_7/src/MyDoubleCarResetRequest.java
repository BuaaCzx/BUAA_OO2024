
public class MyDoubleCarResetRequest extends MyResetRequest {

    private final int elevatorId;
    private final int transferFloor;
    private final int capacity;
    private final long speed;

    public MyDoubleCarResetRequest(int elevatorId, int transferFloor, int capacity, double speed) {
        this.elevatorId = elevatorId;
        this.transferFloor = transferFloor;
        this.capacity = capacity;
        this.speed = (long) (speed * 1000);
    }

    public int getElevatorId() {
        return this.elevatorId;
    }

    public int getTransferFloor() {
        return this.transferFloor;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public long getSpeed() {
        return this.speed;
    }

}
