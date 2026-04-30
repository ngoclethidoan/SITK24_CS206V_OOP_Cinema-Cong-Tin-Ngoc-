package database;

import model.Film;
import java.io.*;
import java.util.*;

public class FilmDatabase {

    private static List<Film> films = new ArrayList<>();

    // ───────────────── INIT DATABASE ─────────────────
    public static void initDatabase() {

        films.clear();

        File file = new File("Data/films.csv");

        if (!file.exists()) {
            System.out.println("films.csv not found!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] data = line.split("//");

                if (data.length < 10) {
                    System.out.println("Invalid row: " + line);
                    continue;
                }

                String id = data[0].trim();
                String title = data[1].trim();
                int duration = Integer.parseInt(data[2].trim());
                double price = Double.parseDouble(data[3].trim());

                String director = data[4].trim();
                String cast = data[5].trim();
                String summary = data[6].trim();
                String imagePath = data[7].trim();

                String roomId = data[8].trim();
                Film.State state = Film.State.valueOf(data[9].trim());

                films.add(new Film(
                        id, title, duration, price,
                        director, cast, summary,
                        imagePath, roomId, state
                ));
            }

        } catch (Exception e) {
            System.err.println("Film DB error: " + e.getMessage());
        }
    }

    // ───────────────── GET ALL FILMS ─────────────────
    public static List<Film> getUniqueFilms() {
        if (films.isEmpty()) initDatabase();
        return films;
    }

    // alias (giữ compatibility với code cũ)
    public static List<Film> getFilms() {
        return getUniqueFilms();
    }

    // ───────────────── FIND BY ID ─────────────────
    public static Film getById(String id) {
        for (Film f : getFilms()) {
            if (f.getCodeFilm().equals(id)) return f;
        }
        return null;
    }
}