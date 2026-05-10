// src/service/PaymentService.java
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
        for (CartItem t : tickets)    total += t.getSeat().computePrice();
        for (SnackCartItem s : snacks) total += s.getTotalPrice();
        return total;
    }

    @Override
public void processPayment(User user, List<CartItem> tickets, List<SnackCartItem> snacks) {
    SeatService seatService = new SeatService();

    // Flatten all snack items once
    List<Item> flatItems = new ArrayList<>();
    for (SnackCartItem s : snacks) {
        flatItems.addAll(s.getItems());
    }

    for (CartItem c : tickets) {
        if (!c.getSeat().isAvailable()) continue;
        seatService.select(c.getSeat());
        BookTicket ticket = new BookTicket(c.getRoom(), c.getSeat(), c.getFilm(), c.getSeat().computePrice());
        user.getBookingHistory().add(ticket);
        BookingDatabase.save(user.getUserId(), ticket, flatItems.isEmpty() ? null : flatItems);
    }

    user.getCart().clear();
    user.getSnackCart().clear();
}
}