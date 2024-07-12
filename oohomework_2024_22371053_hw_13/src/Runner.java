import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.oocourse.library1.LibrarySystem.PRINTER;
import static com.oocourse.library1.LibrarySystem.SCANNER;

public class Runner {
    private final Bookshelf bookshelf;
    private final AppointmentOffice appointmentOffice;
    private final BorrowReturnOffice borrowReturnOffice;
    private final HashMap<String, User> users;
    private LocalDate date;

    public Runner(
            Bookshelf bookshelf,
            AppointmentOffice appointmentOffice,
            BorrowReturnOffice borrowReturnOffice
    ) {
        this.bookshelf = bookshelf;
        this.appointmentOffice = appointmentOffice;
        this.borrowReturnOffice = borrowReturnOffice;
        this.users = new HashMap<>();
    }

    public void run() {
        Map<LibraryBookId, Integer> inventory = SCANNER.getInventory();
        for (Map.Entry<LibraryBookId, Integer> entry : inventory.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                bookshelf.addBook(new Book(entry.getKey()));
            }
        }

        while (true) {
            LibraryCommand<?> command = SCANNER.nextCommand();
            if (command == null) {
                break;
            }
            date = command.getDate();

            if (command.getCmd().equals("OPEN")) {
                arrange4Open();
            } else if (command.getCmd().equals("CLOSE")) {
                arrange4Close();
            } else {
                LibraryRequest request = (LibraryRequest) command.getCmd();
                String userId = request.getStudentId();
                users.putIfAbsent(userId, new User(userId));

                if (request.getType().equals(LibraryRequest.Type.QUERIED)) {
                    handleQueried(request);
                } else if (request.getType().equals(LibraryRequest.Type.BORROWED)) {
                    handleBorrowed(request);
                } else if (request.getType().equals(LibraryRequest.Type.RETURNED)) {
                    handleReturned(request);
                } else if (request.getType().equals(LibraryRequest.Type.ORDERED)) {
                    handleOrdered(request);
                } else if (request.getType().equals(LibraryRequest.Type.PICKED)) {
                    handlePick(request);
                }
            }
        }
    }

    private void handleQueried(LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        int res = bookshelf.queryBookCount(bookId);
        PRINTER.info(date, bookId, res);
    }

    private void handleBorrowed(LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        Book book = bookshelf.getBook(bookId);
        if (book == null) { // 无余本在架
            PRINTER.reject(date, request);
        } else if (bookId.getType().equals(LibraryBookId.Type.A)) { // A 类书不予借阅
            PRINTER.reject(date, request);
        } else { // 前往借还台
            bookshelf.removeBook(book); // 从书架移除
            if (user.canBorrowOrOrder(bookId)) { // 检查该用户所持有的书籍数量是否符合借阅数量限制
                // 借阅成功，该用户从此刻起持有该书
                user.addBook(book);
                PRINTER.accept(date, request);
            } else {
                // 借阅失败，书籍被扣在借还处，借还处从此刻起持有该书
                borrowReturnOffice.addBook(book);
                PRINTER.reject(date, request);
            }
        }
    }

    private void handleReturned(LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        Book book = user.getBook(bookId);
        // 借还处持有该书，用户不再持有该书
        user.removeBook(book);
        borrowReturnOffice.addBook(book);
        PRINTER.accept(date, request);
    }

    private void handleOrdered(LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        if (user.canBorrowOrOrder(bookId)) { // 该用户必须满足要求才可以预约成功
            appointmentOffice.addRequest(request);
            // 预约成功后，图书馆可在此后的整理流程中选择将书送至预约处
            PRINTER.accept(date, request);
        } else {
            PRINTER.reject(date, request);
        }
    }

    private void handlePick(LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        if (user.canBorrowOrOrder(bookId)) { // 取书后该用户持有的书仍然满足借阅数量限制
            // 预约处存在一本为该用户保留的图书
            Book book = appointmentOffice.getBook4User(userId, bookId);
            if (book == null) { // 没有
                PRINTER.reject(date, request);
            } else { // 该用户取书成功，即刻起由用户持有该书
                appointmentOffice.removeBook(userId, book);
                user.addBook(book);
                PRINTER.accept(date, request);
            }
        } else {
            PRINTER.reject(date, request);
        }
    }

    // 在开馆对应的整理后（即在 OPEN 指令对应的整理后），借还处不应该有书，预约处不应该有逾期的书。
    /*
    闭馆时:借还处(all)->书架，预约处所有书籍剩余时间-1
     */
    private void arrange4Close() {
        ArrayList<LibraryMoveInfo> moveInfos
                = new ArrayList<>(borrowReturnOffice.moveBook2Bookshelf());
        PRINTER.move(date, moveInfos);
    }

    /*
    开馆时：预约处剩余时间为0的书籍整理回书架；遍历并试图处理预约处所有预约请求，然后清理
     */
    private void arrange4Open() {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();

        moveInfos.addAll(appointmentOffice.moveBooks2bookshelf(date));
        moveInfos.addAll(appointmentOffice.handleRequests(date));

        PRINTER.move(date, moveInfos);
    }

}
