package model;

import java.time.LocalDateTime;
public class Film {

    public enum State {
        COMING_SOON,
        NOW_SHOWING,
        ENDED
    }

    private final Room room;
    private final String codeFilm;     // Example: F001
    private final String title;        // Name
    private final int duration;        // Minus
    protected final double price;  // Base price
    private final LocalDateTime showtime; //Ex: 2026, 4, 19, 19, 04
    private State state;
    
    //
    protected String director;
    protected String cast;
    protected String summary;
    protected String imagePath; //films must be save with rule

    public Film(Room room, String codeFilm, String title, int duration, 
            double price, LocalDateTime showtime, State state,
            String director, String cast, String summary, String imagePath) {
        this.room = room;
        this.codeFilm = codeFilm;
        this.title = title;
        this.duration = duration;
        this.price = price;
        this.showtime = showtime;
        this.state = state;
        this.director = director;
        this.cast = cast;
        this.summary = summary;
        this.imagePath = imagePath; 
    }

    // ── Getters ──────────────────────────────────────────────────────
    public String getCodeFilm() { return codeFilm; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public double getPrice() { return price; }
    public State getState() { return state; }
    //
    public String getDirector() { return director; }
    public String getCast() { return cast; }
    public String getSummary() { return summary; }
    public String getImagePath() { return imagePath; }
    
    // ── Setters ──────────────────────────────────────────────────────
    public void setState(State newState) {this.state = newState;}

    // ── State transitions ───────────────────────────────────────────
    public boolean isComingSoon() { return state == State.COMING_SOON; }
    public boolean isNowShowing() { return state == State.NOW_SHOWING; }
    public boolean isEnded() { return state == State.ENDED; }

    @Override
    public String toString() {
        return String.format("[%s | %s | %d mins | %.0f VND]", 
                codeFilm, title, duration, getPrice());
    }
}
