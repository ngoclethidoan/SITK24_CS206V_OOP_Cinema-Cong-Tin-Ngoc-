/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
import java.util.List;
import java.util.ArrayList;
public class User {
    private String name;
    private final String userId;
    private String password;
    private boolean isVIP;
    private List<BookTicket> bookingHistory;
    
    public User(String name, String userId, String password, List<BookTicket> bookingHistory) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.isVIP = false;
        this.bookingHistory = bookingHistory;
    }
    
    // ── Getters ──────────────────────────────────────────────────────
    public String getName() {return this.name;}
    public String getUserId() {return this.userId;}
    public String getPassword() {return this.password;}
    public List<BookTicket> getBookingHistory() {return this.bookingHistory;}
    
    // ── Setters ──────────────────────────────────────────────────────
    public void setName(String newName) {this.name = newName;}
    public void setPassword(String newPassword) {this.password = newPassword;} 
    public void setVIP(boolean isVIP) {this.isVIP = isVIP;}
    public void setBookingHistory(List<BookTicket> newBookingHistory) {
        this.bookingHistory = newBookingHistory;
    }  
}
