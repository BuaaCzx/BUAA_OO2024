import com.oocourse.library1.LibraryMoveInfo;

import java.util.ArrayList;

public class BorrowReturnOffice {
    private final ArrayList<Book> books = new ArrayList<>();
    private final Bookshelf bookshelf;

    public BorrowReturnOffice(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public ArrayList<LibraryMoveInfo> moveBook2Bookshelf() {
        ArrayList<LibraryMoveInfo> res = new ArrayList<>();
        for (Book book : books) {
            res.add(new LibraryMoveInfo(book.getId(), "bro", "bs"));
            bookshelf.addBook(book);
        }
        books.clear();
        return res;
    }
}
