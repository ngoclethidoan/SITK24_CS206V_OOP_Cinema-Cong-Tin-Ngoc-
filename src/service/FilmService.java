package service;

import database.FilmDatabase;
import interfaces.IFilmService;
import java.util.List;
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
    
    @Override
    public boolean isBookable(Film film) {
        // Chỉ cho phép đặt vé hoặc thêm vào giỏ nếu trạng thái là NOW_SHOWING
        return film != null && film.getState() == Film.State.NOW_SHOWING;
    }
    
    public void updateFilm(Film updated) {
    List<Film> films = FilmDatabase.getFilms();
    for (int i = 0; i < films.size(); i++) {
        if (films.get(i).getCodeFilm().equalsIgnoreCase(updated.getCodeFilm())) {
            films.set(i, updated);
            return;
        }
    }
}
}