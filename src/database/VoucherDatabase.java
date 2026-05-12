package database;

import model.Voucher;
import java.io.*;
import java.util.*;

/**
 * VoucherDatabase – reads/writes Data/vouchers.csv
 *
 * CSV format (pipe-separated):
 *   voucherID|name|percentOff|minOrderAmount|active
 *   SUMMER25|Summer Sale 25%|25|100000|true
 *   NEWUSER|New User 15%|15|0|true
 *   VIP50|VIP 50% Off|50|500000|true
 */
public class VoucherDatabase {

    private static final String FILE   = "Data/vouchers.csv";
    private static final String HEADER = "voucherID|name|percentOff|minOrderAmount|active";

    private static final List<Voucher> vouchers = new ArrayList<>();
    private static boolean loaded = false;

    public static void init() {
        vouchers.clear();
        loaded = false;
        ensureFileExists();
        load();
    }

    private static void load() {
        File f = new File(FILE);
        if (!f.exists()) { loaded = true; return; }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("voucherID")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;
                try {
                    vouchers.add(new Voucher(
                        p[0].trim(), p[1].trim(),
                        Double.parseDouble(p[2].trim()),
                        Double.parseDouble(p[3].trim()),
                        Boolean.parseBoolean(p[4].trim())
                    ));
                } catch (Exception e) {
                    System.err.println("VoucherDatabase: bad row – " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("VoucherDatabase: load error – " + e.getMessage());
        }
        loaded = true;
    }

    // ── QUERY ────────────────────────────────────────────────────────
    public static List<Voucher> getAll() {
        if (!loaded) init();
        return Collections.unmodifiableList(vouchers);
    }

    public static List<Voucher> getActive() {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : getAll()) if (v.isActive()) result.add(v);
        return result;
    }

    public static Voucher findById(String id) {
        if (id == null || id.isBlank()) return null;
        for (Voucher v : getAll())
            if (v.getVoucherId().equalsIgnoreCase(id.trim())) return v;
        return null;
    }

    // ── ADMIN WRITE ──────────────────────────────────────────────────
    public static void add(Voucher v) {
        if (!loaded) init();
        vouchers.removeIf(x -> x.getVoucherId().equalsIgnoreCase(v.getVoucherId()));
        vouchers.add(v);
    }

    public static void remove(String id) {
        if (!loaded) init();
        vouchers.removeIf(v -> v.getVoucherId().equalsIgnoreCase(id));
    }

    public static void saveToCSV() {
        new File("Data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8")))) {
            pw.println(HEADER);
            for (Voucher v : vouchers) {
                pw.println(String.join("|",
                    v.getVoucherId(), v.getName(),
                    String.valueOf(v.getPercentOff()),
                    String.valueOf(v.getMinOrderAmount()),
                    String.valueOf(v.isActive())
                ));
            }
        } catch (IOException e) {
            System.err.println("VoucherDatabase: save error – " + e.getMessage());
        }
    }

    // ── CREATE DEFAULT FILE ──────────────────────────────────────────
    private static void ensureFileExists() {
        new File("Data").mkdirs();
        File f = new File(FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(f), "UTF-8")))) {
                pw.println(HEADER);
                // Default vouchers
                pw.println("WELCOME10|Welcome 10% Off|10|0|true");
                pw.println("SUMMER20|Summer Sale 20%|20|200000|true");
                pw.println("VIP30|VIP Member 30%|30|300000|true");
                pw.println("FLASH50|Flash Sale 50%|50|500000|true");
                pw.println("STUDENT15|Student Discount 15%|15|100000|true");
            } catch (IOException e) {
                System.err.println("VoucherDatabase: cannot create – " + e.getMessage());
            }
        }
    }
}
