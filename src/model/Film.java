package model;

public class Film {

    public enum State {
        COMING_SOON,
        NOW_SHOWING,
        ENDED
    }


    private final String codeFilm;     // Example: F001
    private final String title;        // Name
    private final int duration;        // Minus
    protected final double price;  // Base price
    protected String director;
    protected String cast;
    protected String summary;
    protected String imagePath; //films must be save with rule
    private State state;

    public Film(String codeFilm, String title, int duration, 
            double price, String director, String cast, String summary, String imagePath, State state) {
      
        this.codeFilm = codeFilm;
        this.title = title;
        this.duration = duration;
        this.price = price;
        
        this.director = director;
        this.cast = cast;
        this.summary = summary;
        this.imagePath = imagePath; 
        this.state = state;
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
