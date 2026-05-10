// src/interfaces/IBookingService.java
package interfaces;
import model.*;
import java.util.List;

public interface IBookingService {
    BookTicket book(User user, Room room, Seat seat, Film film, List<Item> items);
    boolean cancel(User user, BookTicket ticket);
    void addToCart(User user, Film film, Room room, Seat seat);
    void removeFromCart(User user, CartItem item);
    void checkout(User user);
    double calcCartTotal(User user);
    double calcTotal(BookTicket ticket, List<Item> items);
}