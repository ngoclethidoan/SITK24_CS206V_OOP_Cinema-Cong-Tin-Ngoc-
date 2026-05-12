// SeatService.java
package service;

import exception.SeatAlreadyBookedException;
import interfaces.ISeatService;
import model.*;

public class SeatService implements ISeatService {

    @Override
    public void select(Seat seat) throws SeatAlreadyBookedException {
        synchronized (seat) {
            if (!seat.isAvailable()) {
                throw new SeatAlreadyBookedException(seat.getCodeSeat()); // ← custom exception
            }
            seat.setState(Seat.State.booked);
        }
    }

    @Override
    public void cancel(Seat seat) {
        synchronized (seat) {
            if (!seat.isAvailable()) {
                seat.setState(Seat.State.available);
            }
        }
    }
}