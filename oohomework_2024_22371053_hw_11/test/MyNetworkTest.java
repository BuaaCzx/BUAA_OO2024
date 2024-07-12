import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

import java.util.Random;

import static org.junit.Assert.*;

public class MyNetworkTest {

    private MyNetwork network;
    private MyNetwork networkCopy;
    private Random rand = new Random();

    private void generateNetwork() {
        network = new MyNetwork();
        networkCopy = new MyNetwork();

        for (int i = 0; i < 50; i++) {
            try {
                network.addPerson(new MyPerson(i, "name" + i, 50));
                networkCopy.addPerson(new MyPerson(i, "name" + i, 50));
            } catch (Exception e) {
            }
        }

        int emojiSize = 20;
        for (int i = 0; i < emojiSize; i++) {
            try {
                network.storeEmojiId(i);
                networkCopy.storeEmojiId(i);
            } catch (Exception e) {
            }
        }

        int messageSize = 1000;

        for (int i = 0; i < messageSize; i++) {
            int person1 = rand.nextInt(50);
            int person2 = rand.nextInt(50);
            int emoji = rand.nextInt(emojiSize);
            try {
                network.addRelation(person1, person2, 1);
                networkCopy.addRelation(person1, person2, 1);
            } catch (Exception e) {

            }
            try {
                network.addMessage(new MyEmojiMessage(i, emoji, network.getPerson(person1), network.getPerson(person2)));
                networkCopy.addMessage(new MyEmojiMessage(i, emoji, networkCopy.getPerson(person1), networkCopy.getPerson(person2)));
                if (rand.nextBoolean()) {
                    network.sendMessage(i);
                    networkCopy.sendMessage(i);
                }
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < messageSize / 10; i++) {
            int person1 = rand.nextInt(50);
            int person2 = rand.nextInt(50);
            try {
                network.addRelation(person1, person2, 1);
                networkCopy.addRelation(person1, person2, 1);
            } catch (Exception e) {

            }
            try {
                network.addMessage(new MyNoticeMessage(i + 100000, "111", network.getPerson(person1), network.getPerson(person2)));
                networkCopy.addMessage(new MyNoticeMessage(i + 100000, "111", networkCopy.getPerson(person1), networkCopy.getPerson(person2)));
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < messageSize / 10; i++) {
            int person1 = rand.nextInt(50);
            int person2 = rand.nextInt(50);
            try {
                network.addRelation(person1, person2, 1);
                networkCopy.addRelation(person1, person2, 1);
            } catch (Exception e) {

            }
            try {
                network.addMessage(new MyRedEnvelopeMessage(i + 500000, 111, network.getPerson(person1), network.getPerson(person2)));
                networkCopy.addMessage(new MyRedEnvelopeMessage(i + 500000, 111, networkCopy.getPerson(person1), networkCopy.getPerson(person2)));
            } catch (Exception e) {
            }
        }

        for (int i = 1000; i < messageSize * 2; i++) {
            int person1 = rand.nextInt(50);
            int person2 = rand.nextInt(50);
            int emoji = rand.nextInt(emojiSize);
            try {
                network.addRelation(person1, person2, 1);
                networkCopy.addRelation(person1, person2, 1);
            } catch (Exception e) {

            }
            try {
                network.addMessage(new MyEmojiMessage(i, emoji, network.getPerson(person1), network.getPerson(person2)));
                networkCopy.addMessage(new MyEmojiMessage(i, emoji, networkCopy.getPerson(person1), networkCopy.getPerson(person2)));
                if (rand.nextBoolean()) {
                    network.sendMessage(i);
                    networkCopy.sendMessage(i);
                }
            } catch (Exception e) {
            }
        }

    }

    @org.junit.Test
    public void testDeleteColdEmoji() {
        int testSize = 100;
        for (int _ = 0; _ < testSize; _++) {
            generateNetwork();
            int limit = _ * 3;
            int res = network.deleteColdEmoji(limit);
            int[] oldEmojiId = networkCopy.getEmojiIdList();
            int[] oldEmojiHeat = networkCopy.getEmojiHeatList();
            int[] emojiId = network.getEmojiIdList();
            int[] emojiHeat = network.getEmojiHeatList();
            Message[] messages = network.getMessages();
            Message[] oldMessages = networkCopy.getMessages();



            /*
            @ ensures (\forall int i; 0 <= i && i < \old(emojiIdList.length);
      @          (\old(emojiHeatList[i] >= limit) ==>
      @          (\exists int j; 0 <= j && j < emojiIdList.length; emojiIdList[j] == \old(emojiIdList[i]))));
             */
            for (int i = 0; i < oldEmojiId.length; i++) {
                if (oldEmojiHeat[i] >= limit) {
                    boolean find = false;
                    for (int j = 0; j < emojiId.length; j++) {
                        if (emojiId[j] == oldEmojiId[i]) {
                            find = true;
                            break;
                        }
                    }
                    assertTrue(find);
                }
            }

            /*
            @ ensures (\forall int i; 0 <= i && i < emojiIdList.length;
      @          (\exists int j; 0 <= j && j < \old(emojiIdList.length);
      @          emojiIdList[i] == \old(emojiIdList[j]) && emojiHeatList[i] == \old(emojiHeatList[j])));
             */

            for (int i = 0; i < emojiId.length; i++) {
                boolean find = false;
                for (int j = 0; j < oldEmojiId.length; j++) {
                    if (emojiId[i] == oldEmojiId[j] && emojiHeat[i] == oldEmojiHeat[j]) {
                        find = true;
                        break;
                    }
                }
                assertTrue(find);
            }

            /*
            @ ensures emojiIdList.length ==
      @          (\num_of int i; 0 <= i && i < \old(emojiIdList.length); \old(emojiHeatList[i] >= limit));
             */
            int lenExp = 0;
            for (int i = 0; i < oldEmojiId.length; i++) {
                if (oldEmojiHeat[i] >= limit) {
                    lenExp++;
                }
            }
            assertEquals(lenExp, emojiId.length);

            /*
            @ ensures emojiIdList.length == emojiHeatList.length;
             */
            assertEquals(emojiId.length, emojiHeat.length);

            /*
            @ ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage &&
      @           containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))  ==> \not_assigned(\old(messages[i])) &&
      @           (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))));
             */
//            for (Message message : oldMessages) {
//                System.err.println(message.getId());
//            }
//            System.err.println("-------------------");
//            for (Message message : messages) {
//                System.err.println(message.getId());
//            }
            for (int i = 0; i < oldMessages.length; i++) {
                if (oldMessages[i] instanceof EmojiMessage && network.containsEmojiId(((EmojiMessage) oldMessages[i]).getEmojiId())) {
                    assertTrue(messageEquals(oldMessages[i], networkCopy.getMessage(oldMessages[i].getId())));
                    boolean find = false;
//                    System.out.println("Start find : " + oldMessages[i].getId() + " " + messages.length);
                    for (int j = 0; j < messages.length; j++) {
                        if ((messages[j].getId()) == (oldMessages[i].getId())) {
                            find = true;
                            break;
                        }
                    }
                    assertTrue(find);
                }
            }

            for (int i = 0; i < oldMessages.length; i++) {
                if (!(oldMessages[i] instanceof EmojiMessage)) {
                    assertTrue(messageEquals(oldMessages[i], networkCopy.getMessage(oldMessages[i].getId())));
                    boolean find = false;
                    for (int j = 0; j < messages.length; j++) {
                        if (messages[j].getId() == (oldMessages[i].getId())) {
                            find = true;
                            break;
                        }
                    }
                    assertTrue(find);
                }
            }

            /*
                  @ ensures messages.length == (\num_of int i; 0 <= i && i < \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage) ==>
      @           (containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))));
             */
            int mesLenExp = 0;
            for (int i = 0; i < oldMessages.length; i++) {
                if (!(oldMessages[i] instanceof EmojiMessage)) {
                    mesLenExp++;
                } else if (network.containsEmojiId(((EmojiMessage) oldMessages[i]).getEmojiId())) {
                    mesLenExp++;
                }
            }
            assertEquals(mesLenExp, messages.length);
            assertEquals(res, emojiId.length);


        }

    }

    private boolean messageEquals(Message m1, Message m2) {
        if (m1 == null || m2 == null) {
            return false;
        }
        return m1.getId() == m2.getId()
                && (m1.getTag() == null || (m1.getTag() != null && m1.getTag().equals(m2.getTag())))
                && m1.getPerson1().equals(m2.getPerson1())
                && (m1.getPerson2() == null || (m1.getPerson2() != null && m1.getPerson2().equals(m2.getPerson2())))
                && m1.getType() == m2.getType();
    }

}