public class User implements Observer {

    final private String name;

    public User (String name) {
        this.name = name;
    }

    @Override
    public void update(String msg) {
        System.out.println(name + ": " + msg);
    }
}
