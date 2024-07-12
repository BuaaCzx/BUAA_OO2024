import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;
import java.util.HashSet;

public class MyNetwork implements Network {

    private final HashMap<Integer, Person> idPersonMap = new HashMap<>();
    private final HashSet<Tag> tags = new HashSet<>();
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
            ((MyPerson) person1).addAcquaintance(person2, value, tags);
            ((MyPerson) person2).addAcquaintance(person1, value, tags);
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
            ((MyPerson) person1).deleteAcquaintance(person2, tags);
            ((MyPerson) person2).deleteAcquaintance(person1, tags);
            HashSet<Person> person1Acquaintances = ((MyPerson) person1).getAcquaintances();
            HashSet<Person> person2Acquaintances = ((MyPerson) person2).getAcquaintances();
            HashSet<Person> person1AcquaintancesCopy = new HashSet<>(person1Acquaintances);
            person1AcquaintancesCopy.retainAll(person2Acquaintances);
            triples -= person1AcquaintancesCopy.size();
        } else {
            ((MyPerson) person1).addAcquaintance(
                    person2, person1.queryValue(person2) + value, tags);
            ((MyPerson) person2).addAcquaintance(
                    person1, person2.queryValue(person1) + value, tags);
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

    @Override
    public void addTag(int personId, Tag tag)
            throws PersonIdNotFoundException, EqualTagIdException {
        Person person = getPerson(personId);
        if (person == null) {
            throw new MyPersonIdNotFoundException(personId);
        }
        if (person.containsTag(tag.getId())) {
            throw new MyEqualTagIdException(tag.getId());
        }
        person.addTag(tag);
        tags.add(tag);
    }

    @Override
    public void addPersonToTag(int personId1, int personId2, int tagId)
            throws PersonIdNotFoundException,
            RelationNotFoundException,
            TagIdNotFoundException,
            EqualPersonIdException {
        Person person1 = getPerson(personId1);
        Person person2 = getPerson(personId2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        if (person2 == null) {
            throw new MyPersonIdNotFoundException(personId2);
        }
        if (person1.getId() == person2.getId()) {
            throw new MyEqualPersonIdException(personId1);
        }
        if (!person2.isLinked(person1)) {
            throw new MyRelationNotFoundException(personId1, personId2);
        }
        Tag tag = person2.getTag(tagId);
        if (tag == null) {
            throw new MyTagIdNotFoundException(tagId);
        }
        if (tag.hasPerson(person1)) {
            throw new MyEqualPersonIdException(personId1);
        }
        if (tag.getSize() > 1111) {
            return; // 不执行添加操作
        }
        tag.addPerson(person1);
    }

    @Override public int queryTagValueSum(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        Person person = getPerson(personId);
        if (person == null) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Tag tag = person.getTag(tagId);
        if (tag == null) {
            throw new MyTagIdNotFoundException(tagId);
        }
        return tag.getValueSum();
    }

    @Override
    public int queryTagAgeVar(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        Person person = getPerson(personId);
        if (person == null) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Tag tag = person.getTag(tagId);
        if (tag == null) {
            throw new MyTagIdNotFoundException(tagId);
        }
        return tag.getAgeVar();
    }

    @Override
    public void delPersonFromTag(int personId1, int personId2, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        Person person1 = getPerson(personId1);
        Person person2 = getPerson(personId2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        if (person2 == null) {
            throw new MyPersonIdNotFoundException(personId2);
        }
        Tag tag = person2.getTag(tagId);
        if (tag == null) {
            throw new MyTagIdNotFoundException(tagId);
        }
        if (!tag.hasPerson(person1)) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        tag.delPerson(person1);
    }

    @Override
    public void delTag(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        Person person = getPerson(personId);
        if (person == null) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Tag tag = person.getTag(tagId);
        if (tag == null) {
            throw new MyTagIdNotFoundException(tagId);
        }
        person.delTag(tagId);
        tags.remove(tag);
    }

    @Override
    public int queryBestAcquaintance(int id)
            throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        Person person = getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        Integer bestAcquaintance = ((MyPerson) person).queryBestAcquaintance();
        if (bestAcquaintance == null) {
            throw new MyAcquaintanceNotFoundException(id);
        }
        return bestAcquaintance;
    }

    @Override
    public int queryCoupleSum() {
        int res = 0;
        for (Person person1 : idPersonMap.values()) {
            Integer bestAcquaintance1 = ((MyPerson) person1).queryBestAcquaintance();
            if (bestAcquaintance1 == null) {
                continue;
            }
            Person person2 = getPerson(bestAcquaintance1);
            Integer bestAcquaintance2 = ((MyPerson) person2).queryBestAcquaintance();
            if (person1.getId() == bestAcquaintance2) {
                res++;
            }
        }
        return res / 2;
    }

    @Override
    public int queryShortestPath(int id1, int id2)
            throws PersonIdNotFoundException, PathNotFoundException {
        Person person1 = getPerson(id1);
        Person person2 = getPerson(id2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        }
        int res = ((MyPerson) person1).queryShortestPath(id2);
        if (res == -1) {
            throw new MyPathNotFoundException(id1, id2);
        }
        return res - 1;
    }
}
