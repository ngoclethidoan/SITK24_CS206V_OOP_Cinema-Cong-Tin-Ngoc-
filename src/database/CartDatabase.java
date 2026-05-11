package database;

import model.*;
import java.io.*;
import java.util.*;

/**
 * CartDatabase – saves and loads a user's pending cart to Data/cart.csv
 *
 * Format per line:
 *   userId|TICKET|filmID|roomID|seatID
 *   userId|SNACK|itemCode:qty;itemCode:qty
 */
public class CartDatabase {

    private static final String FILE      = "Data/cart.csv";
    private static final String TICKET    = "TICKET";
    private static final String SNACK     = "SNACK";
    private static final String COL_SEP   = "|";
    private static final String ITEM_SEP  = ";";
    private static final String QTY_SEP   = ":";

    // ── SAVE ─────────────────────────────────────────────────────────
    /**
     * Saves the user's current cart to file.
     * Replaces any previous cart rows for this userId.
     */
    public static void save(User user) {
        if (user == null) return;

        // Read all rows that belong to OTHER users
        List<String> otherRows = new ArrayList<>();
        File f = new File(FILE);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] cols = line.split("\\|", -1);
                    if (cols.length > 0 && !cols[0].equalsIgnoreCase(user.getUserId())) {
                        otherRows.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("CartDatabase: read error – " + e.getMessage());
            }
        }

        // Write other users' rows + this user's current cart
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE)))) {
            // Keep other users' cart rows
            for (String row : otherRows) pw.println(row);

            // Write ticket cart items
            for (CartItem item : user.getCart()) {
                if (item.getFilm() == null || item.getRoom() == null || item.getSeat() == null)
                    continue;
                pw.println(String.join(COL_SEP,
                    user.getUserId(),
                    TICKET,
                    item.getFilm().getCodeFilm(),
                    item.getRoom().getRoomId(),
                    item.getSeat().getCodeSeat()
                ));
            }

            // Write snack cart items
            for (SnackCartItem si : user.getSnackCart()) {
                StringBuilder items = new StringBuilder();
                for (Item it : si.getItems()) {
                    if (it.getQuantity() <= 0) continue;
                    if (items.length() > 0) items.append(ITEM_SEP);
                    items.append(it.getCodeItem()).append(QTY_SEP).append(it.getQuantity());
                }
                if (items.length() > 0) {
                    pw.println(String.join(COL_SEP,
                        user.getUserId(),
                        SNACK,
                        items.toString()
                    ));
                }
            }

        } catch (IOException e) {
            System.err.println("CartDatabase: write error – " + e.getMessage());
        }
    }

    // ── LOAD ─────────────────────────────────────────────────────────
    /**
     * Loads saved cart items into the user's in-memory cart.
     * Call this right after login.
     */
    public static void load(User user) {
        if (user == null) return;

        user.getCart().clear();
        user.getSnackCart().clear();

        File f = new File(FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] cols = line.split("\\|", -1);
                if (cols.length < 3) continue;
                if (!cols[0].equalsIgnoreCase(user.getUserId())) continue;

                String type = cols[1];

                if (TICKET.equals(type) && cols.length >= 5) {
                    // Resolve objects
                    Film film = FilmDatabase.getById(cols[2]);
                    Room room = RoomDatabase.getRoom(cols[3]);
                    if (film == null || room == null) continue;

                    Seat seat = findSeat(room, cols[4]);
                    if (seat == null || !seat.isAvailable()) continue;

                    user.getCart().add(new CartItem(film, room, seat));

                } else if (SNACK.equals(type) && cols.length >= 3) {
                    // Parse "itemCode:qty;itemCode:qty"
                    List<Item> items = new ArrayList<>();
                    for (String part : cols[2].split(ITEM_SEP)) {
                        String[] kv = part.split(QTY_SEP, 2);
                        if (kv.length < 2) continue;
                        try {
                            int qty = Integer.parseInt(kv[1].trim());
                            Item item = ItemDatabase.getById(kv[0].trim(), qty);
                            if (item != null) items.add(item);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (!items.isEmpty()) {
                        user.addSnackToCart(new SnackCartItem(items));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("CartDatabase: load error – " + e.getMessage());
        }
    }

    // ── HELPER ───────────────────────────────────────────────────────
    private static Seat findSeat(Room room, String seatCode) {
        for (Seat[] row : room.getSeats()) {
            for (Seat seat : row) {
                if (seat != null && seat.getCodeSeat().equalsIgnoreCase(seatCode))
                    return seat;
            }
        }
        return null;
    }
}