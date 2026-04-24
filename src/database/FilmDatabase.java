package database;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDatabase {
    private static List<Film> listFilm = new ArrayList<>();

    // This static block will automatically run to load data when the program starts.
    static {
        listFilm.add(new ActionFilm(null, "ACT01", "John Wick: Chapter 4", 169, 10.0, null, null, 
            "Chad Stahelski", "Keanu Reeves, Donnie Yen", 
            "John Wick tìm ra con đường để đánh bại High Table. Nhưng trước khi giành lại tự do, Wick phải đối mặt với một kẻ thù mới.", 
       "posters/ACT01.jpg"));

        listFilm.add(new ComedyFilm(null, "COM01", "Despicable Me 4", 94, 8.0, null, null, 
            "Chris Renaud", "Steve Carell, Kristen Wiig", 
            "Gru và gia đình chào đón một thành viên mới, Gru Jr., người có ý định hành hạ cha mình.",
        "posters/COM01.jpeg"));

        listFilm.add(new ActionFilm(null, "ACT02", "Deadpool & Wolverine", 127, 12.0, null, null, 
            "Shawn Levy", "Ryan Reynolds, Hugh Jackman", 
            "Một nhiệm vụ vô vọng khiến Deadpool phải hợp tác với Wolverine để cứu lấy vũ trụ của mình.",
        "posters/ACT02.jpg"));
        listFilm.add(new ActionFilm(
    null,"ACT03", "John Wick: Ballerina", 107,                                      // Thời lượng (phút)
    120000.0,null, Film.State.NOW_SHOWING, "Len Wiseman",                            // Đạo diễn
    "Ana de Armas, Keanu Reeves, Ian McShane",
    "Lấy bối cảnh giữa phần 3 và 4 của John Wick, câu chuyện theo chân Eve Macarro, một sát thủ trẻ tuổi đang tìm cách trả thù những kẻ đã sát hại gia đình mình.", 
    "posters/ACT03.jpg"                  
));
    }

    public static List<Film> getAllFilms() {
        return listFilm;
    }  
}