import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyEmojiMessage implements EmojiMessage {

    private int messageId;
    private int emojiNumber;
    private Person messagePerson1;
    private Person messagePerson2;
    private Tag messageTag;
    private int type;

    public MyEmojiMessage(int messageId,
                          int emojiNumber,
                          Person messagePerson1,
                          Person messagePerson2) {
        this.messageId = messageId;
        this.emojiNumber = emojiNumber;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = messagePerson2;
        this.messageTag = null;
        this.type = 0;
    }

    public MyEmojiMessage(int messageId, int emojiNumber, Person messagePerson1, Tag messageTag) {
        this.messageId = messageId;
        this.emojiNumber = emojiNumber;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = null;
        this.messageTag = messageTag;
        this.type = 1;
    }

    @Override
    public int getEmojiId() {
        return emojiNumber;
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
        return emojiNumber;
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
}
