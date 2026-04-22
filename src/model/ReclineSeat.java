/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
public class ReclineSeat extends Seat {

    public ReclineSeat(String codeSeat, int row, int column, double price) {
        super(codeSeat, row, column, price);
    }

    @Override
    public double computePrice() {
        return price * 1.3; // Increase 30%
    }
}
