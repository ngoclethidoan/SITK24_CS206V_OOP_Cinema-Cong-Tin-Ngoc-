package database;

import model.Beverage;
import model.Corn;
import model.Item;

import java.io.*;
import java.util.*;

public class ItemDatabase {

    private static final String CSV_FILE = "Data/items.csv";
    private static final Map<String, Item> itemMap = new LinkedHashMap<>();
    private static boolean loaded = false;

    public static void initDatabase() {
        itemMap.clear();
        loaded = false;
        load();
    }

    private static void ensureLoaded() {
        if (!loaded) load();
    }

    private static void load() {
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            System.err.println("ItemDatabase: not found " + CSV_FILE);
            loaded = true;
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("codeItem")) continue;
                String[] cols = line.split("\\|", -1);
                if (cols.length < 7) continue;
                try {
                    String code   = cols[0].trim();
                    String nameVI = cols[2].trim().isEmpty() ? cols[1].trim() : cols[2].trim();
                    double price  = Double.parseDouble(cols[4].trim());
                    int qty       = Integer.parseInt(cols[5].trim());
                    String type   = cols[6].trim().toUpperCase();
                    Item item;
                    switch (type) {
                        case "CORN"     -> item = new Corn(code, nameVI, price, qty);
                        case "BEVERAGE" -> item = new Beverage(code, nameVI, price, qty);
                        default         -> item = new Beverage(code, nameVI, price, qty);
                    }
                    itemMap.put(code.toLowerCase(), item);
                } catch (Exception e) {
                    System.err.println("ItemDatabase: bad row – " + line);
                }
            }
            System.out.println("ItemDatabase: loaded " + itemMap.size() + " item(s).");
        } catch (IOException e) {
            System.err.println("ItemDatabase: load error – " + e.getMessage());
        }
        loaded = true;
    }

    // ── READ ────────────────────────────────────────────────────────
    public static Item getById(String codeItem) {
        ensureLoaded();
        if (codeItem == null) return null;
        Item t = itemMap.get(codeItem.trim().toLowerCase());
        if (t == null) return null;
        return cloneWithQty(t, t.getQuantity());
    }

    public static Item getById(String codeItem, int quantity) {
        ensureLoaded();
        if (codeItem == null) return null;
        Item t = itemMap.get(codeItem.trim().toLowerCase());
        if (t == null) return null;
        return cloneWithQty(t, quantity);
    }

    public static List<Item> getAll() {
        ensureLoaded();
        List<Item> result = new ArrayList<>();
        for (Item t : itemMap.values()) result.add(cloneWithQty(t, t.getQuantity()));
        return result;
    }

    public static List<Item> resolveItems(List<String[]> entries) {
        List<Item> result = new ArrayList<>();
        for (String[] kv : entries) {
            if (kv.length < 2) continue;
            try {
                int qty  = Integer.parseInt(kv[1].trim());
                Item item = getById(kv[0].trim(), qty);
                if (item != null) result.add(item);
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    public static double totalItemPrice(List<Item> items) {
        if (items == null) return 0;
        double total = 0;
        for (Item i : items) total += i.getPrice() * i.getQuantity();
        return total;
    }

    // ── ADMIN WRITE ─────────────────────────────────────────────────
    /** Add or update an item (replaces if code already exists). */
    public static void addOrUpdate(Item item) {
        ensureLoaded();
        itemMap.put(item.getCodeItem().toLowerCase(), item);
    }

    /** Remove an item by code. */
    public static void remove(String codeItem) {
        ensureLoaded();
        itemMap.remove(codeItem.trim().toLowerCase());
    }

    /**
     * Write the current item list back to Data/items.csv.
     * Format: codeItem|nameEN|nameVI|nameJP|price|quantity|type
     */
    public static void saveToCSV() {
        new File("Data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(CSV_FILE), "UTF-8")))) {
            pw.println("codeItem|name|nameVI|nameJP|price|quantity|type");
            for (Item item : itemMap.values()) {
                String type = item instanceof Corn ? "CORN"
                            : item instanceof Beverage ? "BEVERAGE"
                            : "COMBO";
                pw.println(String.join("|",
                    item.getCodeItem(),
                    item.getName(),   // nameEN (same as name for simplicity)
                    item.getName(),   // nameVI
                    item.getName(),   // nameJP
                    String.valueOf((int) item.getPrice()),
                    String.valueOf(item.getQuantity()),
                    type
                ));
            }
        } catch (IOException e) {
            System.err.println("ItemDatabase: save error – " + e.getMessage());
        }
    }

    private static Item cloneWithQty(Item src, int qty) {
        Item clone;
        if (src instanceof Corn)     clone = new Corn(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        else                         clone = new Beverage(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        return clone;
    }
}
