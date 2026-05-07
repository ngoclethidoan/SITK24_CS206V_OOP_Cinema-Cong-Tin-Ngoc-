package database;

import model.*;
import java.io.*;
import java.util.*;

/**
 * BookingDatabase – lưu/tải lịch sử đặt vé vào file bookings.csv
 *
 * Format mỗi dòng (giống pandas DataFrame):
 *   userID | filmID | roomID | seatID | itemIDs
 *
 * Ví dụ:
 *   user01|F001|R3|B2|ITEM01:2;ITEM02:1
 *
 * Cách đọc lại:
 *   - Dò filmID → FilmDatabase.getById(filmID) → lấy title, price, ...
 *   - Dò roomID → RoomDatabase.getRoom(roomID)
 *   - Dò seatID → room.getSeat(row, col)  (tìm theo codeSeat)
 *   - itemIDs   → danh sách "codeItem:quantity" phân cách bằng ";"
 *
 * Ghi chú: itemIDs có thể rỗng nếu không chọn combo.
 */
public class BookingDatabase {

    // ── Hằng số đường dẫn file CSV ──────────────────────────────────────
    private static final String CSV_FILE = "Data/bookings.csv";

    // Header cho file CSV (dễ đọc như pandas DataFrame)
    private static final String CSV_HEADER = "userID|filmID|roomID|seatID|itemIDs";

    // ── Separator ────────────────────────────────────────────────────────
    private static final String COL_SEP  = "|";   // phân cách cột
    private static final String ITEM_SEP = ";";   // phân cách nhiều item
    private static final String QTY_SEP  = ":";   // phân cách itemCode và quantity

    // ────────────────────────────────────────────────────────────────────
    //  GHI 1 BOOKING VÀO CSV
    // ────────────────────────────────────────────────────────────────────

