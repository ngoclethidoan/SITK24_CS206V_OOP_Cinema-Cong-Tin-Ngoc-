package service;

import model.*;
import java.util.List;

public class BookingService {

    private SeatService seatService = new SeatService();

    public BookTicket book(User user, Room room, Seat seat,
                           Film film, List<Item> items) {

        if (!seat.isAvailable()) {
            return null;
        }

        seatService.select(seat);

        BookTicket ticket = new BookTicket(room, seat, film);

        for (Item item : items) {
            ticket.getItems().add(item);
        }

        user.getBookingHistory().add(ticket);

        return ticket;
    }

    public boolean cancel(User user, BookTicket ticket) {

        if (!user.getBookingHistory().contains(ticket)) {
            return false;
        }

        seatService.cancel(ticket.getSeat());
        user.getBookingHistory().remove(ticket);
        return true;
    }

    public double calcTotal(BookTicket ticket) {

        double total = ticket.getSeat().computePrice();

        for (Item item : ticket.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }

        return total;
    }

    public void addToCart(User user, Film film, Room room, Seat seat) {

        if (!seat.isAvailable()) return;

        List<CartItem> cart = user.getCart();

        for (CartItem c : cart) {
            if (c.getFilm().equals(film)
                    && c.getSeat().equals(seat)
                    && c.getRoom().equals(room)) {
                c.increase();
                return;
            }
        }

        cart.add(new CartItem(film, room, seat));
    }

    public void removeFromCart(User user, CartItem item) {
        user.getCart().remove(item);
    }

    public void checkout(User user) {

        List<CartItem> cart = user.getCart();

        for (CartItem c : cart) {

            if (!c.getSeat().isAvailable()) continue;

            seatService.select(c.getSeat());

            BookTicket ticket = new BookTicket(
                    c.getRoom(),
                    c.getSeat(),
                    c.getFilm()
            );

            user.getBookingHistory().add(ticket);
        }

        cart.clear();
    }

    public double calcCartTotal(User user) {

        double total = 0;

        for (CartItem c : user.getCart()) {
            total += c.getSeat().computePrice() * c.getQuantity();
        }

        return total;
    }
}