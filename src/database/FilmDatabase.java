package database;

import model.Film;
import java.io.*;
import java.util.*;

public class FilmDatabase {
    // 1. THE KEEPER: The list that holds the data
    private static List<Film> films = new ArrayList<>();

    //THE READER: replaces your old CSVReader class
    public static void initDatabase() {
        films.clear(); // Start fresh
        try (BufferedReader br = new BufferedReader(new FileReader("Data/films.csv"))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
        continue; 
    }
                
                String[] data = line.split("//",9);
                
                String id = data[0].trim();
                String title = data[1].trim();
                int duration = Integer.parseInt(data[2].trim());
                double price = Double.parseDouble(data[3].trim());
                String director = data[4].trim();
                String cast = data[5].trim();
                String summary = data[6].trim();
                String imagePath = data[7].trim();
                String statusString = data[8].trim(); 
                Film.State filmStatus = Film.State.valueOf(statusString); 
               

films.add(new Film(id, title, duration, price, 
        director, cast, summary, imagePath, filmStatus));
            }
        } catch (IOException e) {
            System.err.println("Error loading films: " + e.getMessage());
        }
    }

    // 3. THE PROVIDER: How the GUI gets the films
    public static List<Film> getUniqueFilms() {
        if (films.isEmpty()) initDatabase(); // Auto-load if empty
        return films;
    }
}