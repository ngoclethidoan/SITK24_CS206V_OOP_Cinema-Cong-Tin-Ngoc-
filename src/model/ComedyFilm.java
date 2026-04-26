package model;

public class ComedyFilm extends Film {
    public ComedyFilm(String codeFilm, String title, int duration, 
            double price, String director, String cast, 
            String summary, String imagePath, State state) {

        super(codeFilm, title, duration, price,  director, cast, summary, imagePath,state);
    }
}
