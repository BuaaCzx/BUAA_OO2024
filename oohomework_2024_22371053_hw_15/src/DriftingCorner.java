import com.oocourse.library3.LibraryBookId;

import java.util.ArrayList;

public class DriftingCorner {

    private ArrayList<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBook(LibraryBookId id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public int queryBookCount(LibraryBookId bookId) {
        int res = 0;
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
                res++;
            }
        }
        return res;
    }
}
