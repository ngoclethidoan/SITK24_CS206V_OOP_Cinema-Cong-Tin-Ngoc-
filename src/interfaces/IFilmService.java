package interfaces;

import model.Film;

public interface IFilmService {
    void changeState(Film film, Film.State state);
    void addFilm(Film film);
    void removeFilm(String codeFilm);
    void saveFilms();
    boolean isBookable(Film film);
}