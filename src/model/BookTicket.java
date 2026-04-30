package model;

import java.util.ArrayList;
import java.util.List;

public class BookTicket {

    private final Room room;
    private final Seat seat;
    private final Film film;
    private final List<Item> items;

    public BookTicket(Room room, Seat seat, Film film) {
        this.room = room;
        this.seat = seat;
        this.film = film;
        this.items = new ArrayList<>();
    }

    public Room getRoom() { return room; }
    public Seat getSeat() { return seat; }
    public Film getFilm() { return film; }
    public List<Item> getItems() { return items; }
}