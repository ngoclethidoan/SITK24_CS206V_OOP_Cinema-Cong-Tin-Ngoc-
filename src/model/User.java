package model;

import java.util.List;
import java.util.ArrayList;

public class User {
    private String name;
    private final String userId;
    private String password;
    private boolean isVIP;
    private boolean isAdmin;

    private List<CartItem> cart = new ArrayList<>();
    private List<BookTicket> bookingHistory = new ArrayList<>();

    public User(String name, String userId, String password, List<BookTicket> bookingHistory) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.isVIP = false;
        this.bookingHistory = bookingHistory;
        this.isAdmin = false;
    }

    public String getName() { return this.name; }
    public String getUserId() { return this.userId; }
    public String getPassword() { return this.password; }
    public boolean isAdmin() { return isAdmin;}

    public List<CartItem> getCart() { return cart; }
    public List<BookTicket> getBookingHistory() { return bookingHistory; }

    public void setName(String newName) { this.name = newName; }
    public void setPassword(String newPassword) { this.password = newPassword; }
    public void setVIP(boolean isVIP) { this.isVIP = isVIP; }
}   