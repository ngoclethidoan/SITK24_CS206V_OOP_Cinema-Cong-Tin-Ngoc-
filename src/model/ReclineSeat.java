package model;

public class ReclineSeat extends Seat {

    public ReclineSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        return price * 1.3; // Increase 30%
    }
}
