public class Consumer implements Runnable {

    final private Plate plate;

    public Consumer(Plate plate) {
        this.plate = plate;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (plate) {
                while (plate.isEmpty()) {
                    try {
                        plate.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Consumer get:" + plate.getId());
                plate.setEmpty(true);
                plate.notifyAll();
            }
        }
    }
}
