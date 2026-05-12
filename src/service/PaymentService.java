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
    public void processPayment(User user, List<CartItem> tickets, 
                           List<SnackCartItem> snacks, boolean fromCart) {
    List<Item> flatItems = new ArrayList<>();
    for (SnackCartItem s : snacks) flatItems.addAll(s.getItems());

    for (int i = 0; i < tickets.size(); i++) {
        CartItem c = tickets.get(i);
        // Reserve seat
        synchronized (c.getSeat()) {
            c.getSeat().setState(Seat.State.booked);
        }
        List<Item> ticketSnacks = (i == 0 && !flatItems.isEmpty()) ? flatItems : null;
        BookTicket ticket = new BookTicket(
            c.getRoom(), c.getSeat(), c.getFilm(),
            c.getSeat().computePrice(), ticketSnacks
        );
        user.getBookingHistory().add(ticket);
        BookingDatabase.save(user.getUserId(), ticket, ticketSnacks);
    }
    if (fromCart) {
        // ✅ Only remove the paid tickets from cart, not everything
        user.getCart().removeAll(tickets);
        user.getSnackCart().removeAll(snacks);

        // Remove only paid pending rows from CSV
        for (CartItem c : tickets) {
            BookingDatabase.removePendingTicket(
                user.getUserId(),
                c.getFilm().getCodeFilm(),
                c.getSeat().getCodeSeat()
            );
        }
        // Remove paid snack pending rows
        if (!snacks.isEmpty()) {
            BookingDatabase.removePending(user.getUserId()); // snacks don't have individual remove
        }
    }
     
    
    
}
    @Override
    public List<String[]> getPaymentMethods() {
        return List.of(
            new String[]{"CASH",  "💵", "Cash"},
            new String[]{"CARD",  "💳", "Credit Card"},
            new String[]{"DEBIT", "🏦", "Debit Card"},
            new String[]{"QR",    "📱", "QR Pay"}
        );
    }

    @Override
    public boolean isValidPaymentMethod(String code) {
        return getPaymentMethods().stream()
            .anyMatch(m -> m[0].equals(code));
    }
}
