public class Request {

    private final int personId;
    private final int elevatorId;
    private final int beginning;
    private final int destination;

    public Request(int personId, int beginning, int destination, int elevatorId) {
        this.personId = personId;
        this.beginning = beginning;
        this.destination = destination;
        this.elevatorId = elevatorId;
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

    public Direction getDirection() {
        return destination > beginning ? Direction.UP : Direction.DOWN;
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
