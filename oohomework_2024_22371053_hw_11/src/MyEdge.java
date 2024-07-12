import com.oocourse.spec3.main.Person;

public class MyEdge {
    private final Person person;
    private final int value;

    public MyEdge(Person person, int value) {
        this.person = person;
        this.value = value;
    }

    public Person getPerson() {
        return person;
    }

    public int getPersonId() {
        return person.getId();
    }

    public int getValue() {
        return value;
    }
}
