package service;

import interfaces.ISeatService;
import model.*;

public class SeatService implements ISeatService {

    @Override
    public void select(Seat seat) {
        synchronized (seat) {
            if (!seat.isAvailable()) {
                throw new IllegalStateException(
                    "Seat " + seat.getCodeSeat() + " is not available"
                );
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