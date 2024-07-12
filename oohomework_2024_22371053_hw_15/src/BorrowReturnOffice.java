import com.oocourse.library3.LibraryMoveInfo;

import java.util.ArrayList;

public class BorrowReturnOffice {
    private final ArrayList<Book> books = new ArrayList<>();
    private final Bookshelf bookshelf;
    private final DriftingCorner driftingCorner;

    public BorrowReturnOffice(Bookshelf bookshelf, DriftingCorner driftingCorner) {
        this.bookshelf = bookshelf;
        this.driftingCorner = driftingCorner;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public ArrayList<LibraryMoveInfo> moveBooks() {
        ArrayList<LibraryMoveInfo> res = new ArrayList<>();
        for (Book book : books) {
            if (book.isFormal()) {
                res.add(new LibraryMoveInfo(book.getId(), "bro", "bs"));
                bookshelf.addBook(book);
            } else {
                if (book.getReturnCount() >= 2) {
                    book.getDonor().addCreditScore(2);
                    Book bookNew = book.trans2FormalBook();
                    res.add(new LibraryMoveInfo(book.getId(), "bro", "bs"));
                    bookshelf.addBook(bookNew);
                } else {
                    res.add(new LibraryMoveInfo(book.getId(), "bro", "bdc"));
                    driftingCorner.addBook(book);
                }
            }
        }
        books.clear();
        return res;
    }
}
