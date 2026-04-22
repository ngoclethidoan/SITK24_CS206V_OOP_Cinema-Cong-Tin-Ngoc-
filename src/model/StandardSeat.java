/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
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
