import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class MyNetworkTest {

    private MyNetwork network;
    private MyNetwork networkCopy;
    private ArrayList<Person> personList;
    private Random random = new Random();

    @Before
    public void setUp() {
        network = new MyNetwork();
        networkCopy = new MyNetwork();
        personList = new ArrayList<>();
    }

    @org.junit.Test
    public void queryCoupleSum() {
        for (int i = 0; i < 150; i++) {
            setUp();
            if (i == 0) {
                generateCompleteGraph(1);
            } else if (i == 1) {
                generatePointGraph(1);
            } else if (i == 2) {
                circleGraph(1);
            }else if (i % 7 == 0) {
                generateCompleteGraph(200);
            } else if (i % 7 == 1) {
                generateRandomGraph();
            } else if (i % 7 == 2) {
                generateRandomGraph2(100, 200);
            } else if (i % 7 == 3){
                generateRandomGraph2(100, 3);
            } else if (i % 7== 4) {
                jvhuaGraph(10);
            } else if (i % 7 == 5) {
                generateCompleteGraph(5);
            } else if (i % 7 == 6) {
                circleGraph(3);
            }
            assertEquals(network.queryCoupleSum(), getCoupleSum());
            // System.err.println(getCoupleSum());
            assertEquals(network.getPersons().length, networkCopy.getPersons().length);
            boolean changed = false;
            for (int j = 0; j < 100; j++) {
                if (!((MyPerson) network.getPerson(j)).strictEquals(networkCopy.getPerson(j))) {
                    changed = true;
                }
            }
            assertFalse(changed);
        }
    }

    private void generateRandomGraph() {
//        Random random = new Random();
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            Person person = new MyPerson(i, "person" + i, random.nextInt(30) + 30);
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person); // 添加节点到网络中
                networkCopy.addPerson(personCopy);
                personList.add(person);
            } catch (EqualPersonIdException e) {
                // e.printStackTrace();
            }
        }

        for (int i = 0; i < numNodes / (random.nextInt(10) + 1); i++) {
            for (int j = i + 1; j < numNodes / (random.nextInt(10) + 1); j++) {
                int value = random.nextInt(10) + 1;
                try {
                    int id1 = random.nextInt(100);
                    int id2 = random.nextInt(100);
                    network.addRelation(id1, id2, value);
                    networkCopy.addRelation(id1, id2, value);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                }
            }
        }
    }

    public void generateRandomGraph2(int numPersons, int value) {
//        Random random = new Random();
        for (int i = 0; i < numPersons; i++) {
            Person person = new MyPerson(i, "Person" + i, 20 + random.nextInt(40));
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person);
                personList.add(person);
                networkCopy.addPerson(personCopy);
            } catch (EqualPersonIdException e) {
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 50; j++) {
                int id1 = i * 10 + random.nextInt(10);
                int id2 = i * 10 + random.nextInt(10);

                if (id1 != id2) {
                    try {
                        int val = random.nextInt(value) + 1;
                        network.addRelation(personList.get(id1).getId(), personList.get(id2).getId(), val);
                        networkCopy.addRelation(personList.get(id1).getId(), personList.get(id2).getId(), val);
                    } catch (PersonIdNotFoundException | EqualRelationException e) {
                    }
                }
            }
        }
    }

    private void generateCompleteGraph(int val) {
//        Random random = new Random();
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            Person person = new MyPerson(i, "person" + i, random.nextInt(30) + 30);
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person);
                networkCopy.addPerson(personCopy);
                personList.add(person);
            } catch (EqualPersonIdException e) {
                // e.printStackTrace();
            }
        }

        for (int i = 0; i < numNodes; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                int value = random.nextInt(val) + 1;
                try {
                    network.addRelation(i, j, value);
                    networkCopy.addRelation(i, j, value);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    private void generatePointGraph(int val) {
//        Random random = new Random();
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            Person person = new MyPerson(i, "person" + i, random.nextInt(30) + 30);
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person);
                networkCopy.addPerson(personCopy);
                personList.add(person);
            } catch (EqualPersonIdException e) {
                // e.printStackTrace();
            }
        }
    }

    private void jvhuaGraph(int val) {
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            Person person = new MyPerson(i, "person" + i, random.nextInt(30) + 30);
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person);
                networkCopy.addPerson(personCopy);
                personList.add(person);
            } catch (EqualPersonIdException e) {
                // e.printStackTrace();
            }
        }
        for (int i = 1; i < 100; i++) {
            int value = random.nextInt(val) + 1;
            try {
                network.addRelation(0, i, value); // 中心节点与其他所有节点相连
                networkCopy.addRelation(0, i, value);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                // e.printStackTrace();
            }
        }
    }

    private void circleGraph(int val) {
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            Person person = new MyPerson(i, "person" + i, random.nextInt(30) + 30);
            Person personCopy = new MyPerson(person.getId(), person.getName(), person.getAge());
            try {
                network.addPerson(person);
                networkCopy.addPerson(personCopy);
                personList.add(person);
            } catch (EqualPersonIdException e) {
                // e.printStackTrace();
            }
        }
        for (int i = 0; i < numNodes; i++) {
            int value = random.nextInt(val) + 1;
            try {
                network.addRelation(i, (i + 1) % numNodes, value); // 每个节点与相邻节点相连
                networkCopy.addRelation(i, (i + 1) % numNodes, value);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                // e.printStackTrace();
            }
        }
    }

    private int getCoupleSum() {
        int res = 0;
        for (int i = 0; i < personList.size(); i++) {
            for (int j = i + 1; j < personList.size(); j++) {
                Person person1 = personList.get(i);
                Person person2 = personList.get(j);
                try {
                    int best1 = network.queryBestAcquaintance(person1.getId());
                    int best2 = network.queryBestAcquaintance(person2.getId());
                    if (best1 == person2.getId() && best2 == person1.getId()) {
                        res++;
                    }
                } catch (AcquaintanceNotFoundException | PersonIdNotFoundException e) {
                    // throw new RuntimeException(e);
                }
            }
        }
        return res;
    }


}