/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import interfaces.ISeatService;
/**
 *
 * @author Administrator
 */
import model.*;
public class SeatService implements ISeatService {
    
    // Manage selection
    public void select(Seat seat) {
    if (!seat.isAvailable()) {
        throw new IllegalStateException("Seat " + seat.getCodeSeat() + " is not available");
    }
    seat.setState(Seat.State.booked); // ← ADD THIS LINE
}
    
    // Manage cancellation
    public void cancel(Seat seat) {
        if (!seat.isAvailable()) {
            seat.setState(Seat.State.available);
        }
    }
}
