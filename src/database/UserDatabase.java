package database;

import model.User;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * UserDatabase – lưu/tải dữ liệu user vào file users.dat (cùng thư mục chạy).
 * Format mỗi dòng: userId|name|password|isAdmin
 */
public class UserDatabase {

    private static final String DATA_FILE = "users.dat";
    private static List<User> users = new ArrayList<>();

    static {
        load();
        // Nếu file trống (lần đầu chạy) thì tạo admin mặc định
        if (users.isEmpty()) {
            User admin = new User("Admin", "admin", "123", new ArrayList<>());
            admin.setAdmin(true);
            users.add(admin);
            save();
        }
    }

    // ── Login bằng userId HOẶC name ──────────────────────────────────
    public static User login(String input, String password) {
        for (User u : users) {
            boolean idMatch   = u.getUserId().equalsIgnoreCase(input);
            boolean nameMatch = u.getName().equalsIgnoreCase(input);
            if ((idMatch || nameMatch) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        users.add(user);
        save();
    }

    /** Gọi sau khi thay đổi bất kỳ thuộc tính nào của user */
    public static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (User u : users) {
                // userId|name|password|isAdmin
                pw.println(escape(u.getUserId()) + "|"
                         + escape(u.getName())   + "|"
                         + escape(u.getPassword())+ "|"
                         + u.isAdmin());
            }
        } catch (IOException e) {
            System.err.println("UserDatabase: cannot save – " + e.getMessage());
        }
    }

    private static void load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                String userId   = unescape(p[0]);
                String name     = unescape(p[1]);
                String password = unescape(p[2]);
                boolean isAdmin = Boolean.parseBoolean(p[3]);
                User u = new User(name, userId, password, new ArrayList<>());
                u.setAdmin(isAdmin);
                users.add(u);
            }
        } catch (IOException e) {
            System.err.println("UserDatabase: cannot load – " + e.getMessage());
        }
    }

    public static boolean userIdExists(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equalsIgnoreCase(userId));
    }

    public static List<User> getAll() { return Collections.unmodifiableList(users); }

    // Escape '|' and newlines in stored values
    private static String escape(String s)   { return s.replace("\\", "\\\\").replace("|", "\\|"); }
    private static String unescape(String s) { return s.replace("\\|", "|").replace("\\\\", "\\"); }
}