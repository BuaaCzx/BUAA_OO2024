import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCloseCmd;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryOpenCmd;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.oocourse.library2.LibrarySystem.PRINTER;
import static com.oocourse.library2.LibrarySystem.SCANNER;

public class Runner {
    private final Bookshelf bookshelf;
    private final AppointmentOffice appointmentOffice;
    private final BorrowReturnOffice borrowReturnOffice;
    private final DriftingCorner driftingCorner;
    private final HashMap<String, User> users;
    private final HashMap<LibraryBookId, Integer> appointmentsNum = new HashMap<>();

    private LocalDate date;

    public Runner(
            Bookshelf bookshelf,
            AppointmentOffice appointmentOffice,
            BorrowReturnOffice borrowReturnOffice,
            DriftingCorner driftingCorner
    ) {
        this.bookshelf = bookshelf;
        this.appointmentOffice = appointmentOffice;
        this.borrowReturnOffice = borrowReturnOffice;
        this.driftingCorner = driftingCorner;
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
            LibraryCommand command = SCANNER.nextCommand();
            if (command == null) {
                break;
            }
            date = command.getDate(); // 今天的日期
            if (command instanceof LibraryOpenCmd) {
                // 在开馆时做点什么
                arrange4Open();
            } else if (command instanceof LibraryCloseCmd) {
                // 在闭馆时做点什么
                arrange4Close();
            } else {
                LibraryReqCmd req = (LibraryReqCmd) command;
                LibraryRequest.Type type = req.getType(); // 指令对应的类型（查询/借阅/预约/还书/取书/续借/捐赠）
                LibraryBookId bookId = req.getBookId(); // 指令对应书籍编号（type-uid）
                String studentId = req.getStudentId(); // 指令对应的用户Id
                users.putIfAbsent(studentId, new User(studentId));
                // 对指令进行处理
                if (type.equals(LibraryRequest.Type.QUERIED)) {
                    handleQueried(req);
                } else if (type.equals(LibraryRequest.Type.BORROWED)) {
                    handleBorrowed(req);
                } else if (type.equals(LibraryRequest.Type.RETURNED)) {
                    handleReturned(req);
                } else if (type.equals(LibraryRequest.Type.ORDERED)) {
                    handleOrdered(req);
                } else if (type.equals(LibraryRequest.Type.PICKED)) {
                    handlePick(req);
                } else if (type.equals(LibraryRequest.Type.RENEWED)) {
                    handleRenewed(req);
                } else if (type.equals(LibraryRequest.Type.DONATED)) {
                    handleDonated(req);
                }
            }
        }
    }

    private void handleQueried(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        int res;
        if (isFormalBook(bookId)) {
            res = bookshelf.queryBookCount(bookId);
        } else {
            res = driftingCorner.queryBookCount(bookId);
        }

        PRINTER.info(request, res);
    }

    private void handleBorrowed(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);

        if (isFormalBook(bookId)) { // 是正式书籍，从书架里找
            Book book = bookshelf.getBook(bookId);
            if (book == null) { // 无余本在架
                PRINTER.reject(request);
            } else if (bookId.getType().equals(LibraryBookId.Type.A)) { // A 类书不予借阅
                PRINTER.reject(request);
            } else { // 前往借还台
                bookshelf.removeBook(book); // 从书架移除
                if (user.canBorrow(bookId)) { // 检查该用户所持有的书籍数量是否符合借阅数量限制
                    // 借阅成功，该用户从此刻起持有该书
                    user.addBook(book);
                    book.setExpirationTime(date);
                    PRINTER.accept(request);
                } else {
                    // 借阅失败，书籍被扣在借还处，借还处从此刻起持有该书
                    borrowReturnOffice.addBook(book);
                    PRINTER.reject(request);
                }
            }
        } else { // 去漂流处找
            Book book = driftingCorner.getBook(bookId);
            if (book == null) { // 无余本在架
                PRINTER.reject(request);
            } else if (bookId.getType().equals(LibraryBookId.Type.AU)) { // A 类书不予借阅
                PRINTER.reject(request);
            } else { // 前往借还台
                driftingCorner.removeBook(book); // 从书架移除
                if (user.canBorrow(bookId)) { // 检查该用户所持有的书籍数量是否符合借阅数量限制
                    // 借阅成功，该用户从此刻起持有该书
                    user.addBook(book);
                    book.setExpirationTime(date);
                    PRINTER.accept(request);
                } else {
                    // 借阅失败，书籍被扣在借还处，借还处从此刻起持有该书
                    borrowReturnOffice.addBook(book);
                    PRINTER.reject(request);
                }
            }
        }

    }

    private void handleReturned(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        Book book = user.getBook(bookId);
        // 借还处持有该书，用户不再持有该书
        user.removeBook(book);
        borrowReturnOffice.addBook(book);
        if (!isFormalBook(bookId)) {
            book.addReturnCount();
        }
        if (date.isAfter(book.getExpirationTime())) {
            PRINTER.accept(request, "overdue");
        } else {
            PRINTER.accept(request, "not overdue");
        }

    }

    private void handleOrdered(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        if (user.canOrder(bookId)) { // 该用户必须满足要求才可以预约成功
            appointmentOffice.addRequest(request);
            appointmentsNum.putIfAbsent(bookId, 0);
            appointmentsNum.put(bookId, appointmentsNum.get(bookId) + 1);
            // 预约成功后，图书馆可在此后的整理流程中选择将书送至预约处
            PRINTER.accept(request);
        } else {
            PRINTER.reject(request);
        }
    }

    private void handlePick(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        if (user.canBorrow(bookId)) { // 取书后该用户持有的书仍然满足借阅数量限制
            // 预约处存在一本为该用户保留的图书
            Book book = appointmentOffice.getBook4User(userId, bookId);
            if (book == null) { // 没有
                PRINTER.reject(request);
            } else { // 该用户取书成功，即刻起由用户持有该书
                appointmentOffice.removeBook(userId, book);
                user.addBook(book);
                appointmentsNum.put(bookId, appointmentsNum.get(bookId) - 1);
                book.setExpirationTime(date);
                PRINTER.accept(request);
            }
        } else {
            PRINTER.reject(request);
        }
    }

    private void handleDonated(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        driftingCorner.addBook(new Book(bookId));
        PRINTER.accept(request);
    }

    /*
    当一位用户持有某书时，可在还书期限的前5天内办理续借手续。
    即1月30日为还书不逾期的最后一天时，26-30日开馆后可办理续借手续。提前办理或逾期办理皆会失败。

    续借时，若存在任意一位用户对该书正在生效的预约
    （从预约成功开始，直到取书成功预约完成前，或送书后一直未取书导致预约失效前）
    且该书无在架余本，则续借失败。除此种情况外，用户续借成功，
    该书的借阅期限延长30天，例如原本应在3月1日及以前还书，续借成功后还书期限延长至3月31日。
     */
    private void handleRenewed(LibraryReqCmd request) {
        LibraryBookId bookId = request.getBookId();
        String userId = request.getStudentId();
        User user = users.get(userId);
        Book book = user.getBook(bookId);

        if (book.canRenew(date)) {
            appointmentsNum.putIfAbsent(bookId, 0);
            if (bookshelf.getBook(bookId) == null && appointmentsNum.get(bookId) > 0) {
                PRINTER.reject(request);
            } else {
                book.renew();
                PRINTER.accept(request);
            }
        } else {
            PRINTER.reject(request);
        }
    }

    // 在开馆对应的整理后（即在 OPEN 指令对应的整理后），借还处不应该有书，预约处不应该有逾期的书。
    /*
    闭馆时:借还处(formalBook)->书架，预约处所有书籍剩余时间-1
     */
    private void arrange4Close() {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();

        moveInfos.addAll(borrowReturnOffice.moveBooks());

        PRINTER.move(date, moveInfos);
    }

    /*
    开馆时：预约处剩余时间为0的书籍整理回书架；遍历并试图处理预约处所有预约请求，然后清理
    借还处(informalBook)->书架/漂流
     */
    private void arrange4Open() {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
        ArrayList<LibraryMoveInfo> tmp = appointmentOffice.moveBooks2bookshelf(date);

        moveInfos.addAll(tmp);
        moveInfos.addAll(appointmentOffice.handleRequests(date));

        for (LibraryMoveInfo moveInfo : tmp) {
            LibraryBookId id = moveInfo.getBookId();
            appointmentsNum.put(id, appointmentsNum.get(id) - 1);
        }

        PRINTER.move(date, moveInfos);
    }

    private boolean isFormalBook(LibraryBookId id) {
        return id.getType().equals(LibraryBookId.Type.A)
                || id.getType().equals(LibraryBookId.Type.B)
                || id.getType().equals(LibraryBookId.Type.C);
    }

}
