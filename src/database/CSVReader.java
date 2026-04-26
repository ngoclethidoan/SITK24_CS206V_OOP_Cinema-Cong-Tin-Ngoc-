//package database;
//
//import model.*;
//import java.io.*;
//import java.time.LocalDateTime;
//import java.util.*;
//
//public class CSVReader {
//    private static final String FILMS_PATH = "Data/films.csv";
//    private static final String SHOWTIMES_PATH = "Data/showtimes.csv";
//
//    public static List<Film> loadAllData() {
//        Map<String, Film> filmPrototypes = new HashMap<>();
//        List<Film> finalFilmList = new ArrayList<>();
//
//        try {
//            String line;
//            // --- Bước 1: Đọc thông tin phim gốc ---
//            try (BufferedReader brFilms = new BufferedReader(new FileReader(FILMS_PATH))) {
//                brFilms.readLine(); // Bỏ qua header của file films.csv
//                while ((line = brFilms.readLine()) != null) {
//                    if (line.trim().isEmpty()) continue;
//                    String[] data = line.split(",");
//                    if (data.length < 9) continue;
//
//                    String type = data[0].trim();
//                    String code = data[1].trim();
//                    String title = data[2].trim();
//                    int duration = Integer.parseInt(data[3].trim());
//                    double price = Double.parseDouble(data[4].trim());
//                    
//                    Film film;
//                    if (type.equalsIgnoreCase("ACT")) {
//                        film = new ActionFilm(null, code, title, duration, price, null, Film.State.NOW_SHOWING, data[5], data[6], data[7], data[8]);
//                    } else {
//                        film = new ComedyFilm(null, code, title, duration, price, null, Film.State.NOW_SHOWING, data[5], data[6], data[7], data[8]);
//                    }
//                    filmPrototypes.put(code, film);
//                }
//            }
//
//            // --- Bước 2: Đọc lịch chiếu ---
//            try (BufferedReader brShows = new BufferedReader(new FileReader(SHOWTIMES_PATH))) {
//                brShows.readLine(); // 🔥 BỎ QUA DÒNG TIÊU ĐỀ (dateTime)
//                
//                while ((line = brShows.readLine()) != null) {
//                    if (line.trim().isEmpty()) continue;
//                    String[] data = line.split(",");
//                    
//                    Film proto = filmPrototypes.get(data[0].trim());
//                    if (proto != null) {
//                        LocalDateTime time = LocalDateTime.parse(data[2].trim());
//                        
//                        if (proto instanceof ActionFilm) {
//                            finalFilmList.add(new ActionFilm(null, proto.getCodeFilm(), proto.getTitle(), 
//                                proto.getDuration(), proto.getPrice(), time, proto.getState(), 
//                                proto.getDirector(), proto.getCast(), proto.getSummary(), proto.getImagePath()));
//                        } else {
//                            finalFilmList.add(new ComedyFilm(null, proto.getCodeFilm(), proto.getTitle(), 
//                                proto.getDuration(), proto.getPrice(), time, proto.getState(), 
//                                proto.getDirector(), proto.getCast(), proto.getSummary(), proto.getImagePath()));
//                        }
//                    }
//                }
//            }
//            
//            System.out.println(">>> ĐÃ NẠP THÀNH CÔNG: " + finalFilmList.size() + " SUẤT CHIẾU.");
//
//        } catch (Exception e) {
//            System.err.println("Lỗi nạp dữ liệu: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return finalFilmList;
//    }
//}