import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.EqualTagIdException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.TagIdNotFoundException;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Tag;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> idPersonMap = new HashMap<>();
    private final HashSet<Tag> tags = new HashSet<>();
    private int triples = 0;
    private boolean reCalc = true;
    private int blockSum = 0;
    private final HashMap<Integer, Message> idMessageMap = new HashMap<>();
    private final HashMap<Integer, Integer> emojiHeatMap = new HashMap<>();
    private int[] emojiHeatList;

    public MyNetwork() {}

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
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
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
            ((MyPerson) person1).addAcquaintance(person2, value, idPersonMap.values());
            ((MyPerson) person2).addAcquaintance(person1, value, idPersonMap.values());
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
            ((MyPerson) person1).deleteAcquaintance(person2, idPersonMap.values());
            ((MyPerson) person2).deleteAcquaintance(person1, idPersonMap.values());
            HashSet<Person> person1Acquaintances = ((MyPerson) person1).getAcquaintances();
            HashSet<Person> person2Acquaintances = ((MyPerson) person2).getAcquaintances();
            HashSet<Person> person1AcquaintancesCopy = new HashSet<>(person1Acquaintances);
            person1AcquaintancesCopy.retainAll(person2Acquaintances);
            triples -= person1AcquaintancesCopy.size();
        } else {
            ((MyPerson) person1).addAcquaintance(
                    person2, person1.queryValue(person2) + value, idPersonMap.values());
            ((MyPerson) person2).addAcquaintance(
                    person1, person2.queryValue(person1) + value, idPersonMap.values());
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
            throws PersonIdNotFoundException, RelationNotFoundException,
            TagIdNotFoundException, EqualPersonIdException {
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
    public boolean containsMessage(int id) {
        return idMessageMap.containsKey(id);
    }

    @Override
    public void addMessage(Message message)
            throws EqualMessageIdException, EmojiIdNotFoundException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message instanceof EmojiMessage
                && !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
        }
        if (message.getType() == 0 && message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        idMessageMap.put(message.getId(), message);
    }

    @Override
    public Message getMessage(int id) {
        return idMessageMap.getOrDefault(id, null);
    }

    @Override
    public void sendMessage(int id)
            throws RelationNotFoundException, MessageIdNotFoundException, TagIdNotFoundException {
        Message message = getMessage(id);
        if (message == null) {
            throw new MyMessageIdNotFoundException(id);
        }
        if (message.getType() == 0 && !message.getPerson1().isLinked(message.getPerson2())) {
            throw new MyRelationNotFoundException(
                    message.getPerson1().getId(), message.getPerson2().getId());
        }
        if (message.getType() == 1 && !message.getPerson1().containsTag(message.getTag().getId())) {
            throw new MyTagIdNotFoundException(message.getTag().getId());
        }
        idMessageMap.remove(id);
        MyPerson person1 = (MyPerson) message.getPerson1();
        person1.addSocialValue(message.getSocialValue());
        if (message.getType() == 0) {
            MyPerson person2 = (MyPerson) message.getPerson2();
            person2.addSocialValue(message.getSocialValue());
            if (message instanceof RedEnvelopeMessage) {
                person1.addMoney(-((RedEnvelopeMessage) message).getMoney());
                person2.addMoney(((RedEnvelopeMessage) message).getMoney());
            } else if (message instanceof EmojiMessage) {
                int emojiId = ((EmojiMessage) message).getEmojiId();
                int oldHeat = emojiHeatMap.get(emojiId);
                emojiHeatMap.put(emojiId, oldHeat + 1);
            }
            person2.addMessage(message);
        } else if (message.getType() == 1) {
            for (Person person2 : ((MyTag)message.getTag()).getPersons()) {
                person2.addSocialValue(message.getSocialValue());
            }
            if (message instanceof RedEnvelopeMessage && message.getTag().getSize() > 0) {
                int sz = ((MyTag)message.getTag()).getPersons().size();
                int i = ((RedEnvelopeMessage)message).getMoney() / sz;
                person1.addMoney(-i * sz);
                for (Person person2 : ((MyTag)message.getTag()).getPersons()) {
                    person2.addMoney(i);
                }
            } else if (message instanceof EmojiMessage) {
                int emojiId = ((EmojiMessage) message).getEmojiId();
                int oldHeat = emojiHeatMap.get(emojiId);
                emojiHeatMap.put(emojiId, oldHeat + 1);
            }
        }

    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        Person person = getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        return person.getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        Person person = getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        return person.getReceivedMessages();
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiHeatMap.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiHeatMap.put(id, 0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        Person person = getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        return person.getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiHeatMap.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        HashSet<Integer> deleteEmojiIds = new HashSet<>();
        HashSet<Integer> deleteMessageIds = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : emojiHeatMap.entrySet()) {
            if (entry.getValue() < limit) {
                deleteEmojiIds.add(entry.getKey());
            }
        }
        for (Integer id : deleteEmojiIds) {
            emojiHeatMap.remove(id);
        }
        for (Map.Entry<Integer, Message> entry : idMessageMap.entrySet()) {
            if (entry.getValue() instanceof EmojiMessage
                    && !containsEmojiId(((EmojiMessage) entry.getValue()).getEmojiId())) {
                deleteMessageIds.add(entry.getKey());
            }
        }
        for (Integer id : deleteMessageIds) {
            idMessageMap.remove(id);
        }
        return emojiHeatMap.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        Person person = getPerson(personId);
        if (person == null) {
            throw new MyPersonIdNotFoundException(personId);
        }
        ((MyPerson) person).clearNotices();
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

    public Message[] getMessages() {
        return idMessageMap.values().toArray(new Message[0]);
    }

    public int[] getEmojiIdList() {
        int[] emojiIds = new int[emojiHeatMap.size()];
        emojiHeatList = new int[emojiHeatMap.size()];
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : emojiHeatMap.entrySet()) {
            emojiIds[index] = entry.getKey();
            emojiHeatList[index] = entry.getValue();
            index++;
        }
        return emojiIds;
    }

    public int[] getEmojiHeatList() { return emojiHeatList; }
}