public class MainClass {

    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();
        AppointmentOffice appointmentOffice = new AppointmentOffice(bookshelf);
        DriftingCorner driftingCorner = new DriftingCorner();
        BorrowReturnOffice borrowReturnOffice = new BorrowReturnOffice(bookshelf, driftingCorner);


        Runner runner = new Runner(
                bookshelf,
                appointmentOffice,
                borrowReturnOffice,
                driftingCorner
        );
        runner.run();
    }
}
