import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;

public class MyPerson implements Person {

    /*@ public instance model int id;
      @ public instance model non_null String name;
      @ public instance model int age;
      @ public instance model non_null Person[] acquaintance;
      @ public instance model non_null int[] value;
      @*/
    private int id;
    private String name;
    private int age;
    private HashMap<Person, Integer> acquaintanceValueMap;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        acquaintanceValueMap = new HashMap<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Person) {
            Person otherPerson = (Person) obj;
            return otherPerson.getId() == this.getId();
        } else {
            return false;
        }
    }

    @Override
    public boolean isLinked(Person person) {
        if (person == this) {
            return true;
        }

        return acquaintanceValueMap.containsKey(person);
    }

    @Override
    public int queryValue(Person person) {
        return acquaintanceValueMap.getOrDefault(person, 0);
    }

    public void addAcquaintance(Person person, int value) {
        acquaintanceValueMap.put(person, value);
    }

    public void deleteAcquaintance(Person person) {
        acquaintanceValueMap.remove(person);
    }

    public void getBlock(HashSet<Person> vis) {
        vis.add(this);
        for (Person p : acquaintanceValueMap.keySet()) {
            if (!vis.contains(p)) {
                ((MyPerson) p).getBlock(vis);
            }
        }
    }

    public HashSet<Person> getAcquaintances() {
        return new HashSet<>(acquaintanceValueMap.keySet());
    }

    public boolean strictEquals(Person person) {
        return true;
    }

}
