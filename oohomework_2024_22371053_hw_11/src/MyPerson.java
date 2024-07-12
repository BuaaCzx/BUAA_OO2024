import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MyPerson implements Person {

    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Person, Integer> acquaintanceValueMap;
    private final HashMap<Integer, Tag> tags;
    private final PriorityQueue<MyEdge> edgesPriorityQueue = new PriorityQueue<>((o1, o2) -> {
        if (o1.getValue() != o2.getValue()) {
            return Integer.compare(o2.getValue(), o1.getValue());
        }
        return Integer.compare(o1.getPersonId(), o2.getPersonId());
    });
    private final HashMap<Integer, Message> messagesMap;
    private final LinkedList<Message> messagesList;
    private int socialValue = 0;
    private int money = 0;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        acquaintanceValueMap = new HashMap<>();
        tags = new HashMap<>();
        messagesMap = new HashMap<>();
        messagesList = new LinkedList<>();
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

    public Collection<Tag> getTags() {
        return tags.values();
    }

    @Override
    public boolean containsTag(int id) {
        return tags.containsKey(id);
    }

    @Override
    public Tag getTag(int id) {
        return tags.getOrDefault(id, null);
    }

    @Override
    public void addTag(Tag tag) {
        if (!containsTag(tag.getId())) {
            tags.put(tag.getId(), tag);
        }
    }

    @Override
    public void delTag(int id) {
        if (containsTag(id)) {
            tags.remove(id);
        }
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

    // TODO:---------

    @Override
    public void addSocialValue(int num) {
        socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return messagesList;
    }

    @Override
    public List<Message> getReceivedMessages() {
        int size = Math.min(messagesList.size(), 5);
        return messagesList.subList(0, size);
    }

    public void clearNotices() {
        messagesList.removeIf(message -> message instanceof NoticeMessage);
    }

    public void addMessage(Message message) {
        messagesList.addFirst(message);
    }

    @Override
    public void addMoney(int num) {
        money += num;
    }

    @Override
    public int getMoney() {
        return money;
    }

    public void addAcquaintance(Person person, int value, Collection<Person> persons) {
        for (Person person1 : persons) {
            for (Tag tag : ((MyPerson) person1).getTags()) {
                ((MyTag) tag).addRelation(person, this, value);
            }
        }

        acquaintanceValueMap.put(person, value);
        edgesPriorityQueue.add(new MyEdge(person, value));
    }

    public void deleteAcquaintance(Person person, Collection<Person> persons) {
        for (Person person1 : persons) {
            for (Tag tag : ((MyPerson) person1).getTags()) {
                ((MyTag) tag).addRelation(person, this, 0);
            }
        }
        acquaintanceValueMap.remove(person);
        for (Tag tag : tags.values()) {
            tag.delPerson(person);
        }
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

    public void updatePriorityQueue() {
        while (!edgesPriorityQueue.isEmpty()
                && queryValue(edgesPriorityQueue.peek().getPerson())
                != edgesPriorityQueue.peek().getValue()) {
            edgesPriorityQueue.poll();
        }
    }

    public Integer queryBestAcquaintance() {
        updatePriorityQueue();
        return edgesPriorityQueue.isEmpty() ? null : edgesPriorityQueue.peek().getPersonId();
    }

    public int queryShortestPath(int id) {
        if (id == this.id) {
            return 1;
        }
        HashSet<Person> visited = new HashSet<>();
        Queue<Person> queue = new LinkedList<>();
        Map<Integer, Integer> distance = new HashMap<>();

        queue.add(this);
        visited.add(this);
        distance.put(this.id, 0);

        while (!queue.isEmpty()) {
            Person currentPerson = queue.poll();
            int currentId = currentPerson.getId();
            for (Person acquaintance : ((MyPerson) currentPerson).acquaintanceValueMap.keySet()) {
                int acquaintanceId = acquaintance.getId();
                if (!visited.contains(acquaintance)) {
                    visited.add(acquaintance);
                    distance.put(acquaintanceId, distance.get(currentId) + 1);
                    queue.add(acquaintance);

                    if (acquaintanceId == id) {
                        return distance.get(acquaintanceId);
                    }
                }
            }
        }

        return -1;
    }

    public boolean strictEquals(Person person) {
        return true;
    }

}
