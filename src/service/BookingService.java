package service;

import database.BookingDatabase;
import model.*;
import java.util.List;

/**
 * BookingService – xử lý đặt vé, huỷ vé, giỏ hàng.
 *
 * Lỗi cũ đã sửa:
 *   1. new BookTicket(room, seat, film)         → thiếu tham số price
 *      → sửa thành: new BookTicket(room, seat, film, seat.computePrice())
 *
 *   2. ticket.getItems()                        → BookTicket không có trường items
 *      → items được truyền thẳng vào book() và lưu qua BookingDatabase
 *
 *   3. Sau mỗi thao tác book/checkout, tự động lưu vào bookings.csv
 *      thông qua BookingDatabase.save()
 */
public class BookingService {

    private SeatService seatService = new SeatService();

    // ────────────────────────────────────────────────────────────────────
    //  ĐẶT VÉ TRỰC TIẾP
    // ────────────────────────────────────────────────────────────────────

    /**
     * Đặt vé cho một ghế cụ thể.
     *
     * @param user  Người đặt
     * @param room  Phòng chiếu
     * @param seat  Ghế muốn đặt
     * @param film  Phim
     * @param items Danh sách combo/đồ ăn đi kèm (có thể null/rỗng)
     * @return BookTicket nếu thành công, null nếu ghế đã bị đặt
     */
    public BookTicket book(User user, Room room, Seat seat,
                           Film film, List<Item> items) {

        if (!seat.isAvailable()) {
            return null;
        }

        seatService.select(seat);

        // FIX: truyền đúng 4 tham số — thêm seat.computePrice()
        BookTicket ticket = new BookTicket(room, seat, film, seat.computePrice());

        user.getBookingHistory().add(ticket);

        // Lưu vào CSV database
        BookingDatabase.save(user.getUserId(), ticket, items);

        return ticket;
    }

    // ────────────────────────────────────────────────────────────────────
    //  HUỶ VÉ
    // ────────────────────────────────────────────────────────────────────

    public boolean cancel(User user, BookTicket ticket) {

        if (!user.getBookingHistory().contains(ticket)) {
            return false;
        }

        seatService.cancel(ticket.getSeat());
        user.getBookingHistory().remove(ticket);
        return true;
    }

    // ────────────────────────────────────────────────────────────────────
    //  TÍNH TIỀN
    // ────────────────────────────────────────────────────────────────────

    /**
     * Tính tổng tiền: giá ghế + giá item đi kèm.
     */
    public double calcTotal(BookTicket ticket, List<Item> items) {

        double total = ticket.getSeat().computePrice();

        if (items != null) {
            for (Item item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }

        return total;
    }

    /** Overload: tính tiền vé không có item. */
    public double calcTotal(BookTicket ticket) {
        return calcTotal(ticket, null);
    }

    // ────────────────────────────────────────────────────────────────────
    //  GIỎ HÀNG
    // ────────────────────────────────────────────────────────────────────

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

    /**
     * Thanh toán toàn bộ giỏ hàng.
     * Mỗi CartItem hợp lệ sẽ tạo BookTicket và lưu vào bookings.csv.
     */
    public void checkout(User user) {

        List<CartItem> cart = user.getCart();

        for (CartItem c : cart) {

            if (!c.getSeat().isAvailable()) continue;

            seatService.select(c.getSeat());

            // FIX: truyền đúng 4 tham số
            BookTicket ticket = new BookTicket(
                    c.getRoom(),
                    c.getSeat(),
                    c.getFilm(),
                    c.getSeat().computePrice()
            );

            user.getBookingHistory().add(ticket);

            // Lưu vào CSV database
            BookingDatabase.save(user.getUserId(), ticket, null);
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