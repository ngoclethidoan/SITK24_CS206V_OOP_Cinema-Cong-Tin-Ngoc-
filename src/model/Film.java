package model;

public class Film {

    public enum State {
        COMING_SOON,
        NOW_SHOWING,
        ENDED
    }

    private final String codeFilm;
    private final String title;
    private final int duration;
    private final double price;

    private String director;
    private String cast;
    private String summary;
    private String imagePath;

    // 🔥 IMPORTANT: mapping film → room
    private String roomId;

    private State state;

    public Film(String codeFilm, String title, int duration,
                double price, String director, String cast,
                String summary, String imagePath,
                String roomId, State state) {

        this.codeFilm = codeFilm;
        this.title = title;
        this.duration = duration;
        this.price = price;

        this.director = director;
        this.cast = cast;
        this.summary = summary;
        this.imagePath = imagePath;

        this.roomId = roomId;
        this.state = state;
    }

    // ── GETTERS ─────────────────────
    public String getCodeFilm() { return codeFilm; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public double getPrice() { return price; }

    public String getRoomId() { return roomId; }

    public State getState() { return state; }

    public String getDirector() { return director; }
    public String getCast() { return cast; }
    public String getSummary() { return summary; }
    public String getImagePath() { return imagePath; }

    // ── SETTERS ─────────────────────
    public void setState(State state) {
        this.state = state;
    }
}