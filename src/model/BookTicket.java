package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookTicket {

    private final Room room;
    private final Seat seat;
    private final Film film;
    private final double price;
    private final LocalDateTime time;
    private String status;

    // ⭐ THÊM: danh sách bắp/nước đi kèm
    private List<Item> snackItems = new ArrayList<>();

    public BookTicket(Room room, Seat seat, Film film, double price) {
        this.room = room;
        this.seat = seat;
        this.film = film;
        this.price = price;
        this.time = LocalDateTime.now();
        this.status = "BOOKED";
    }

    // Constructor mới với snack items
    public BookTicket(Room room, Seat seat, Film film, double price, List<Item> snackItems) {
        this(room, seat, film, price);
        if (snackItems != null) {
            this.snackItems = new ArrayList<>(snackItems);
        }
    }

    public Room getRoom()             { return room; }
    public Seat getSeat()             { return seat; }
    public Film getFilm()             { return film; }
    public double getPrice()          { return price; }
    public LocalDateTime getTime()    { return time; }
    public String getStatus()         { return status; }
    public List<Item> getSnackItems() { return snackItems; }

    public void setStatus(String status) { this.status = status; }

    public void setSnackItems(List<Item> items) {
        this.snackItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    /** Tổng tiền bắp/nước */
    public double getSnackTotal() {
        return snackItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    /** Tổng tiền vé + bắp/nước */
    public double getGrandTotal() {
        return price + getSnackTotal();
    }
}