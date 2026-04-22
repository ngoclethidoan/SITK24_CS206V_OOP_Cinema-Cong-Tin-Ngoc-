/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
public class Room {  
    private final String roomId;
    private final int roomSize;
    State state;
    
    public enum State {
        occupied,
        unoccupied;
    }
        
    public Room(String roomId, int roomSize) {
        this.roomId = roomId;
        this.roomSize = roomSize;
        this.state = State.occupied;
    }
    
    // ── Getters ──────────────────────────────────────────────────────
    public String getRoomId() {return this.roomId;}
    public State getState() {return this.state;}
    
    // ── Setters ──────────────────────────────────────────────────────
    public void setState(State newState) {this.state = newState;}
    
    public boolean isOccupied() {return this.state == State.occupied;}
}
