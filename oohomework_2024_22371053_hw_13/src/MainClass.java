public class MainClass {

    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();
        AppointmentOffice appointmentOffice = new AppointmentOffice(bookshelf);
        BorrowReturnOffice borrowReturnOffice = new BorrowReturnOffice(bookshelf);

        Runner runner = new Runner(bookshelf, appointmentOffice, borrowReturnOffice);
        runner.run();
    }
}
