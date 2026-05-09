package database;

import model.Beverage;
import model.Corn;
import model.Item;

import java.io.*;
import java.util.*;

/**
 * ItemDatabase – load và tra cứu Item từ file Data/items.csv
 *
 * ─── Format CSV (pipe-separated, có header) ───────────────────────────────
 *   codeItem|name|nameVI|nameJP|price|quantity|type
 *
 * Ví dụ:
 *   CORN01|Regular Popcorn|Bắp rang thường|レギュラーポップコーン|45000|100|CORN
 *   BEV01|Coca Cola (M)|Coca Cola (M)|コカ・コーラ (M)|35000|150|BEVERAGE
 *   COMBO01|Combo 1: ...|...|...|70000|80|COMBO
 *
 * ─── Cách dùng với BookingDatabase ───────────────────────────────────────
 *   // Từ 1 booking row, lấy danh sách Item đầy đủ (có giá, tên, ...):
 *   List<String[]> entries = BookingDatabase.resolveItemEntries(row);
 *   List<Item>     items   = ItemDatabase.resolveItems(entries);
 *
 *   for (Item item : items) {
 *       System.out.println(item.getName() + " x" + item.getQuantity()
 *                          + " = " + (item.getPrice() * item.getQuantity()) + " VND");
 *   }
 */
public class ItemDatabase {

    private static final String CSV_FILE = "Data/items.csv";

    // Cache: codeItem → Item (load một lần, dùng lại)
    private static final Map<String, Item> itemMap = new LinkedHashMap<>();

    // Flag để tránh load nhiều lần
    private static boolean loaded = false;

    // ────────────────────────────────────────────────────────────────────
    //  INIT / LOAD
    // ────────────────────────────────────────────────────────────────────

    /**
     * Load tất cả item từ CSV vào bộ nhớ.
     * Gọi 1 lần khi khởi động (Cinema.java hoặc static block).
     * Gọi lại nếu muốn reload.
     */
    public static void initDatabase() {
        itemMap.clear();
        loaded = false;
        load();
    }

    // Lazy-load: tự load lần đầu nếu chưa load
    private static void ensureLoaded() {
        if (!loaded) load();
    }

