import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyNoticeMessage implements NoticeMessage {

    private int messageId;
    private String noticeString;
    private Person messagePerson1;
    private Person messagePerson2;
    private Tag messageTag;
    private int type;

    public MyNoticeMessage(int messageId,
                           String noticeString,
                           Person messagePerson1,
                           Person messagePerson2) {
        this.messageId = messageId;
        this.noticeString = noticeString;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = messagePerson2;
        this.messageTag = null;
        this.type = 0;
    }

    public MyNoticeMessage(int messageId,
                           String noticeString,
                           Person messagePerson1,
                           Tag messageTag) {
        this.messageId = messageId;
        this.noticeString = noticeString;
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
        return noticeString.length();
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
    public String getString() {
        return noticeString;
    }
}
