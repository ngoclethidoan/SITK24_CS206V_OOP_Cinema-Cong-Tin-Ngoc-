package model;

public class CoupleSeat extends Seat {

    public CoupleSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        return price * 1.8; // 2 people
    }
}
