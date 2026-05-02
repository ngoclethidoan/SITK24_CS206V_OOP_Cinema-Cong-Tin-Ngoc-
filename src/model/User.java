package model;

import java.util.List;
import java.util.ArrayList;

public class User {
    private String name;
    private String userId;        // mutable – có thể đổi qua Settings
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
        this.isAdmin = true;
        this.bookingHistory = bookingHistory != null ? bookingHistory : new ArrayList<>();
    }

    public String getName()     { return name; }
    public String getUserId()   { return userId; }
    public String getPassword() { return password; }
    public boolean isAdmin()    { return isAdmin; }
    public boolean isVIP()      { return isVIP; }

    public List<CartItem>    getCart()           { return cart; }
    public List<BookTicket>  getBookingHistory() { return bookingHistory; }

    public void setName(String newName)         { this.name     = newName; }
    public void setUserId(String newId)         { this.userId   = newId; }
    public void setPassword(String newPassword) { this.password = newPassword; }
    public void setVIP(boolean isVIP)           { this.isVIP    = isVIP; }
    public void setAdmin(boolean isAdmin)       { this.isAdmin  = isAdmin; }
    
    public void addBooking(BookTicket ticket) {
        bookingHistory.add(ticket);
    }
}