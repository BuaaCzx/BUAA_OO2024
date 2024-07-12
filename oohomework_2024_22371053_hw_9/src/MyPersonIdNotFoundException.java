import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {

    private int id;
    private static ExceptionCounter counter = new ExceptionCounter();
    private static HashMap<Integer, ExceptionCounter> idCounterMap =
            new HashMap<Integer, ExceptionCounter>();

    public MyPersonIdNotFoundException(int id) {
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
        System.out.println("pinf-" + totCnt + ", " + id + "-" + idCnt);
    }
}
