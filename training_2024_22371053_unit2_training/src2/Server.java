import java.util.ArrayList;

public class Server implements Observerable {

    final ArrayList<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(String msg) {
        System.out.println("server: " + msg);
        for (Observer observer : observers) {
            observer.update(msg);
        }
    }
}
