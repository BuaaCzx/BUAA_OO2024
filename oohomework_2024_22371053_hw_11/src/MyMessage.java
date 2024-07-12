import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyMessage implements Message {

    private int messageId;
    private int messageSocialValue;
    private Person messagePerson1;
    private Person messagePerson2;
    private Tag messageTag;
    private int type;

    public MyMessage(int messageId,
                     int messageSocialValue,
                     Person messagePerson1,
                     Person messagePerson2) {
        this.messageId = messageId;
        this.messageSocialValue = messageSocialValue;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = messagePerson2;
        messageTag = null;
        this.type = 0;
    }

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1, Tag messageTag) {
        this.messageId = messageId;
        this.messageSocialValue = messageSocialValue;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = null;
        this.messageTag = messageTag;
        this.type = 1;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return messageId;
    }

    @Override
    public int getSocialValue() {
        return messageSocialValue;
    }

    @Override
    public Person getPerson1() {
        return messagePerson1;
    }

    @Override
    public Person getPerson2() {
        return messagePerson2;
    }

    @Override
    public Tag getTag() {
        return messageTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Message)) {
            return false;
        }
        System.out.println("Compare : " + messageId + " " + ((Message) obj).getId());
        Message other = (Message) obj;
        return messageId == other.getId();
    }
}
