/*
实现一个简单的生产者消费者模型，要求如下：
托盘容量为1。
Producer生产10个货物，每生产一个货物后会立刻尝试放入，放入成功前不会继续生产，货物按照从1-10编号。
Producer在向托盘中成功放入货物后需要sleep 0-100ms，可用sleep((int)(Math.random() * 100))实现。
Consumer只能取托盘中现有的货物。
Producer在向托盘中放入货物时需输出 "Producer put:" + 货物编号
Consumer在从托盘中取出货物时需输出 "Consumer get:" + 货物编号
*/

public class MainClass {

    public static void main(String[] args) {
        Plate plate = new Plate();

        Producer producer = new Producer(plate);
        Consumer consumer = new Consumer(plate);
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}
