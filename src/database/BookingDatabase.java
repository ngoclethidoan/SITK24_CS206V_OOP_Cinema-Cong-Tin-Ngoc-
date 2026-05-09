package database;

import model.*;
import java.io.*;
import java.util.*;

/**
 * BookingDatabase – lưu/tải lịch sử đặt vé vào file Data/bookings.csv
 *
 * ─── Cấu trúc DataFrame (pandas-style) ──────────────────────────────────
 *
 *   userID | filmID | roomID | seatID | itemIDs
 *   ────────────────────────────────────────────
 *   u001   | ACT02  | R3     | B2     | CORN01:1;BEV01:2
 *   u001   | ACT02  | R3     | C4     |
 *   u002   | ANM01  | R9     | A1     | COMBO01:1
 *
 * ─── Cách tra cứu sau khi đọc ────────────────────────────────────────────
 *
 *   List<String[]> rows = BookingDatabase.getByUser("u001");
 *
 *   for (String[] row : rows) {
 *       Film film = BookingDatabase.resolveFilm(row);   // → lấy title, price
 *       Room room = BookingDatabase.resolveRoom(row);   // → lấy roomId, seats
 *       Seat seat = BookingDatabase.resolveSeat(row);   // → lấy loại ghế, computePrice()
 *
 *       // Lấy Item đầy đủ (có giá, số lượng)
 *       List<Item> items = BookingDatabase.resolveItems(row);
 *       double itemTotal = ItemDatabase.totalItemPrice(items);
 *   }
 *
 * ─── Format itemIDs ──────────────────────────────────────────────────────
 *   Nhiều item:  "CORN01:1;BEV01:2;COMBO02:1"
 *   Không có:    "" (chuỗi rỗng)
 */
public class BookingDatabase {

    // ── Đường dẫn file ──────────────────────────────────────────────────
    private static final String CSV_FILE   = "Data/bookings.csv";
    private static final String CSV_HEADER = "userID|filmID|roomID|seatID|itemIDs";

    // ── Separators ──────────────────────────────────────────────────────
    private static final String COL_SEP  = "|";   // phân cách cột
    private static final String ITEM_SEP = ";";   // phân cách nhiều item
    private static final String QTY_SEP  = ":";   // phân cách itemCode và quantity

    // Chỉ số cột (dùng như hằng số, tránh magic number)
    public static final int COL_USER   = 0;
    public static final int COL_FILM   = 1;
    public static final int COL_ROOM   = 2;
    public static final int COL_SEAT   = 3;
    public static final int COL_ITEMS  = 4;

    // ────────────────────────────────────────────────────────────────────
    //  GHI BOOKING
    // ────────────────────────────────────────────────────────────────────

