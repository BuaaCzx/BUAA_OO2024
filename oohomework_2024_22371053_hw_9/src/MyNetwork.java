import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;

public class MyNetwork implements Network {

    private HashMap<Integer, Person> idPersonMap = new HashMap<>();
    private int triples = 0;
    private boolean reCalc = true;
    private int blockSum = 0;

    public MyNetwork() {
    }

    @Override
    public boolean containsPerson(int id) {
        return idPersonMap.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        if (containsPerson(id)) {
            return idPersonMap.get(id);
        } else {
            return null;
        }
    }

    public Person[] getPersons() {
        return idPersonMap.values().toArray(new Person[0]);
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (containsPerson(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        } else {
            blockSum++;
            idPersonMap.put(person.getId(), person);
        }
    }

    @Override
    public void addRelation(
            int id1,
            int id2,
            int value) throws PersonIdNotFoundException, EqualRelationException {
        Person person1 = getPerson(id1);
        Person person2 = getPerson(id2);

        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (person1.isLinked(person2)) {
            throw new MyEqualRelationException(id1, id2);
        } else {
            reCalc = true;
            ((MyPerson) person1).addAcquaintance(person2, value);
            ((MyPerson) person2).addAcquaintance(person1, value);
            HashSet<Person> person1Acquaintances = ((MyPerson) person1).getAcquaintances();
            HashSet<Person> person2Acquaintances = ((MyPerson) person2).getAcquaintances();
            HashSet<Person> person1AcquaintancesCopy = new HashSet<>(person1Acquaintances);
            person1AcquaintancesCopy.retainAll(person2Acquaintances);
            triples += person1AcquaintancesCopy.size();
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualPersonIdException, RelationNotFoundException {
        Person person1 = getPerson(id1);
        Person person2 = getPerson(id2);

        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (person1.equals(person2)) {
            throw new MyEqualPersonIdException(id1);
        } else if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        } else if (person1.queryValue(person2) + value <= 0) {
            reCalc = true;
            ((MyPerson) person1).deleteAcquaintance(person2);
            ((MyPerson) person2).deleteAcquaintance(person1);
            HashSet<Person> person1Acquaintances = ((MyPerson) person1).getAcquaintances();
            HashSet<Person> person2Acquaintances = ((MyPerson) person2).getAcquaintances();
            HashSet<Person> person1AcquaintancesCopy = new HashSet<>(person1Acquaintances);
            person1AcquaintancesCopy.retainAll(person2Acquaintances);
            triples -= person1AcquaintancesCopy.size();
        } else {
            ((MyPerson) person1).addAcquaintance(person2, person1.queryValue(person2) + value);
            ((MyPerson) person2).addAcquaintance(person1, person2.queryValue(person1) + value);
        }
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            Person person1 = getPerson(id1);
            Person person2 = getPerson(id2);
            if (!person1.isLinked(person2)) {
                throw new MyRelationNotFoundException(id1, id2);
            } else {
                return person1.queryValue(person2);
            }
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        Person person1 = getPerson(id1);
        Person person2 = getPerson(id2);

        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            HashSet<Person> person1Block = new HashSet<>();
            ((MyPerson) person1).getBlock(person1Block);
            return person1Block.contains(person2);
        }
    }

    @Override
    public int queryBlockSum() {
        if (reCalc) {
            HashSet<Person> vis = new HashSet<>();
            int res = 0;
            for (Person person : idPersonMap.values()) {
                if (!vis.contains(person)) {
                    res++;
                    ((MyPerson) person).getBlock(vis);
                }
            }
            blockSum = res;
            reCalc = false;
            return res;
        }
        return blockSum;
    }

    @Override
    public int queryTripleSum() {
        return triples;
    }
}
