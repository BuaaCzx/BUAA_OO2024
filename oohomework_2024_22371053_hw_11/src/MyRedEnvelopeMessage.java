import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Tag;

public class MyRedEnvelopeMessage implements RedEnvelopeMessage {

    private int messageId;
    private int luckyMoney;
    private Person messagePerson1;
    private Person messagePerson2;
    private Tag messageTag;
    private int type;

    public MyRedEnvelopeMessage(int messageId,
                                int luckyMoney,
                                Person messagePerson1,
                                Person messagePerson2) {
        this.messageId = messageId;
        this.luckyMoney = luckyMoney;
        this.messagePerson1 = messagePerson1;
        this.messagePerson2 = messagePerson2;
        this.messageTag = null;
        this.type = 0;
    }

    public MyRedEnvelopeMessage(int messageId,
                                int luckyMoney,
                                Person messagePerson1,
                                Tag messageTag) {
        this.messageId = messageId;
        this.luckyMoney = luckyMoney;
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
        return luckyMoney * 5;
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
    public int getMoney() {
        return luckyMoney;
    }
}
