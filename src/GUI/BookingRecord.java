package model;

import java.time.LocalDateTime;

public class BookingRecord {

    private final Film film;
    private final Seat seat;
    private final String roomName;
    private final double price;
    private final LocalDateTime time;
    private String status;

    public BookingRecord(Film film, Seat seat, String roomName, double price) {
        this.film = film;
        this.seat = seat;
        this.roomName = roomName;
        this.price = price;
        this.time = LocalDateTime.now();
        this.status = "BOOKED";
    }

    public Film getFilm() { return film; }
    public Seat getSeat() { return seat; }
    public String getRoomName() { return roomName; }
    public double getPrice() { return price; }
    public LocalDateTime getTime() { return time; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}