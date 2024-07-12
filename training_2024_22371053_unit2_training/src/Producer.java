public class Producer implements Runnable {

    final private Plate plate;

    public Producer(Plate plate) {
        this.plate = plate;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            synchronized (plate) {
                while (!plate.isEmpty()) {
                    try {
                        plate.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                plate.setId(i);
                plate.setEmpty(false);
                System.out.println("Producer put:" + i);
                plate.notifyAll();
                try {
                    Thread.sleep((int) Math.random() * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
