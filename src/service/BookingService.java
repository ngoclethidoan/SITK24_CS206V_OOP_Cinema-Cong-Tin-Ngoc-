/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.*;
import java.util.List;

public class BookingService {

    private SeatService seatService = new SeatService();

    /**
     * Đặt vé: kiểm tra ghế, gắn item, lưu vào lịch sử user.
     * @return BookTicket nếu thành công, null nếu ghế không còn trống.
     */
    public BookTicket book(User user, Room room, Seat seat,
                           Film film, List<Item> items) {
        // 1. Ghế phải còn available
        if (!seat.isAvailable()) {
            return null;
        }

        // 2. Đánh dấu booked
        seatService.select(seat);

        // 3. Tạo vé
        BookTicket ticket = new BookTicket(room, seat, film);

        // 4. Gắn đồ ăn vào vé
        for (Item item : items) {
            ticket.getItems().add(item);
        }

        // 5. Lưu vào lịch sử user
        user.getBookingHistory().add(ticket);

        return ticket;
    }

    /**
     * Huỷ vé: trả ghế về available, xoá khỏi lịch sử.
     */
    public boolean cancel(User user, BookTicket ticket) {
        if (!user.getBookingHistory().contains(ticket)) {
            return false;
        }
        seatService.cancel(ticket.getSeat());
        user.getBookingHistory().remove(ticket);
        return true;
    }

    /**
     * Tính tổng tiền của một vé.
     */
    public double calcTotal(BookTicket ticket) {
        double total = ticket.getSeat().computePrice();
        for (Item item : ticket.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}
