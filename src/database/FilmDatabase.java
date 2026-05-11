package database;

import model.Film;
import java.io.*;
import java.util.*;

public class FilmDatabase {

    private static List<Film> films = new ArrayList<>();

    public static void initDatabase() {
        films.clear();
        File file = new File("Data/films.csv");
        if (!file.exists()) { System.out.println("films.csv not found!"); return; }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("//", -1);
                if (data.length < 14) continue;
                int duration; double price;
                try {
                    duration = Integer.parseInt(data[4].trim());
                    price    = Double.parseDouble(data[5].trim());
                } catch (Exception e) { continue; }
                Film.State state;
                try { state = Film.State.valueOf(data[13].trim()); }
                catch (Exception e) { state = Film.State.ENDED; }
                Film film = new Film(
                    data[0].trim(), data[1].trim(), duration, price,
                    data[6].trim(), data[7].trim(), data[8].trim(),
                    data[11].trim(), data[12].trim(), state
                );
                film.setTitleVI(data[2].trim());   film.setTitleJP(data[3].trim());
                film.setSummaryVI(data[9].trim()); film.setSummaryJP(data[10].trim());
                films.add(film);
            }
        } catch (Exception e) { System.err.println("Film DB error: " + e.getMessage()); }
    }

    public static List<Film> getFilms()       { if (films.isEmpty()) initDatabase(); return films; }
    public static List<Film> getUniqueFilms() { return getFilms(); }

    public static Film getById(String id) {
        for (Film f : getFilms()) if (f.getCodeFilm().equals(id)) return f;
        return null;
    }
    public static Film findByCode(String code) {
        for (Film f : films) if (f.getCodeFilm().equalsIgnoreCase(code)) return f;
        return null;
    }

    // ── ADMIN CRUD ──────────────────────────────────────────
    public static void addFilm(Film film)          { films.add(film); }
    public static void removeFilm(String codeFilm) {
        films.removeIf(f -> f.getCodeFilm().equalsIgnoreCase(codeFilm));
    }

    public static void saveToCSV() {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("Data/films.csv"), "UTF-8")))) {
            for (Film f : films) {
                pw.println(String.join("//",
                    f.getCodeFilm(), f.getTitle(),
                    nvl(f.getTitleVI()), nvl(f.getTitleJP()),
                    String.valueOf(f.getDuration()), String.valueOf(f.getPrice()),
                    nvl(f.getDirector()), nvl(f.getCast()),
                    nvl(f.getSummary()), nvl(f.getSummaryVI()), nvl(f.getSummaryJP()),
                    nvl(f.getImagePath()), nvl(f.getRoomId()), f.getState().name()
                ));
            }
        } catch (IOException e) { System.err.println("FilmDatabase: cannot save – " + e.getMessage()); }
    }

    private static String nvl(String s) { return s != null ? s : ""; }
}