    /**
     * Lưu một booking vào cuối file CSV (append mode).
     *
     * @param userId  ID người dùng
     * @param ticket  BookTicket đã được tạo
     * @param items   Danh sách Item đi kèm (combo, đồ uống...) – có thể null/rỗng
     */
    public static void save(String userId, BookTicket ticket, List<Item> items) {

        ensureFileExists();

        String filmId = ticket.getFilm().getCodeFilm();
        String roomId = ticket.getRoom().getRoomId();
        String seatId = ticket.getSeat().getCodeSeat();
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
     * Lưu nhiều booking cùng lúc (dùng sau khi checkout từ cart).
     *
     * @param userId  ID người dùng
     * @param tickets Danh sách vé đã đặt
     */
    public static void saveAll(String userId, List<BookTicket> tickets) {
        for (BookTicket t : tickets) {
            save(userId, t, null);
        }
    }

    // ────────────────────────────────────────────────────────────────────
    //  ĐỌC TOÀN BỘ BOOKING (trả về List<String[]> giống từng row của pandas)
    // ────────────────────────────────────────────────────────────────────

    /**
     * Đọc toàn bộ booking từ CSV.
     * Mỗi phần tử là một mảng String gồm 5 cột:
     *   [0] userID
     *   [1] filmID
     *   [2] roomID
     *   [3] seatID
     *   [4] itemIDs  ("ITEM01:2;ITEM02:1" hoặc "" nếu không có)
     *
     * @return Danh sách các row (không gồm header)
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
                if (line.isEmpty() || line.startsWith("userID")) continue; // bỏ header

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

    // ────────────────────────────────────────────────────────────────────
    //  LỌC THEO userId  (giống pandas: df[df['userID'] == userId])
    // ────────────────────────────────────────────────────────────────────

    /**
     * Lấy tất cả booking của một user cụ thể.
     *
     * @param userId ID cần lọc
     * @return Danh sách các row thuộc user đó
     */
    public static List<String[]> getByUser(String userId) {

        List<String[]> result = new ArrayList<>();

        for (String[] row : readAll()) {
            if (row[0].equalsIgnoreCase(userId)) {
                result.add(row);
            }
        }

        return result;
    }

    // ────────────────────────────────────────────────────────────────────
    //  TRA CỨU ĐỐI TƯỢNG TỪ ID  (dò qua database tương ứng)
    // ────────────────────────────────────────────────────────────────────

    /**
     * Từ 1 row CSV, lấy Film object đầy đủ (title, price, ...).
     * Dò qua FilmDatabase theo filmID (cột [1]).
     */
    public static Film resolveFilm(String[] row) {
        return FilmDatabase.getById(row[1]);
    }

    /**
     * Từ 1 row CSV, lấy Room object.
     * Dò qua RoomDatabase theo roomID (cột [2]).
     */
    public static Room resolveRoom(String[] row) {
        return RoomDatabase.getRoom(row[2]);
    }

    /**
     * Từ 1 row CSV, lấy Seat object.
     * Tìm ghế theo codeSeat (cột [3]) bên trong Room (cột [2]).
     */
    public static Seat resolveSeat(String[] row) {
        Room room = resolveRoom(row);
        if (room == null) return null;

        String seatCode = row[3];
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
     * Từ 1 row CSV, giải mã danh sách Item (codeItem + quantity).
     * Trả về List&lt;String[]&gt; gồm [codeItem, quantity] để tự dò ItemDatabase sau.
     *
     * Ví dụ: "ITEM01:2;ITEM02:1" → [["ITEM01","2"], ["ITEM02","1"]]
     */
    public static List<String[]> resolveItemEntries(String[] row) {

        List<String[]> entries = new ArrayList<>();

        if (row.length < 5 || row[4].isBlank()) return entries;

        for (String part : row[4].split(ITEM_SEP)) {
            String[] kv = part.split(QTY_SEP, 2);
            if (kv.length == 2) {
                entries.add(kv);
            }
        }

        return entries;
    }

    // ────────────────────────────────────────────────────────────────────
    //  IN BOOKING RA CONSOLE  (debug / kiểm tra)
    // ────────────────────────────────────────────────────────────────────

    /**
     * In toàn bộ dữ liệu booking ra console dạng bảng (debug).
     * Resolve film/seat để hiện giá cụ thể.
     */
    public static void printAll() {

        List<String[]> rows = readAll();

        System.out.println("─".repeat(80));
        System.out.printf("%-12s %-8s %-6s %-6s %-20s%n",
                "userID", "filmID", "roomID", "seatID", "itemIDs");
        System.out.println("─".repeat(80));

        for (String[] r : rows) {

            System.out.printf("%-12s %-8s %-6s %-6s %-20s%n",
                    r[0], r[1], r[2], r[3],
                    r.length > 4 ? r[4] : "");

            // Resolve thêm thông tin từ database
            Film film = resolveFilm(r);
            Seat seat = resolveSeat(r);

            if (film != null) {
                System.out.printf("  └ Film: %s | Giá vé gốc: %.0f VND%n",
                        film.getTitle(), film.getPrice());
            }

            if (seat != null) {
                System.out.printf("  └ Seat type: %s | Giá thực: %.0f VND%n",
                        seat.getClass().getSimpleName(), seat.computePrice());
            }
        }

        System.out.println("─".repeat(80));
        System.out.println("Tổng: " + rows.size() + " booking(s)");
    }

    // ────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────────────────────────

    /**
     * Encode danh sách Item thành chuỗi "ITEM01:2;ITEM02:1".
     * Nếu null/rỗng trả về chuỗi rỗng.
     */
    private static String encodeItems(List<Item> items) {
        if (items == null || items.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            if (sb.length() > 0) sb.append(ITEM_SEP);
            sb.append(item.getCodeItem()).append(QTY_SEP).append(item.getQuantity());
        }
        return sb.toString();
    }

    /**
     * Đảm bảo thư mục Data/ và file bookings.csv tồn tại.
     * Ghi header vào file nếu file mới được tạo.
     */
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