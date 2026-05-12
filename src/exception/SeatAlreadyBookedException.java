// exception/SeatAlreadyBookedException.java
package exception;
public class SeatAlreadyBookedException extends Exception {
    public SeatAlreadyBookedException(String seatCode) {
        super("Seat " + seatCode + " is already booked.");
    }
}