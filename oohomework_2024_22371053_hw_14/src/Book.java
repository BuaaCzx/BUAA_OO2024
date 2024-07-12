import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.annotation.Trigger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book {
    private final LibraryBookId id;
    private LocalDate appointmentTime;
    private LocalDate expirationTime;
    private int returnCount = 0;

    public Book(LibraryBookId id) {
        this.id = id;
        this.appointmentTime = null;
    }

    public LibraryBookId getId() {
        return id;
    }

    public LibraryBookId.Type getType() {
        return id.getType();
    }

    public void setAppointmentTime(LocalDate time) {
        this.appointmentTime = time;
    }

    public boolean noTimeLeft(LocalDate curDate) {
        long daysBetween = ChronoUnit.DAYS.between(appointmentTime, curDate);
        System.err.println("daysBetween: " + daysBetween);
        return daysBetween >= 5;
    }

    private int getTimeLimit() {
        if (id.getType().equals(LibraryBookId.Type.B)) {
            return 30;
        } else if (id.getType().equals(LibraryBookId.Type.BU)) {
            return 7;
        } else if (id.getType().equals(LibraryBookId.Type.C)) {
            return 60;
        } else if (id.getType().equals(LibraryBookId.Type.CU)) {
            return 14;
        }
        System.err.println("Not arrive here!");
        return -1;
    }

    public void setExpirationTime(LocalDate curTime) {
        this.expirationTime = curTime.plusDays(getTimeLimit());
    }

    public boolean isFormal() {
        return id.getType().equals(LibraryBookId.Type.A)
                || id.getType().equals(LibraryBookId.Type.B)
                || id.getType().equals(LibraryBookId.Type.C);
    }

    public LocalDate getExpirationTime() {
        return expirationTime;
    }

    public void addReturnCount() {
        returnCount++;
    }

    public int getReturnCount() {
        return returnCount;
    }

    @Trigger(from = "InitState", to = "FinalState")
    public Book trans2FormalBook() {
        LibraryBookId id = null;
        if (getType().equals(LibraryBookId.Type.BU)) {
            id = new LibraryBookId(LibraryBookId.Type.B, this.id.getUid());
        } else if (getType().equals(LibraryBookId.Type.CU)) {
            id = new LibraryBookId(LibraryBookId.Type.C, this.id.getUid());
        }
        if (id == null) {
            System.err.println("Not Null!");
        }
        return new Book(id);
    }

    /*
    可在还书期限的前5天内办理续借手续。
    即1月30日为还书不逾期的最后一天时，26-30日开馆后可办理续借手续。提前办理或逾期办理皆会失败。
     */
    public boolean canRenew(LocalDate curTime) {

        if (!isFormal()) {
            return false;
        }

        if (curTime.isBefore(expirationTime.minusDays(4))) {
            return false;
        }

        if (curTime.isAfter(expirationTime)) {
            return false;
        }

        return true;
    }

    public void renew() {
        expirationTime = expirationTime.plusDays(30);
    }
}
