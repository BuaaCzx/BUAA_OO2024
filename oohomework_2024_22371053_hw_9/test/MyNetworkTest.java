import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.main.Person;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class MyNetworkTest {

    static public MyNetwork network;
    static public MyNetwork networkCopy;
    static public List<Person> persons;
    static public List<Person> personsCopy;
    static public Random random = new Random();
    public int cnt = 0;

    public static List<Person> generateCompleteGraph(int numPersons) {
        network = new MyNetwork();
        networkCopy = new MyNetwork();
        persons = new ArrayList<>();
        personsCopy = new ArrayList<>();
        for (int i = 0; i < numPersons; i++) {
            Person person = new MyPerson(i, "Person" + i, 20 + random.nextInt(40));
            Person personCopy = new MyPerson(i, "Person" + i, person.getAge());
            try {
                network.addPerson(person);
                networkCopy.addPerson(personCopy);
            } catch (EqualPersonIdException e) {
            }
            persons.add(person);
            personsCopy.add(personCopy);
        }

        for (int i = 0; i < numPersons; i++) {
            for (int j = i + 1; j < numPersons; j++) {
                try {
                    int val = random.nextInt(50) + 1;
                    network.addRelation(persons.get(i).getId(), persons.get(j).getId(), val);
                    networkCopy.addRelation(personsCopy.get(i).getId(), personsCopy.get(j).getId(), val);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                }
            }
        }

        return persons;
    }

    public void generateRandomGraph(int numPersons) {
        network = new MyNetwork();
        persons = new ArrayList<>();
        networkCopy = new MyNetwork();
        personsCopy = new ArrayList<>();
        for (int i = 0; i < numPersons; i++) {
            Person person = new MyPerson(i, "Person" + i, 20 + random.nextInt(40));
            Person personCopy = new MyPerson(i, "Person" + i, person.getAge());
            try {
                network.addPerson(person);
                persons.add(person);
                personsCopy.add(personCopy);
                networkCopy.addPerson(personCopy);
            } catch (EqualPersonIdException e) {
            }
        }

        int numRelations = numPersons * random.nextInt(5);
        for (int i = 0; i < numRelations; i++) {
            int id1 = random.nextInt(numPersons);
            int id2 = random.nextInt(numPersons);
            if (id1 != id2) {
                try {
                    int val = random.nextInt(50) + 1;
                    network.addRelation(persons.get(id1).getId(), persons.get(id2).getId(), val);
                    networkCopy.addRelation(personsCopy.get(id1).getId(), personsCopy.get(id2).getId(), val);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                }
            }
        }
    }

    public void generateRandomGraph2(int numPersons) {
        network = new MyNetwork();
        persons = new ArrayList<>();
        networkCopy = new MyNetwork();
        personsCopy = new ArrayList<>();
        for (int i = 0; i < numPersons; i++) {
            Person person = new MyPerson(i, "Person" + i, 20 + random.nextInt(40));
            Person personCopy = new MyPerson(i, "Person" + i, person.getAge());
            try {
                network.addPerson(person);
                persons.add(person);
                personsCopy.add(personCopy);
                networkCopy.addPerson(personCopy);
            } catch (EqualPersonIdException e) {
            }
        }

        int numRelations = numPersons * random.nextInt(5);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 50; j++) {
                int id1 = i * 10 + random.nextInt(10);
                int id2 = i * 10 + random.nextInt(10);

                if (id1 != id2) {
                    try {
                        int val = random.nextInt(50) + 1;
                        network.addRelation(persons.get(id1).getId(), persons.get(id2).getId(), val);
                        networkCopy.addRelation(personsCopy.get(id1).getId(), personsCopy.get(id2).getId(), val);
                    } catch (PersonIdNotFoundException | EqualRelationException e) {
                    }
                }
            }
        }
    }

    public void generateNetwork() {
        if (cnt % 3 == 0) {
            generateCompleteGraph(150);
        } else if (cnt % 3 == 1) {
            generateRandomGraph(150);
        } else if (cnt % 3 == 2) {
            generateRandomGraph2(150);
        }
        cnt++;
    }

    @Test
    public void queryTripleSum() {
        for (int i = 0; i < 50; i++) {
            generateNetwork();
            int res = calculateTripleSum(personsCopy);
            assertEquals(res, network.queryTripleSum());
            assertEquals(network.getPersons().length, networkCopy.getPersons().length);
            boolean changed = false;
            for (int j = 0; j < 150; j++) {
                if (!((MyPerson) network.getPerson(j)).strictEquals(networkCopy.getPerson(j))) {
                    changed = true;
                }
            }
            assertFalse(changed);
        }
    }

    private static int calculateTripleSum(List<Person> persons) {
        int tripleSum = 0;
        for (int i = 0; i < persons.size(); i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                for (int k = j + 1; k < persons.size(); k++) {
                    Person pi = persons.get(i);
                    Person pj = persons.get(j);
                    Person pk = persons.get(k);
                    if (pi.isLinked(pj) &&
                            pj.isLinked(pk) &&
                            pi.isLinked(pk)) {
                        tripleSum++;
                    }
                }
            }
        }
        return tripleSum;
    }
}