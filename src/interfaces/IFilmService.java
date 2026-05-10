// src/interfaces/IFilmService.java
package interfaces;
import model.Film;

public interface IFilmService {
    void changeState(Film film, Film.State state);
}