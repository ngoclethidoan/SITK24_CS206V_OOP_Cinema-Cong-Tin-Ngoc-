package service;

import interfaces.IPaymentService;
import database.BookingDatabase;
import java.util.ArrayList;
import model.*;
import java.util.List;

public class PaymentService implements IPaymentService {

    @Override
    public double calcTotal(List<CartItem> tickets, List<SnackCartItem> snacks) {
        double total = 0;
        for (CartItem t : tickets)     total += t.getSeat().computePrice();
        for (SnackCartItem s : snacks) total += s.getTotalPrice();
        return total;
    }

    @Override
    public void processPayment(User user, List<CartItem> tickets, List<SnackCartItem> snacks) {
        // Flatten snack items
        List<Item> flatItems = new ArrayList<>();
        for (SnackCartItem s : snacks) flatItems.addAll(s.getItems());

        // Remove all pending rows for this user (they become PAID below)
        BookingDatabase.removePending(user.getUserId());

        for (CartItem c : tickets) {
            // Seat was already reserved (booked) when added to cart.
            // Just confirm the state and save as PAID.
            c.getSeat().setState(Seat.State.booked);

            BookTicket ticket = new BookTicket(
                c.getRoom(), c.getSeat(), c.getFilm(), c.getSeat().computePrice()
            );
            user.getBookingHistory().add(ticket);
            BookingDatabase.save(
                user.getUserId(), ticket,
                flatItems.isEmpty() ? null : flatItems
            );
        }

        user.getCart().clear();
        user.getSnackCart().clear();
    }
}
