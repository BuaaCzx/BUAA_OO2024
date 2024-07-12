import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {

    private int id;
    private static ExceptionCounter counter = new ExceptionCounter();
    private static HashMap<Integer, ExceptionCounter> idCounterMap =
            new HashMap<Integer, ExceptionCounter>();

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;

        counter.incrementCount();

        if (!idCounterMap.containsKey(id)) {
            idCounterMap.put(id, new ExceptionCounter());
        }

        idCounterMap.get(id).incrementCount();
    }

    @Override
    public void print() {
        // anf-x, id-y
        int totCnt = counter.getCount();
        int idCnt = idCounterMap.get(id).getCount();
        System.out.println("anf-" + totCnt + ", " + id + "-" + idCnt);
    }
}