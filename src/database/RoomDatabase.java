package database;

import model.*;
import java.util.*;

public class RoomDatabase {

    private static final Map<String, Room> rooms = new HashMap<>();

    public static void init() {
        rooms.clear();
        for (int i = 1; i <= 14; i++) createRoom("R" + i);
    }

    /**
     * Creates a room with realistic mixed seat layout:
     *   Row A-B : StandardSeat  (80,000 VND)
     *   Row C   : ReclineSeat   (80,000 * 1.3 = 104,000 VND)
     *   Row D   : VIPSeat       (80,000 * 1.5 = 120,000 VND)
     *   Row E   : PremiumSeat   (80,000 * 2.0 = 160,000 VND)
     *   Row F   : CoupleSeat    (80,000 * 1.8 = 144,000 VND)
     */
    private static void createRoom(String id) {
        int rows = 6, cols = 8;
        double base = 80_000;
        Room room = new Room(id, rows, cols);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String code = (char)('A' + r) + "" + (c + 1);
                Seat seat = switch (r) {
                    case 0, 1 -> new StandardSeat(code, r, c, base);
                    case 2    -> new ReclineSeat(code, r, c, base);
                    case 3    -> new VIPSeat(code, r, c, base);
                    case 4    -> new PremiumSeat(code, r, c, base);
                    case 5    -> new CoupleSeat(code, r, c, base);
                    default   -> new StandardSeat(code, r, c, base);
                };
                seat.setRoom(room);
                room.setSeat(r, c, seat);
            }
        }
        rooms.put(id, room);
    }

    public static Room getRoom(String id) { return rooms.get(id); }

    public static void bookSeat(String roomId, Seat seat) {
        Room room = rooms.get(roomId);
        if (room == null) return;
        room.getSeat(seat.getRow(), seat.getColumn()).setState(Seat.State.booked);
    }
}
