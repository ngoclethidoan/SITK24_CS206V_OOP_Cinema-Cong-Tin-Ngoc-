package database;

import model.*;

import java.util.*;

public class RoomDatabase {

    private static Map<String, Room> rooms = new HashMap<>();

    public static void init() {
        rooms.clear();
        for (int i = 1; i <= 14; i++) {
            createRoom("R" + i, 6, 8);
        }
    }

    private static void createRoom(String id, int r, int c) {

        Room room = new Room(id, r, c);

        double base = 80000;

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {

                String code = (char) ('A' + i) + "" + (j + 1);

                room.setSeat(i, j,
                        new StandardSeat(code, i, j, base));
            }
        }

        rooms.put(id, room);
    }

    public static Room getRoom(String id) {
        return rooms.get(id);
    }

    // 🔥 IMPORTANT: update seat state globally
    public static void bookSeat(String roomId, Seat seat) {

        Room room = rooms.get(roomId);
        if (room == null) return;

        room.getSeat(seat.getRow(), seat.getColumn())
                .setState(Seat.State.booked);
    }
}