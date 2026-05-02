package model;

public abstract class Seat {

    public enum State {
        available,
        booked;
    }

    private final String codeSeat;
    private final int row;
    private final int column;
    protected final double price;
    private State state;

    // 🔥 NEW
    private Room room;

    public Seat(String codeSeat, int row, int column, double price) {
        this.codeSeat = codeSeat;
        this.row = row;
        this.column = column;
        this.price = price;
        this.state = State.available;
    }

    public abstract double computePrice();

    public String getCodeSeat() { return codeSeat; }
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public double getPrice() { return price; }
    public State getState() { return state; }

    // 🔥 NEW
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public void setState(State newState) { this.state = newState; }

    public boolean isAvailable() { return state == State.available; }

    @Override
    public String toString() {
        return String.format("[%s | %s | %.0f VND]",
                codeSeat,
                getClass().getSimpleName(),
                computePrice());
    }
}