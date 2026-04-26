package model;



public class AnimationFilm extends Film {
    public AnimationFilm(String codeFilm, String title, int duration, 
            double price, String director, String cast, 
            String summary, String imagePath, State state) {

        super(codeFilm, title, duration, price,  director, cast, summary, imagePath,state);
    }
}