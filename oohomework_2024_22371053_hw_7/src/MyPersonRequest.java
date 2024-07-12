public class MyPersonRequest implements MyRequest { // 继承

    private final int personId;
    private int elevatorId;
    private int beginning;
    private final int destination;

    public MyPersonRequest(int personId, int beginning, int destination) {
        this.personId = personId;
        this.beginning = beginning;
        this.destination = destination;
        this.elevatorId = -1;
    }

    public int getPersonId() {
        return personId;
    }

    public int getBeginning() {
        return beginning;
    }

    public int getDestination() {
        return destination;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public boolean hasElevator() {
        return elevatorId != -1;
    }

    public Direction getDirection() {
        return destination > beginning ? Direction.UP : Direction.DOWN;
    }

    public void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    public void setBeginning(int beginning) {
        this.beginning = beginning;
    }

    public int getDoubleCarId(int transferFloor) {
        if (beginning < transferFloor) {
            return 1;
        } else if (beginning > transferFloor) {
            return 2;
        } else {
            return destination > transferFloor ? 2 : 1;
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "personId=" + personId +
                ", elevatorId=" + elevatorId +
                ", beginning=" + beginning +
                ", destination=" + destination +
                '}';
    }
}
