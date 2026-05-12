// exception/InvalidSeat.java
package exception;
public class InvalidSeat extends Exception {
    public InvalidSeat(String seatCode) {
        super("Seat " + seatCode + " is not available or does not exist.");
    }
}