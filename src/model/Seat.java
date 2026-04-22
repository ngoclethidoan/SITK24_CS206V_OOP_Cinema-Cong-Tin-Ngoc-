/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/*
 *
 * @author Administrator
 */
public abstract class Seat {

    public enum State {
        available,
        booked;
    }

    private final String codeSeat;      // Ex: A1, B5
    private final int row;
    private final int column;
    protected final double price;
    private State state;

    public Seat(String codeSeat, int row, int column, double price) {
        this.codeSeat     = codeSeat;
        this.row     = row;
        this.column      = column;
        this.price = price;
        this.state = State.available;
    }

    /* compute prices of the seats */
    public abstract double computePrice();

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getCodeSeat() { return codeSeat;}
    public int getRow() { return row;}
    public int getColumn() { return column;}
    public double getPrice() { return price;}
    public State getState() { return state;}
    
    // ── Setters ──────────────────────────────────────────────────────
    public void setState(State newState) {this.state = newState;}

    // ── State transitions ────────────────────────────────────────────────────
    public boolean isAvailable()   { return state == State.available; }

    @Override
    public String toString() {
        return String.format("[%s | %s | %.0f VND]", codeSeat, getClass().getSimpleName(), computePrice());
    }
}
