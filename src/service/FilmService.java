package service;

import database.FilmDatabase;
import interfaces.IFilmService;
import model.*;

public class FilmService implements IFilmService {

    @Override
    public void changeState(Film film, Film.State state) {
        film.setState(state);
    }

    @Override
    public void addFilm(Film film) {
        FilmDatabase.addFilm(film);
    }

    @Override
    public void removeFilm(String codeFilm) {
        FilmDatabase.removeFilm(codeFilm);
    }

    @Override
    public void saveFilms() {
        FilmDatabase.saveToCSV();
    }
}