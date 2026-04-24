package model;

public class PremiumSeat extends Seat {

    public PremiumSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        return price * 2; // Premium = x2 standard seat
    }
}
