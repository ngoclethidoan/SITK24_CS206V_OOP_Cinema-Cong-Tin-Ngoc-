package model;

public class RomanceFilm extends Film {
    public RomanceFilm(String codeFilm, String title, int duration, 
            double price, String director, String cast, 
            String summary, String imagePath, State state) {

        super(codeFilm, title, duration, price,  director, cast, summary, imagePath,state);
    }
}

