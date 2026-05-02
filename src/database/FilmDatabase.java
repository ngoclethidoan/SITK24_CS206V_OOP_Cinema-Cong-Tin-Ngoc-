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

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                // safer split
                String[] data = line.split("//", -1);

                // EXPECT 14 FIELDS
                if (data.length < 14) {
                    System.out.println("Invalid row: " + line);
                    continue;
                }

                String id = data[0].trim();

                // ── TITLES ──
                String titleEN = data[1].trim();
                String titleVI = data[2].trim();
                String titleJP = data[3].trim();

                int duration;
                double price;

                try {
                    duration = Integer.parseInt(data[4].trim());
                    price = Double.parseDouble(data[5].trim());
                } catch (Exception e) {
                    System.out.println("Invalid number in row: " + line);
                    continue;
                }

                String director = data[6].trim();
                String cast = data[7].trim();

                // ── SUMMARIES ──
                String summaryEN = data[8].trim();
                String summaryVI = data[9].trim();
                String summaryJP = data[10].trim();

                String imagePath = data[11].trim();
                String roomId = data[12].trim();

                Film.State state;
                try {
                    state = Film.State.valueOf(data[13].trim());
                } catch (Exception e) {
                    state = Film.State.ENDED; // fallback safety
                }

                // ── CREATE FILM ──
                Film film = new Film(
                        id,
                        titleEN,
                        duration,
                        price,
                        director,
                        cast,
                        summaryEN,
                        imagePath,
                        roomId,
                        state
                );

                // ── I18N FIELDS ──
                film.setTitleVI(titleVI);
                film.setTitleJP(titleJP);

                film.setSummaryVI(summaryVI);
                film.setSummaryJP(summaryJP);

                films.add(film);
            }

        } catch (Exception e) {
            System.err.println("Film DB error: " + e.getMessage());
        }
    }

    // ───────────────── GET ALL FILMS ─────────────────
    public static List<Film> getFilms() {
        if (films.isEmpty()) initDatabase();
        return films;
    }

    // alias
    public static List<Film> getUniqueFilms() {
        return getFilms();
    }

    // ───────────────── FIND BY ID ─────────────────
    public static Film getById(String id) {
        for (Film f : getFilms()) {
            if (f.getCodeFilm().equals(id)) {
                return f;
            }
        }
        return null;
    }
    
    public static Film findByCode(String code) {
        for (Film f : films) {
            if (f.getCodeFilm().equalsIgnoreCase(code)) {
                return f;
            }
        }
        return null;
    }
}