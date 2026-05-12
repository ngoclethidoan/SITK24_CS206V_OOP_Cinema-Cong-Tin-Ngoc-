package database;

import model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BookingDatabase – saves/loads bookings to Data/bookings.csv
 * Format: userID|filmID|roomID|seatID|itemIDs|STATUS|bookingId
 */
public class BookingDatabase {

    private static final String CSV_FILE   = "Data/bookings.csv";
    private static final String CSV_HEADER = "userID|filmID|roomID|seatID|itemIDs|STATUS|bookingId";

    private static final String COL_SEP  = "|";
    private static final String ITEM_SEP = ";";
    private static final String QTY_SEP  = ":";

    public static final int COL_USER      = 0;
    public static final int COL_FILM      = 1;
    public static final int COL_ROOM      = 2;
    public static final int COL_SEAT      = 3;
    public static final int COL_ITEMS     = 4;
    public static final int COL_STATUS    = 5;
    public static final int COL_BOOK_ID   = 6;

    public static final String STATUS_PAID    = "PAID";
    public static final String STATUS_PENDING = "PENDING";

    // ── BOOKING ID ───────────────────────────────────────────────────
    public static String generateBookingId(String userId) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "BK-" + userId.toUpperCase() + "-" + ts;
    }

    public static String getBookingId(String[] row) {
        if (row.length > COL_BOOK_ID && !row[COL_BOOK_ID].isBlank())
            return row[COL_BOOK_ID];
        // Fallback for old rows without bookingId
        return "BK-" + row[COL_USER] + "-" + row[COL_FILM] + "-" + row[COL_SEAT];
    }

    // ── SAVE COMPLETED BOOKING ───────────────────────────────────────
    public static void save(String userId, BookTicket ticket, List<Item> items) {
        ensureFileExists();
        String filmId    = ticket.getFilm() != null ? ticket.getFilm().getCodeFilm() : "SNACK_ONLY";
        String roomId    = ticket.getRoom() != null ? ticket.getRoom().getRoomId()   : "-";
        String seatId    = ticket.getSeat() != null ? ticket.getSeat().getCodeSeat() : "-";
        String itemIds   = encodeItems(items);
        String bookingId = generateBookingId(userId);
        String row = String.join(COL_SEP, userId, filmId, roomId, seatId, itemIds, STATUS_PAID, bookingId);
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE, true)))) {
            pw.println(row);
        } catch (IOException e) {
            System.err.println("BookingDatabase: write error – " + e.getMessage());
        }
    }

    public static void saveAll(String userId, List<BookTicket> tickets, List<Item> items) {
        for (int i = 0; i < tickets.size(); i++)
            save(userId, tickets.get(i), i == 0 ? items : null);
    }

    // ── SAVE PENDING TICKET ──────────────────────────────────────────
    public static void savePendingTicket(String userId, Film film, Room room, Seat seat) {
        ensureFileExists();
        String filmId = film != null ? film.getCodeFilm() : "-";
        String roomId = room != null ? room.getRoomId()   : "-";
        String seatId = seat != null ? seat.getCodeSeat() : "-";
        String row = String.join(COL_SEP, userId, filmId, roomId, seatId, "", STATUS_PENDING, "");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE, true)))) {
            pw.println(row);
        } catch (IOException e) {
            System.err.println("BookingDatabase: pending write error – " + e.getMessage());
        }
    }

    // ── SAVE PENDING SNACK ───────────────────────────────────────────
    public static void savePendingSnack(String userId, List<Item> items) {
        ensureFileExists();
        String itemIds = encodeItems(items);
        if (itemIds.isEmpty()) return;
        String row = String.join(COL_SEP, userId, "SNACK_ONLY", "-", "-", itemIds, STATUS_PENDING, "");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE, true)))) {
            pw.println(row);
        } catch (IOException e) {
            System.err.println("BookingDatabase: pending snack error – " + e.getMessage());
        }
    }

    // ── REMOVE PENDING ───────────────────────────────────────────────
    public static void removePending(String userId) {
        rewriteWithout(userId, STATUS_PENDING, null, null);
    }

    public static void removePendingTicket(String userId, String filmId, String seatId) {
        rewriteWithout(userId, STATUS_PENDING, filmId, seatId);
    }

    private static void rewriteWithout(String userId, String status, String filmId, String seatId) {
        List<String[]> all = readAll();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(CSV_FILE), "UTF-8")))) {
            pw.println(CSV_HEADER);
            for (String[] r : all) {
                String rowStatus = r.length > COL_STATUS ? r[COL_STATUS] : STATUS_PAID;
                boolean matchUser   = r[COL_USER].equalsIgnoreCase(userId);
                boolean matchStatus = rowStatus.equals(status);
                boolean matchFilm   = filmId == null || r[COL_FILM].equals(filmId);
                boolean matchSeat   = seatId == null || r[COL_SEAT].equals(seatId);
                if (matchUser && matchStatus && matchFilm && matchSeat) continue;
                pw.println(String.join(COL_SEP, r));
            }
        } catch (IOException e) {
            System.err.println("BookingDatabase: rewrite error – " + e.getMessage());
        }
    }

    // ── LOAD PENDING CART ────────────────────────────────────────────
    public static void loadPendingCart(User user) {
        if (user == null) return;
        user.getCart().clear();
        user.getSnackCart().clear();

        for (String[] row : readAll()) {
            if (!row[COL_USER].equalsIgnoreCase(user.getUserId())) continue;
            String status = row.length > COL_STATUS ? row[COL_STATUS] : STATUS_PAID;
            if (!STATUS_PENDING.equals(status)) continue;

            if ("SNACK_ONLY".equals(row[COL_FILM])) {
                List<Item> items = resolveItems(row);
                if (!items.isEmpty()) user.addSnackToCart(new SnackCartItem(items));
            } else {
                Film film = resolveFilm(row);
                Room room = resolveRoom(row);
                Seat seat = resolveSeat(row);
                if (film != null && room != null && seat != null) {
                    user.getCart().add(new CartItem(film, room, seat));
                    seat.setState(Seat.State.booked);
                }
            }
        }
    }

    // ── RESTORE BOOKED SEATS ON STARTUP ─────────────────────────────
    public static void restoreBookedSeats() {
        for (String[] row : readAll()) {
            if ("-".equals(row[COL_SEAT]) || "SNACK_ONLY".equals(row[COL_FILM])) continue;
            Seat seat = resolveSeat(row);
            if (seat != null) seat.setState(Seat.State.booked);
        }
    }

    // ── READ ─────────────────────────────────────────────────────────
    public static List<String[]> readAll() {
        List<String[]> rows = new ArrayList<>();
        File f = new File(CSV_FILE);
        if (!f.exists()) return rows;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("userID")) continue;
                String[] cols = line.split("\\|", -1);
                if (cols.length >= 5) rows.add(cols);
            }
        } catch (IOException e) {
            System.err.println("BookingDatabase: read error – " + e.getMessage());
        }
        return rows;
    }

    public static List<String[]> getByUser(String userId) {
        List<String[]> result = new ArrayList<>();
        for (String[] row : readAll()) {
            String status = row.length > COL_STATUS ? row[COL_STATUS] : STATUS_PAID;
            if (row[COL_USER].equalsIgnoreCase(userId) && STATUS_PAID.equals(status))
                result.add(row);
        }
        return result;
    }

    // ── RESOLVE ──────────────────────────────────────────────────────
    public static Film resolveFilm(String[] row) { return FilmDatabase.getById(row[COL_FILM]); }
    public static Room resolveRoom(String[] row) { return RoomDatabase.getRoom(row[COL_ROOM]); }

    public static Seat resolveSeat(String[] row) {
        Room room = resolveRoom(row);
        if (room == null) return null;
        String code = row[COL_SEAT];
        for (Seat[] seatRow : room.getSeats())
            for (Seat seat : seatRow)
                if (seat != null && seat.getCodeSeat().equalsIgnoreCase(code)) return seat;
        return null;
    }

    public static List<Item> resolveItems(String[] row) {
        return ItemDatabase.resolveItems(resolveItemEntries(row));
    }

    public static List<String[]> resolveItemEntries(String[] row) {
        List<String[]> entries = new ArrayList<>();
        if (row.length <= COL_ITEMS || row[COL_ITEMS].isBlank()) return entries;
        for (String part : row[COL_ITEMS].split(ITEM_SEP)) {
            String[] kv = part.split(QTY_SEP, 2);
            if (kv.length == 2) entries.add(kv);
        }
        return entries;
    }

    // ── TOTAL ────────────────────────────────────────────────────────
    public static double totalPrice(String[] row) {
        double total = 0;
        Seat seat = resolveSeat(row);
        if (seat != null) total += seat.computePrice();
        total += ItemDatabase.totalItemPrice(resolveItems(row));
        return total;
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private static String encodeItems(List<Item> items) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            if (sb.length() > 0) sb.append(ITEM_SEP);
            sb.append(item.getCodeItem()).append(QTY_SEP).append(item.getQuantity());
        }
        return sb.toString();
    }

    private static void ensureFileExists() {
        new File("Data").mkdirs();
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println(CSV_HEADER);
            } catch (IOException e) {
                System.err.println("BookingDatabase: cannot create – " + e.getMessage());
            }
        }
    }
}
