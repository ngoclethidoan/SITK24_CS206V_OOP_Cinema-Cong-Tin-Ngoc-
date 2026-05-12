package interfaces;

import exception.SeatAlreadyBookedException;
import model.Seat;

public interface ISeatService {
    void select(Seat seat) throws SeatAlreadyBookedException;
    void cancel(Seat seat);
}