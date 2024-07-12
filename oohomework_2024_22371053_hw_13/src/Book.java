import com.oocourse.library1.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book {
    private final LibraryBookId id;
    private LocalDate appointmentTime;

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

}
