package model;

import GUI.LanguageManager;

public class Film {

    public enum State {
        COMING_SOON,
        NOW_SHOWING,
        ENDED
    }

    private final String codeFilm;

    // ── EN (default) ──
    private String title;
    private String summary;

    // ── VI ──
    private String titleVI;
    private String summaryVI;

    // ── JP ──
    private String titleJP;
    private String summaryJP;

    private final int duration;
    private final double price;

    private String director;
    private String cast;
    private String imagePath;
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

    // ───────────────── GETTERS ─────────────────
    public String getCodeFilm() { return codeFilm; }

    public int getDuration() { return duration; }
    public double getPrice() { return price; }

    public String getDirector() { return director; }
    public String getCast() { return cast; }
    public String getImagePath() { return imagePath; }
    public String getRoomId() { return roomId; }
    public State getState() { return state; }

    // ───────────────── i18n TITLE ─────────────────
    public String getTitle() {
        LanguageManager.Language lang = LanguageManager.getInstance().getCurrent();

        return switch (lang) {
            case VIETNAMESE -> titleVI != null ? titleVI : title;
            case JAPANESE -> titleJP != null ? titleJP : title;
            default -> title;
        };
    }

    // ───────────────── i18n SUMMARY ─────────────────
    public String getSummary() {
        LanguageManager.Language lang = LanguageManager.getInstance().getCurrent();

        return switch (lang) {
            case VIETNAMESE -> summaryVI != null ? summaryVI : summary;
            case JAPANESE -> summaryJP != null ? summaryJP : summary;
            default -> summary;
        };
    }

    // ───────────────── SETTERS (i18n fields) ─────────────────
    public void setTitleVI(String titleVI) {
        this.titleVI = titleVI;
    }

    public void setTitleJP(String titleJP) {
        this.titleJP = titleJP;
    }

    public void setSummaryVI(String summaryVI) {
        this.summaryVI = summaryVI;
    }

    public void setSummaryJP(String summaryJP) {
        this.summaryJP = summaryJP;
    }

    // ───────────────── STATE ─────────────────
    public void setState(State state) {
        this.state = state;
    }
}