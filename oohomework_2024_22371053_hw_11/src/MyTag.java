import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.Collection;
import java.util.HashMap;

public class MyTag implements Tag {

    private int id;
    private int valueSum;
    private int ageSum;
    private int agePowSum;
    private final HashMap<Integer, Person> persons;

    public MyTag(int id) {
        this.id = id;
        valueSum = 0;
        ageSum = 0;
        agePowSum = 0;
        persons = new HashMap<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {

            // 维护valueSum
            for (Person p : persons.values()) {
                if (p.isLinked(person)) {
                    valueSum += p.queryValue(person) * 2;
                }
            }

            // 维护 ageSum
            ageSum += person.getAge();
            agePowSum += person.getAge() * person.getAge();

            persons.put(person.getId(), person);
        }
    }

    public void addRelation(Person person1, Person person2, int value) {
        if (hasPerson(person1) && hasPerson(person2)) {
            valueSum = valueSum - person1.queryValue(person2) * 2 + value * 2;
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return valueSum;
    }

    @Override
    public int getAgeMean() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            return ageSum / persons.size();
        }
    }

    // 算方差。存疑。
    @Override
    public int getAgeVar() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            int n = persons.size();
            int ageMean = getAgeMean();
            return (agePowSum - 2 * ageSum * ageMean + n * ageMean * ageMean) / n;
        }
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            // 维护valueSum
            for (Person p : persons.values()) {
                if (p.isLinked(person)) {
                    valueSum -= p.queryValue(person) * 2;
                }
            }

            // 维护 ageSum
            ageSum -= person.getAge();
            agePowSum -= person.getAge() * person.getAge();

            persons.remove(person.getId());
        }
    }

    @Override
    public int getSize() {
        return persons.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Tag) {
            Tag other = (Tag) obj;
            return this.id == other.getId();
        } else {
            return false;
        }
    }

    public Collection<Person> getPersons() {
        return persons.values();
    }

}
