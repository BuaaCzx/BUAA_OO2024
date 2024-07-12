import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

import java.util.HashMap;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private int id;
    private static ExceptionCounter counter = new ExceptionCounter();
    private static HashMap<Integer, ExceptionCounter> idCounterMap =
            new HashMap<Integer, ExceptionCounter>();

    public MyMessageIdNotFoundException(int id) {
        this.id = id;

        counter.incrementCount();

        if (!idCounterMap.containsKey(id)) {
            idCounterMap.put(id, new ExceptionCounter());
        }

        idCounterMap.get(id).incrementCount();

    }

    @Override
    public void print() {
        // pinf-x, id-y
        int totCnt = counter.getCount();
        int idCnt = idCounterMap.get(id).getCount();
        System.out.println("minf-" + totCnt + ", " + id + "-" + idCnt);
    }
}
