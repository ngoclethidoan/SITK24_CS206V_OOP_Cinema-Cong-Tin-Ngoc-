package model;

public class CartItem {

    private Film film;
    private Room room;
    private Seat seat;

    private int quantity = 1;

    // NEW: dùng cho checkbox payment
    private boolean selected = false;

    public CartItem(Film film, Room room, Seat seat) {
        this.film = film;
        this.room = room;
        this.seat = seat;
    }

    // ── GETTERS ─────────────────────────────
    public Film getFilm() { return film; }
    public Room getRoom() { return room; }
    public Seat getSeat() { return seat; }
    public int getQuantity() { return quantity; }

    public boolean isSelected() { return selected; }

    // ── SETTERS ─────────────────────────────
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void increase() {
        quantity++;
    }

    // ── UTILITY ─────────────────────────────
    public double getTotalPrice() {
        if (seat == null) return 0;
        return seat.computePrice() * quantity;
    }

    @Override
    public String toString() {
        return film.getTitle() + " | " +
               room.getRoomId() + " | " +
               seat.getCodeSeat();
    }
}