package model;

public class StandardSeat extends Seat {

    public StandardSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        // Not change the price
        return price;
    }
}
