package model;

public class VIPSeat extends Seat {

    public VIPSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        return price * 1.5; // VIP = Standard + 50%
    }
}
