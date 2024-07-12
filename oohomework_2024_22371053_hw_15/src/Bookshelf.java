import com.oocourse.library3.LibraryBookId;

import java.util.ArrayList;

public class Bookshelf {

    private final ArrayList<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBook(LibraryBookId bookId) {
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
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
