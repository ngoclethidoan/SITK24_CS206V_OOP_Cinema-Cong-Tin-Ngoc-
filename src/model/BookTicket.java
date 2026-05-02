package model;
import java.time.LocalDateTime;

public class BookTicket {

    private final Room room;
    private final Seat seat;
    private final Film film;
    private final double price;
    private final LocalDateTime time;
    private String status;

    public BookTicket(Room room, Seat seat, Film film, double price) {
        this.room = room;
        this.seat = seat;
        this.film = film;
        this.price = price;
        this.time = LocalDateTime.now();
        this.status = "BOOKED";
    }

    public Room getRoom() { return room; }
    public Seat getSeat() { return seat; }
    public Film getFilm() { return film; }
    public double getPrice() { return price; }
    public LocalDateTime getTime() { return time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}