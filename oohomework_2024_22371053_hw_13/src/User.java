import com.oocourse.library1.LibraryBookId;

import java.util.ArrayList;

public class User {

    private final ArrayList<Book> books = new ArrayList<>();
    private final String id;

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
    public boolean canBorrowOrOrder(LibraryBookId bookId) {
        if (bookId.getType().equals(LibraryBookId.Type.A)) {
            // System.err.println("A 类书无法借阅，顾客不应该带着A类书前往借还台");
            return false;
        } else if (bookId.getType().equals(LibraryBookId.Type.B)) {
            return !hasBookB();
        } else if (bookId.getType().equals(LibraryBookId.Type.C)) {
            return !hasBook(bookId);
        }
        System.err.println("不应该执行这一句");
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
}
