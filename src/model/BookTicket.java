/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
import java.util.ArrayList;
import java.util.List;

public class BookTicket {

    private final Room room;
    private final Seat seat;
    private final Film film;
    private final List<Item> items;

    public BookTicket(Room room, Seat seat, Film film) {
        this.room = room;
        this.seat = seat;
        this.film = film;
        this.items = new ArrayList<>();
    }
    
    // ── Getters ──────────────────────────────────────────────────────
    public Room getRoom() {return this.room;}
    public Seat getSeat() {return this.seat;}
    public Film getFilm() {return this.film;}
    public List<Item> getItems() {return this.items;}
}