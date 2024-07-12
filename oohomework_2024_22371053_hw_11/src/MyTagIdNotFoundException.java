import com.oocourse.spec3.exceptions.TagIdNotFoundException;

import java.util.HashMap;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private int id;
    private static ExceptionCounter counter = new ExceptionCounter();
    private static HashMap<Integer, ExceptionCounter> idCounterMap =
            new HashMap<Integer, ExceptionCounter>();

    public MyTagIdNotFoundException(int id) {
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
        System.out.println("tinf-" + totCnt + ", " + id + "-" + idCnt);
    }
}