import com.oocourse.spec2.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private int id1;
    private int id2;
    private static ExceptionCounter counter = new ExceptionCounter();
    private static HashMap<Integer, ExceptionCounter> idCounterMap =
            new HashMap<Integer, ExceptionCounter>();

    public MyEqualRelationException(int id1, int id2) {
        this.id1 = Math.min(id1, id2);
        this.id2 = Math.max(id1, id2);

        counter.incrementCount();

        if (!idCounterMap.containsKey(id1)) {
            idCounterMap.put(id1, new ExceptionCounter());
        }
        if (!idCounterMap.containsKey(id2)) {
            idCounterMap.put(id2, new ExceptionCounter());
        }

        if (id1 != id2) {
            idCounterMap.get(id1).incrementCount();
            idCounterMap.get(id2).incrementCount();
        } else {
            idCounterMap.get(id1).incrementCount();
        }
    }

    @Override
    public void print() {
        // er-x, id1-y, id2-z
        int totCnt = counter.getCount();
        int id1Cnt = idCounterMap.get(id1).getCount();
        int id2Cnt = idCounterMap.get(id2).getCount();

        System.out.println("er-" + totCnt + ", " + id1 + "-" + id1Cnt +
                ", " + id2 + "-" + id2Cnt);
    }
}