    /**
     * Lưu một booking vào cuối file CSV (append).
     *
     * @param userId  ID người dùng
     * @param ticket  BookTicket đã tạo
     * @param items   Danh sách Item kèm theo (combo, đồ uống...) – có thể null/rỗng
     */
    public static void save(String userId, BookTicket ticket, List<Item> items) {
        ensureFileExists();

        String filmId  = (ticket.getFilm() != null) ? ticket.getFilm().getCodeFilm() : "SNACK_ONLY";
        String roomId  = (ticket.getRoom() != null) ? ticket.getRoom().getRoomId()   : "-";
        String seatId  = (ticket.getSeat() != null) ? ticket.getSeat().getCodeSeat() : "-";
        String itemIds = encodeItems(items);

        String row = String.join(COL_SEP, userId, filmId, roomId, seatId, itemIds);

        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(CSV_FILE, true)))) {
            pw.println(row);
        } catch (IOException e) {
            System.err.println("BookingDatabase: không thể ghi – " + e.getMessage());
        }
    }

    /**
     * Lưu nhiều vé cùng lúc (dùng sau checkout từ cart).
     * Không có item kèm (items = null).
     */
    public static void saveAll(String userId, List<BookTicket> tickets) {
        for (BookTicket t : tickets) {
            save(userId, t, null);
        }
    }

    /**
     * Lưu nhiều vé cùng với items.
     */
    public static void saveAll(String userId, List<BookTicket> tickets, List<Item> items) {
        for (int i = 0; i < tickets.size(); i++) {
            // Item chỉ gắn vào vé đầu tiên để tránh trùng lặp
            save(userId, tickets.get(i), i == 0 ? items : null);
        }
    }

    // ────────────────────────────────────────────────────────────────────
    //  ĐỌC DỮ LIỆU  (trả về List<String[]> như pandas DataFrame rows)
    // ────────────────────────────────────────────────────────────────────

    /**
     * Đọc toàn bộ booking từ CSV.
     *
     * Mỗi phần tử là String[] gồm 5 cột:
     *   [0] userID
     *   [1] filmID
     *   [2] roomID
     *   [3] seatID
     *   [4] itemIDs  ("CORN01:1;BEV01:2" hoặc "" nếu không có)
     *
     * @return List các row (không gồm header)
     */
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
                if (cols.length >= 5) {
                    rows.add(cols);
                }
            }

        } catch (IOException e) {
            System.err.println("BookingDatabase: không thể đọc – " + e.getMessage());
        }

        return rows;
    }

    /**
     * Lọc booking theo userId.
     * Tương đương pandas: df[df['userID'] == userId]
     */
    public static List<String[]> getByUser(String userId) {
        List<String[]> result = new ArrayList<>();
        for (String[] row : readAll()) {
            if (row[COL_USER].equalsIgnoreCase(userId)) {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * Lọc booking theo filmID.
     * Tương đương pandas: df[df['filmID'] == filmId]
     */
    public static List<String[]> getByFilm(String filmId) {
        List<String[]> result = new ArrayList<>();
        for (String[] row : readAll()) {
            if (row[COL_FILM].equalsIgnoreCase(filmId)) {
                result.add(row);
            }
        }
        return result;
    }

    // ────────────────────────────────────────────────────────────────────
    //  RESOLVE: dò từ ID trong CSV sang Object đầy đủ
    // ────────────────────────────────────────────────────────────────────

    /**
     * Dò filmID (cột [1]) → Film object đầy đủ (title, price, duration...).
     */
    public static Film resolveFilm(String[] row) {
        return FilmDatabase.getById(row[COL_FILM]);
    }

    /**
     * Dò roomID (cột [2]) → Room object.
     */
    public static Room resolveRoom(String[] row) {
        return RoomDatabase.getRoom(row[COL_ROOM]);
    }

    /**
     * Dò seatID (cột [3]) trong Room tương ứng → Seat object.
     * Từ Seat có thể gọi seat.computePrice() để biết giá thực.
     */
    public static Seat resolveSeat(String[] row) {
        Room room = resolveRoom(row);
        if (room == null) return null;

        String seatCode = row[COL_SEAT];
        for (Seat[] seatRow : room.getSeats()) {
            for (Seat seat : seatRow) {
                if (seat != null && seat.getCodeSeat().equalsIgnoreCase(seatCode)) {
                    return seat;
                }
            }
        }
        return null;
    }

    /**
     * Giải mã itemIDs (cột [4]) → danh sách Item đầy đủ (có tên, giá, quantity).
     *
     * Dùng trực tiếp – không cần gọi resolveItemEntries() nữa.
     *
     * Ví dụ:
     *   List&lt;Item&gt; items = BookingDatabase.resolveItems(row);
     *   double total = ItemDatabase.totalItemPrice(items);
     */
    public static List<Item> resolveItems(String[] row) {
        List<String[]> entries = resolveItemEntries(row);
        return ItemDatabase.resolveItems(entries);
    }

    /**
     * Giải mã itemIDs (cột [4]) → danh sách [codeItem, quantity] thô.
     *
     * Dùng khi chỉ cần mã và số lượng, không cần object đầy đủ.
     * Ví dụ: "CORN01:1;BEV01:2" → [["CORN01","1"], ["BEV01","2"]]
     */
    public static List<String[]> resolveItemEntries(String[] row) {
        List<String[]> entries = new ArrayList<>();
        if (row.length <= COL_ITEMS || row[COL_ITEMS].isBlank()) return entries;

        for (String part : row[COL_ITEMS].split(ITEM_SEP)) {
            String[] kv = part.split(QTY_SEP, 2);
            if (kv.length == 2) entries.add(kv);
        }
        return entries;
    }

    // ────────────────────────────────────────────────────────────────────
    //  TÍNH TỔNG TIỀN
    // ────────────────────────────────────────────────────────────────────

    /**
     * Tính tổng tiền 1 booking row (giá vé + giá item).
     *
     * @param row  1 row từ readAll() / getByUser()
     * @return Tổng tiền (VND), hoặc 0 nếu không resolve được
     */
    public static double totalPrice(String[] row) {
        double total = 0;

        Seat seat = resolveSeat(row);
        if (seat != null) total += seat.computePrice();

        List<Item> items = resolveItems(row);
        total += ItemDatabase.totalItemPrice(items);

        return total;
    }

    /**
     * Tổng tiền tất cả booking của một user.
     */
    public static double totalPriceByUser(String userId) {
        double total = 0;
        for (String[] row : getByUser(userId)) {
            total += totalPrice(row);
        }
        return total;
    }

    // ────────────────────────────────────────────────────────────────────
    //  DEBUG / IN RA CONSOLE
    // ────────────────────────────────────────────────────────────────────

    /**
     * In toàn bộ booking dạng bảng (kèm resolve chi tiết).
     */
    public static void printAll() {
        List<String[]> rows = readAll();

        System.out.println("═".repeat(80));
        System.out.printf("%-12s %-8s %-6s %-8s %-20s%n",
                "userID", "filmID", "roomID", "seatID", "itemIDs");
        System.out.println("─".repeat(80));

        for (String[] r : rows) {
            System.out.printf("%-12s %-8s %-6s %-8s %-20s%n",
                    r[COL_USER], r[COL_FILM], r[COL_ROOM], r[COL_SEAT],
                    r.length > COL_ITEMS ? r[COL_ITEMS] : "");

            // Resolve thêm chi tiết
            Film film = resolveFilm(r);
            Seat seat = resolveSeat(r);
            List<Item> items = resolveItems(r);

            if (film != null)
                System.out.printf("  ├ Film  : %s (giá gốc %.0f VND)%n",
                        film.getTitle(), film.getPrice());

            if (seat != null)
                System.out.printf("  ├ Ghế   : %s – %s | Giá thực: %.0f VND%n",
                        seat.getCodeSeat(), seat.getClass().getSimpleName(),
                        seat.computePrice());

            if (!items.isEmpty()) {
                System.out.printf("  ├ Items :%n");
                for (Item item : items) {
                    System.out.printf("  │  %-28s x%d = %.0f VND%n",
                            item.getName(), item.getQuantity(),
                            item.getPrice() * item.getQuantity());
                }
            }

            System.out.printf("  └ TỔNG  : %.0f VND%n", totalPrice(r));
            System.out.println();
        }

        System.out.println("═".repeat(80));
        System.out.println("Tổng: " + rows.size() + " booking(s)");
    }

    // ────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────────────────────────

    /** Encode danh sách Item → "CORN01:1;BEV01:2". Trả "" nếu rỗng. */
    private static String encodeItems(List<Item> items) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            if (sb.length() > 0) sb.append(ITEM_SEP);
            sb.append(item.getCodeItem()).append(QTY_SEP).append(item.getQuantity());
        }
        return sb.toString();
    }

    /** Tạo thư mục Data/ và file bookings.csv nếu chưa có. */
    private static void ensureFileExists() {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();

        File f = new File(CSV_FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println(CSV_HEADER);
            } catch (IOException e) {
                System.err.println("BookingDatabase: không thể tạo file – " + e.getMessage());
            }
        }
    }
}