    private static void load() {
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            System.err.println("ItemDatabase: không tìm thấy " + CSV_FILE);
            loaded = true;
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), "UTF-8"))) {

            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();

                // Bỏ dòng rỗng và header
                if (line.isEmpty() || line.startsWith("codeItem")) continue;

                String[] cols = line.split("\\|", -1);

                // Cần ít nhất 7 cột: codeItem|name|nameVI|nameJP|price|quantity|type
                if (cols.length < 7) {
                    System.err.println("ItemDatabase: dòng " + lineNum + " không hợp lệ – " + line);
                    continue;
                }

                try {
                    String code     = cols[0].trim();
                    String nameEN   = cols[2].trim().isEmpty() ? cols[1].trim() : cols[2].trim(); // ưu tiên nameVI
                    // nameVI = cols[2], nameJP = cols[3] (dùng nếu cần đa ngôn ngữ)
                    double price    = Double.parseDouble(cols[4].trim());
                    int    quantity = Integer.parseInt(cols[5].trim());
                    String type     = cols[6].trim().toUpperCase();

                    Item item;
                    switch (type) {
                        case "CORN":
                        case "COMBO":
                            item = new Corn(code, nameEN, price, quantity);
                            break;
                        case "BEVERAGE":
                            item = new Beverage(code, nameEN, price, quantity);
                            break;
                        default:
                            // Fallback: dùng Item gốc nếu có subclass khác sau
                            item = new Item(code, nameEN, price, quantity);
                            break;
                    }

                    itemMap.put(code.toLowerCase(), item);

                } catch (NumberFormatException e) {
                    System.err.println("ItemDatabase: lỗi số ở dòng " + lineNum + " – " + e.getMessage());
                }
            }

            loaded = true;
            System.out.println("ItemDatabase: đã load " + itemMap.size() + " item(s).");

        } catch (IOException e) {
            System.err.println("ItemDatabase: lỗi đọc file – " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────
    //  TRA CỨU
    // ────────────────────────────────────────────────────────────────────

    /**
     * Lấy Item theo mã (case-insensitive).
     * Trả về null nếu không tìm thấy.
     *
     * @param codeItem  VD: "CORN01", "BEV02", "COMBO01"
     */
    public static Item getById(String codeItem) {
        ensureLoaded();
        if (codeItem == null) return null;

        Item template = itemMap.get(codeItem.trim().toLowerCase());
        if (template == null) return null;

        // Trả về bản sao với quantity = 1 (mặc định khi tra cứu đơn lẻ)
        return cloneWithQty(template, 1);
    }

    /**
     * Lấy Item theo mã và gán số lượng cụ thể.
     *
     * @param codeItem  Mã item
     * @param quantity  Số lượng cần gán
     * @return Item mới với quantity đã gán, hoặc null nếu không tìm thấy
     */
    public static Item getById(String codeItem, int quantity) {
        ensureLoaded();
        if (codeItem == null) return null;

        Item template = itemMap.get(codeItem.trim().toLowerCase());
        if (template == null) return null;

        return cloneWithQty(template, quantity);
    }

    /**
     * Lấy toàn bộ danh sách item (bản sao, không thay đổi cache).
     */
    public static List<Item> getAll() {
        ensureLoaded();
        List<Item> result = new ArrayList<>();
        for (Item t : itemMap.values()) {
            result.add(cloneWithQty(t, t.getQuantity()));
        }
        return result;
    }

    // ────────────────────────────────────────────────────────────────────
    //  TÍCH HỢP VỚI BookingDatabase
    // ────────────────────────────────────────────────────────────────────

    /**
     * Giải mã danh sách entries [codeItem, quantity] thành List&lt;Item&gt; đầy đủ.
     *
     * Dùng kết hợp với:
     *   List&lt;String[]&gt; entries = BookingDatabase.resolveItemEntries(row);
     *   List&lt;Item&gt;     items   = ItemDatabase.resolveItems(entries);
     *
     * @param entries  Output từ {@link BookingDatabase#resolveItemEntries(String[])}
     * @return Danh sách Item với đúng quantity; bỏ qua codeItem không tồn tại
     */
    public static List<Item> resolveItems(List<String[]> entries) {
        List<Item> result = new ArrayList<>();
        if (entries == null) return result;

        for (String[] kv : entries) {
            if (kv.length < 2) continue;
            try {
                String code = kv[0].trim();
                int    qty  = Integer.parseInt(kv[1].trim());
                Item   item = getById(code, qty);
                if (item != null) {
                    result.add(item);
                } else {
                    System.err.println("ItemDatabase.resolveItems: không tìm thấy item '" + code + "'");
                }
            } catch (NumberFormatException e) {
                System.err.println("ItemDatabase.resolveItems: số lượng không hợp lệ – " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Tính tổng tiền items từ một danh sách.
     *
     * @param items Danh sách Item đã có quantity
     * @return Tổng giá (price × quantity)
     */
    public static double totalItemPrice(List<Item> items) {
        if (items == null) return 0;
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // ────────────────────────────────────────────────────────────────────
    //  DEBUG
    // ────────────────────────────────────────────────────────────────────

    /** In toàn bộ item ra console để kiểm tra. */
    public static void printAll() {
        ensureLoaded();
        System.out.println("─".repeat(60));
        System.out.printf("%-10s %-30s %12s %8s%n", "Code", "Name", "Price (VND)", "Stock");
        System.out.println("─".repeat(60));
        for (Item item : itemMap.values()) {
            System.out.printf("%-10s %-30s %12.0f %8d%n",
                    item.getCodeItem(), item.getName(), item.getPrice(), item.getQuantity());
        }
        System.out.println("─".repeat(60));
        System.out.println("Tổng: " + itemMap.size() + " item(s)");
    }

    // ────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────────────────────────

    /**
     * Tạo bản sao Item với quantity mới (tránh mutate cache).
     * Giữ đúng subclass (Corn / Beverage).
     */
    private static Item cloneWithQty(Item src, int qty) {
        if (src instanceof Corn) {
            return new Corn(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        } else if (src instanceof Beverage) {
            return new Beverage(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        } else {
            return new Item(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        }
    }
}