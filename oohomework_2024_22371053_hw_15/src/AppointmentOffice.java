import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibraryReqCmd;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentOffice {
    private final HashMap<String, ArrayList<Book>> userIdBooks = new HashMap<>();

    private final ArrayList<LibraryReqCmd> requests = new ArrayList<>();

    private final Bookshelf bookshelf;

    public AppointmentOffice(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    public void addRequest(LibraryReqCmd request) {
        requests.add(request);
    }

    public Book getBook4User(String userId, LibraryBookId bookId) {
        if (!userIdBooks.containsKey(userId)) {
            return null;
        }
        ArrayList<Book> books = userIdBooks.get(userId);
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    public void removeBook(String userId, Book book) {
        userIdBooks.get(userId).remove(book);
    }

    public ArrayList<LibraryMoveInfo> moveBooks2bookshelf(
            LocalDate time, HashMap<String, User> users) {
        ArrayList<LibraryMoveInfo> res = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Book>> entry : userIdBooks.entrySet()) {
            User user = users.get(entry.getKey());
            ArrayList<Book> books = entry.getValue();
            ArrayList<Book> del = new ArrayList<>();
            for (Book book : books) {
                if (book.noTimeLeft(time)) {
                    user.addCreditScore(-3);
                    // System.err.println("appointment overdue -3, cur = " + user.getCreditScore());
                    res.add(new LibraryMoveInfo(book.getId(), "ao", "bs"));
                    del.add(book);
                    bookshelf.addBook(book);
                    user.removeAppointedBook(book.getId());
                }
            }
            books.removeAll(del);
        }
        return res;
    }

    public ArrayList<LibraryMoveInfo> handleRequests(LocalDate time) {
        ArrayList<LibraryMoveInfo> res = new ArrayList<>();
        for (int i = requests.size() - 1; i >= 0; i--) {
            LibraryReqCmd request = requests.get(i);
            LibraryBookId bookId = request.getBookId();
            String userId = request.getStudentId();
            Book book = bookshelf.getBook(bookId);
            if (book != null) { // 书架上有对应的书，那么就为这个用户预留，移动到预约处
                bookshelf.removeBook(book);
                userIdBooks.putIfAbsent(userId, new ArrayList<>());
                userIdBooks.get(userId).add(book); // 移动到预约处
                book.setAppointmentTime(time);
                res.add(new LibraryMoveInfo(bookId, "bs", "ao", userId));
                requests.remove(i);
            }
        }
        return res;
    }

    public void getOrderedBook() {

    }

}
