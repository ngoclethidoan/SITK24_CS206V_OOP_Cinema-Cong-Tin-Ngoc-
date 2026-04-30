package model;

public class Room {

    private final String roomId;
    private final int row;
    private final int col;

    private final Seat[][] seats;

    private State state;

    public enum State {
        occupied,
        unoccupied
    }

    public Room(String roomId, int row, int col) {
        this.roomId = roomId;
        this.row = row;
        this.col = col;
        this.seats = new Seat[row][col];
        this.state = State.unoccupied;
    }

    // ── Getters ─────────────────────
    public String getRoomId() {
        return roomId;
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public Seat getSeat(int r, int c) {
        return seats[r][c];
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isOccupied() {
        return state == State.occupied;
    }

    // ── Seat control ─────────────────
    public void setSeat(int r, int c, Seat seat) {
        seats[r][c] = seat;
    }

    public void setState(State state) {
        this.state = state;
    }
}