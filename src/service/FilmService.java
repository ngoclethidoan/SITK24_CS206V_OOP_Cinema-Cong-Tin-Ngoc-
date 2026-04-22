/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Administrator
 */
import model.*;
public class FilmService {
    
    // Changes of the film by Adminitrator
    public void changeState(Film film, Film.State state) {
        film.setState(state);
    }
}
