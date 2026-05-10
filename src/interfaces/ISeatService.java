// src/interfaces/ISeatService.java
package interfaces;
import model.Seat;

public interface ISeatService {
    void select(Seat seat);
    void cancel(Seat seat);
}