import com.oocourse.library3.LibraryBookId;

import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.Math.min;

public class User {

    private final ArrayList<Book> books = new ArrayList<>();
    private final String id;
    private int creditScore = 10; // 信用分
    private final HashSet<LibraryBookId> appoitedBooks = new HashSet<>();

    public User(String id) {
        this.id = id;
    }

    public Book getBook(LibraryBookId bookId) {
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    /*
    A 类书无法借阅，顾客不应该带着A类书前往借还台
    B 类书：一人同一时刻仅能持有一个 B 类书的副本。
    C 类书：对于每一个书号，一人同一时刻仅能持有一个具有该书号的书籍副本。
    */
    public boolean canBorrow(LibraryBookId bookId) {
        if (creditScore < 0) {
            return false;
        }
        if (bookId.getType().equals(LibraryBookId.Type.A)) {
            // System.err.println("A 类书无法借阅，顾客不应该带着A类书前往借还台");
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.B)) {
            return !hasBookB();
        } else if (bookId.getType().equals(LibraryBookId.Type.C)) {
            return !hasBook(bookId);
        } else if (bookId.getType().equals(LibraryBookId.Type.BU)) {
            return !hasBookBU();
        } else if (bookId.getType().equals(LibraryBookId.Type.CU)) {
            return !hasBook(bookId);
        }
        System.err.println("不应该执行这一句");
        return false;
    }

    public boolean canPick(LibraryBookId bookId) {
        if (bookId.getType().equals(LibraryBookId.Type.A)) {
            // System.err.println("A 类书无法借阅，顾客不应该带着A类书前往借还台");
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.B)) {
            return !hasBookB();
        } else if (bookId.getType().equals(LibraryBookId.Type.C)) {
            return !hasBook(bookId);
        } else if (bookId.getType().equals(LibraryBookId.Type.BU)) {
            return !hasBookBU();
        } else if (bookId.getType().equals(LibraryBookId.Type.CU)) {
            return !hasBook(bookId);
        }
        System.err.println("不应该执行这一句");
        return false;
    }

    public boolean canOrder(LibraryBookId bookId) {
        if (creditScore < 0) {
            return false;
        }
        if (bookId.getType().equals(LibraryBookId.Type.A)) {
            // System.err.println("A 类书无法借阅，顾客不应该带着A类书前往借还台");
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.B)) {
            return !hasAppointedBookB() && !hasBookB();
        } else if (bookId.getType().equals(LibraryBookId.Type.C)) {
            return !hasAppointedBook(bookId) && !hasBook(bookId);
        } else if (bookId.getType().equals(LibraryBookId.Type.AU)) {
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.BU)) {
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.CU)) {
            return false;
        }
        System.err.println("不应该执行这一句");
        return false;
    }

    public boolean hasBookBU() {
        // 检查当前是否持有BU类书
        for (Book book : books) {
            if (book.getId().getType().equals(LibraryBookId.Type.BU)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBookB() { // 检查当前是否持有B类书
        for (Book book : books) {
            if (book.getType().equals(LibraryBookId.Type.B)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBook(LibraryBookId bookId) { // 检查当前是否持有bookId书
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
                return true;
            }
        }
        return false;
    }

    public void addCreditScore(int credits) {
        this.creditScore = min(credits + this.creditScore, 20);
        // System.err.println("+=" + credits + " cur=" + this.creditScore);
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void addAppointedBook(LibraryBookId bookId) {
        appoitedBooks.add(bookId);
    }

    public void removeAppointedBook(LibraryBookId bookId) {
        appoitedBooks.remove(bookId);
    }

    private boolean hasAppointedBook(LibraryBookId bookId) {
        for (LibraryBookId book : appoitedBooks) {
            if (book.equals(bookId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAppointedBookB() {
        for (LibraryBookId b : appoitedBooks) {
            if (b.getType().equals(LibraryBookId.Type.B)) {
                return true;
            }
        }
        return false;
    }

    public void orderNewBook() {

    }
}
