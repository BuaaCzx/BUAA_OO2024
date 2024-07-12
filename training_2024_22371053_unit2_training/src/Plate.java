public class Plate {

    private boolean isEmpty;
    private int id;

    public Plate() {
        isEmpty = true;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public void setId(int id) {
        this.id = id;
    }

}
