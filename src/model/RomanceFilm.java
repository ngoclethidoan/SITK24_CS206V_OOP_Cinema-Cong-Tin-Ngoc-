package model;

import java.time.LocalDateTime;

public class RomanceFilm extends Film {
    public RomanceFilm(Room room, String codeFilm, String title, int duration, double price, 
                      LocalDateTime showtime, State state, String director, String cast, 
                      String summary, String imagePath) {
        // Phải truyền đủ 11 tham số này lên cha
        super(room, codeFilm, title, duration, price, showtime, state, director, cast, summary, imagePath);
    }
}

