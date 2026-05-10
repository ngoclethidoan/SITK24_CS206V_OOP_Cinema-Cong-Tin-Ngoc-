/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.util.*;

/**
 * LanguageManager – singleton i18n manager.
 * Stores all UI strings for supported languages and notifies
 * registered listeners when the active language changes.
 */
public class LanguageManager {

    public enum Language {
        ENGLISH("English", "EN"),
        VIETNAMESE("Tiếng Việt", "VI"),
        JAPANESE("日本語", "JP");

        public final String displayName;
        public final String code;

        Language(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }

        @Override public String toString() { return displayName; }
    }

    // ── Singleton ──────────────────────────────────────────────────────
    private static LanguageManager instance;
    public static LanguageManager getInstance() {
        if (instance == null) instance = new LanguageManager();
        return instance;
    }

    // ── State ──────────────────────────────────────────────────────────
    private Language current = Language.ENGLISH;
    private final List<Runnable> listeners = new ArrayList<>();

    // ── String keys ───────────────────────────────────────────────────
    // General UI
    public static final String APP_TITLE       = "app.title";
    public static final String BTN_LOGIN       = "btn.login";
    public static final String BTN_LOGOUT      = "btn.logout";
    public static final String BTN_REGISTER    = "btn.register";
    public static final String BTN_BACK        = "btn.back";
    public static final String BTN_SETTINGS    = "btn.settings";
    public static final String BTN_CART        = "btn.cart";
    public static final String BTN_PAY         = "btn.pay";
    public static final String BTN_ADD_CART    = "btn.addCart";
    public static final String BTN_BOOK_NOW    = "btn.bookNow";
    public static final String BTN_SAVE        = "btn.save";
    public static final String BTN_CANCEL      = "btn.cancel";

    // Search
    public static final String SEARCH_PLACEHOLDER = "search.placeholder";
    public static final String SEARCH_NO_RESULTS  = "search.noResults";

    // Film panel
    public static final String FILM_DIRECTOR   = "film.director";
    public static final String FILM_CAST       = "film.cast";
    public static final String FILM_DURATION   = "film.duration";
    public static final String FILM_MINS       = "film.mins";

    // Cart
    public static final String CART_NOT_LOGGED_IN = "cart.notLoggedIn";
    public static final String CART_SEAT          = "cart.seat";
    public static final String CART_TOTAL         = "cart.total";
    public static final String CART_EMPTY = "cart.empty";
    public static final String CART_PRICE = "cart.price";
    
    // Seat
    public static final String SEAT_NOT_SELECTED = "seat.notSelected";
    public static final String SEAT_SELECTED     = "seat.selected";
    public static final String FILM_SEAT_AVAILABLE = "seat.available";
    public static final String FILM_SEAT_UNAVAILABLE = "seat.unavailable";

    // Settings
    public static final String SETTINGS_TITLE     = "settings.title";
    public static final String SETTINGS_LANGUAGE  = "settings.language";
    public static final String SETTINGS_ACCOUNT   = "settings.account";
    public static final String SETTINGS_SECURITY  = "settings.security";
    public static final String SETTINGS_USERNAME  = "settings.username";
    public static final String SETTINGS_EMAIL     = "settings.email";
    public static final String SETTINGS_OLD_PASS  = "settings.oldPass";
    public static final String SETTINGS_NEW_PASS  = "settings.newPass";
    public static final String SETTINGS_CONFIRM_PASS = "settings.confirmPass";
    public static final String SETTINGS_CHANGE_PASS   = "settings.changePass";
    public static final String SETTINGS_CHANGE_INFO   = "settings.changeInfo";
    public static final String SETTINGS_SELECT_LANG   = "settings.selectLang";
    public static final String SETTINGS_APPLY_LANG    = "settings.applyLang";
    public static final String SETTINGS_LANG_APPLIED  = "settings.langApplied";

    // Login
    public static final String LOGIN_TITLE        = "login.title";
    public static final String LOGIN_USERNAME      = "login.username";
    public static final String LOGIN_PASSWORD      = "login.password";
    public static final String LOGIN_SHOW_PASS     = "login.showPass";
    public static final String LOGIN_ENTER_BOTH    = "login.enterBoth";
    public static final String LOGIN_WRONG_CREDS   = "login.wrongCreds";
    public static final String LOGIN_CREATE_ACCT   = "login.createAccount";
    
    // Register
    public static final String REGISTER_USER_ID = "register.userId";
    public static final String REGISTER_DISPLAY_NAME = "register.displayName";
    public static final String REGISTER_USER_EXISTS = "register.userExists";
    public static final String REGISTER_EMPTY = "register.empty";

    // Messages
    public static final String MSG_NOT_LOGGED_IN   = "msg.notLoggedIn";
    public static final String MSG_SUCCESS          = "msg.success";
    public static final String MSG_ERROR            = "msg.error";
    public static final String MSG_PASS_MISMATCH    = "msg.passMismatch";
    public static final String MSG_WRONG_OLD_PASS   = "msg.wrongOldPass";
    public static final String MSG_MUST_LOGIN_FIRST = "msg.mustLoginFirst";
    public static final String MSG_UPDATED          = "msg.updated";
    public static final String MSG_SETTINGS_COMING  = "msg.settingsComing";
    
    // User
    public static final String USER_PENDING  = "USER_PENDING";
    public static final String USER_BOOKED   = "USER_BOOKED";
    public static final String USER_REFUND   = "USER_REFUND";
    public static final String USER_HISTORY  = "USER_HISTORY";
    
    // Items
    public static final String CART_MIXED_TOTAL = "cart.mixedTotal";
    public static final String CART_SNACK_ONLY  = "cart.snackOnly";
    
    // Snack and Drink
    public static final String SNACK_TITLE      = "snack.title";
    public static final String SNACK_HINT       = "snack.hint";
    public static final String SNACK_CORN       = "snack.corn";
    public static final String SNACK_DRINK      = "snack.drink";
    public static final String SNACK_TOTAL      = "snack.total";
    public static final String SNACK_ADD_CART   = "snack.addToCart";
    public static final String SNACK_EMPTY      = "snack.empty";
    public static final String SNACK_SUCCESS    = "snack.success";
    public static final String CURRENCY         = "currency";

    // ── Translation tables ────────────────────────────────────────────
    private static final Map<String, Map<Language, String>> table = new HashMap<>();

    static {
        
        define(APP_TITLE,            "CNT Cinema",                 "CNT Rạp Chiếu Phim",         "CNTシネマ");
        define(BTN_LOGIN,            "Login",                      "Đăng nhập",                  "ログイン");
        define(BTN_LOGOUT,           "Logout",                     "Đăng xuất",                  "ログアウト");
        define(BTN_REGISTER,         "Create Account",             "Tạo tài khoản",              "アカウント作成");
        define(BTN_BACK,             "⬅ BACK",                     "⬅ QUAY LẠI",                 "⬅ 戻る");
        define(BTN_SETTINGS,         "⚙ Settings",                 "⚙ Cài đặt",                  "⚙ 設定");
        define(BTN_CART,             "🛒 Cart",                    "🛒 Giỏ hàng",                "🛒 カート");
        define(BTN_PAY,              "PAY",                        "THANH TOÁN",                 "支払う");
        define(BTN_ADD_CART,         "ADD TO CART 🛒",             "THÊM VÀO GIỎ 🛒",            "カートに追加 🛒");
        define(BTN_BOOK_NOW,         "BOOK NOW 🎫",                "ĐẶT VÉ NGAY 🎫",             "今すぐ予約 🎫");
        define(BTN_SAVE,             "Save",                       "Lưu",                        "保存");
        define(BTN_CANCEL,           "Cancel",                     "Hủy",                        "キャンセル");

        define(SEARCH_PLACEHOLDER,   "Search movies, directors...", "Tìm phim, đạo diễn...",     "映画・監督を検索...");
        define(SEARCH_NO_RESULTS,    "No results found",           "Không tìm thấy kết quả",     "結果が見つかりません");

        define(FILM_DIRECTOR,        "Director",                   "Đạo diễn",                   "監督");
        define(FILM_CAST,            "Cast",                       "Diễn viên",                  "キャスト");
        define(FILM_DURATION,        "Duration",                   "Thời lượng",                 "上映時間");
        define(FILM_MINS,            "mins",                       "phút",                       "分");

        define(CART_NOT_LOGGED_IN,   "⚠ You have not logged in!",  "⚠ Bạn chưa đăng nhập!",     "⚠ ログインしていません！");
        define(CART_SEAT,            "Seat",                       "Ghế",                        "座席");
        define(CART_TOTAL,           "Total",                      "Tổng cộng",                  "合計");
        define(CART_EMPTY,"Your cart is empty.","Giỏ hàng trống.","カートは空です");
        define(CART_PRICE, "Price", "Giá","価格");
        
        define(SEAT_NOT_SELECTED, "You have not selected any seat", "Bạn chưa chọn ghế nào", "座席が選択されていません");
        define(SEAT_SELECTED,   "Selected seats","Ghế đã chọn", "選択された座席");
        define(FILM_SEAT_AVAILABLE, "Available", "Còn trống","空席");
        define(FILM_SEAT_UNAVAILABLE,"Booked","Đã đặt","予約済み");
        
        define("pay.ticket", "🎬 Movies", "🎬 Vé phim", "🎬 映画");
        define("pay.snack", "🍿 Snacks", "🍿 Bắp & Nước", "🍿 スナック");
        define("pay.totalTicket", "Ticket total: ", "Tổng vé: ", "チケット合計: ");
        define("pay.total_snack", "Snack total: ", "Tổng bắp/nước: ", "スナック合計: ");
        define("pay.confirm", "Confirm payment ", "Xác nhận thanh toán ", "支払い確認 ");
        define("pay.confirmTitle", "Confirmation", "Xác nhận", "確認");
        define("pay.success", "Booking successful!", "Đặt hàng thành công!", "予約成功！");
        define("pay.total", "Total payment: ", "Tổng thanh toán: ", "合計支払い: ");
        
        define(SETTINGS_TITLE,       "Settings",                   "Cài đặt",                    "設定");
        define(SETTINGS_LANGUAGE,    "🌐 Language",                "🌐 Ngôn ngữ",               "🌐 言語");
        define(SETTINGS_ACCOUNT,     "👤 Account",                 "👤 Tài khoản",              "👤 アカウント");
        define(SETTINGS_SECURITY,    "🔒 Security",                "🔒 Bảo mật",               "🔒 セキュリティ");
        define(SETTINGS_USERNAME,    "Username",                   "Tên người dùng",             "ユーザー名");
        define(SETTINGS_EMAIL,       "Email",                      "Email",                      "メール");
        define(SETTINGS_OLD_PASS,    "Current Password",           "Mật khẩu hiện tại",          "現在のパスワード");
        define(SETTINGS_NEW_PASS,    "New Password",               "Mật khẩu mới",               "新しいパスワード");
        define(SETTINGS_CONFIRM_PASS,"Confirm Password",           "Xác nhận mật khẩu",          "パスワードを確認");
        define(SETTINGS_CHANGE_PASS, "Change Password",            "Đổi mật khẩu",               "パスワードを変更");
        define(SETTINGS_CHANGE_INFO, "Update Info",                "Cập nhật thông tin",         "情報を更新");
        define(SETTINGS_SELECT_LANG, "Select Language",            "Chọn ngôn ngữ",              "言語を選択");
        define(SETTINGS_APPLY_LANG,  "Apply Language",             "Áp dụng ngôn ngữ",           "言語を適用");
        define(SETTINGS_LANG_APPLIED,"Language applied!",          "Đã áp dụng ngôn ngữ!",       "言語が適用されました！");

        define(LOGIN_TITLE,          "LOGIN",                      "ĐĂNG NHẬP",                  "ログイン");
        define(LOGIN_USERNAME,       "Username:",                  "Tên đăng nhập:",             "ユーザー名:");
        define(LOGIN_PASSWORD,       "Password:",                  "Mật khẩu:",                  "パスワード:");
        define(LOGIN_SHOW_PASS,      "Show password",              "Hiện mật khẩu",              "パスワードを表示");
        define(LOGIN_ENTER_BOTH,     "Please enter username and password.",
                                     "Vui lòng nhập tên đăng nhập và mật khẩu.",
                                     "ユーザー名とパスワードを入力してください。");
        define(LOGIN_WRONG_CREDS,    "Incorrect username or password.",
                                     "Tên đăng nhập hoặc mật khẩu không đúng.",
                                     "ユーザー名またはパスワードが正しくありません。");
        define(LOGIN_CREATE_ACCT,    "Create Account",             "Tạo tài khoản",              "アカウント作成");
        
        define(REGISTER_USER_ID, "User ID",    "Tên đăng nhập",  "ユーザーID");
        define(REGISTER_DISPLAY_NAME,    "Display Name",   "Tên hiển thị","表示名");
        define(REGISTER_USER_EXISTS,  "User already exists",  "Người dùng đã tồn tại","ユーザーは既に存在します");
        define(REGISTER_EMPTY,    "Please fill all fields",     "Vui lòng điền đầy đủ thông tin", "すべての項目を入力してください");

        define(MSG_NOT_LOGGED_IN,    "You have not logged in!",    "Bạn chưa đăng nhập!",        "ログインしていません！");
        define(MSG_SUCCESS,          "Success",                    "Thành công",                 "成功");
        define(MSG_ERROR,            "Error",                      "Lỗi",                        "エラー");
        define(MSG_PASS_MISMATCH,    "Passwords do not match.",    "Mật khẩu không khớp.",       "パスワードが一致しません。");
        define(MSG_WRONG_OLD_PASS,   "Current password is wrong.", "Mật khẩu hiện tại không đúng.", "現在のパスワードが正しくありません。");
        define(MSG_MUST_LOGIN_FIRST, "Please log in first.",       "Vui lòng đăng nhập trước.",  "先にログインしてください。");
        define(MSG_UPDATED,          "Updated successfully!",      "Cập nhật thành công!",       "更新しました！");
        define(MSG_SETTINGS_COMING,  "Settings coming soon!",      "Tính năng sắp ra mắt!",      "近日公開！");
        
        // User status / history
        define(USER_PENDING, "Pending", "Đang chờ", "保留中");
        define(USER_BOOKED,  "Booked", "Đã đặt", "予約済み");
        define(USER_REFUND,  "Refund", "Hoàn tiền", "返金");
        define(USER_HISTORY, "History", "Lịch sử", "履歴");
        define("user.pending.title", "Pending Confirm", "Chờ xác nhận", "確認待ち");
        define("user.booked.title", "Booked Tickets", "Vé đã đặt", "予約済みチケット");
        define("user.refund.title", "Refund Requests", "Yêu cầu hoàn tiền", "返金リクエスト");
        define("user.history.title", "Booking History", "Lịch sử đặt vé", "予約履歴");
        
        // Snack and Drink
        define(CART_MIXED_TOTAL,     "(vé + bắp/nước)",  "(vé + bắp/nước)",    "(チケット + スナック)");
        define(CART_SNACK_ONLY,"(chỉ bắp/nước)","(chỉ bắp・ドリンク)","(スナックのみ)");
        define(SNACK_TITLE, "Snack Order", "Đặt bắp/nước", "スナック注文");
        define(SNACK_HINT, "(click to increase quantity)", "(bấm để tăng số lượng)", "クリックで追加");
        define(SNACK_CORN, "Popcorn", "Bắp rang", "ポップコーン");
        define(SNACK_DRINK, "Drinks", "Nước uống", "ドリンク");
        define(SNACK_TOTAL, "Total", "Tổng", "合計");
        define(SNACK_ADD_CART, "Add to cart", "Thêm vào giỏ", "カートに追加");
        define(SNACK_EMPTY, "No items selected", "Chưa chọn món", "未選択");
        define(SNACK_SUCCESS, "Added to cart", "Thêm thành công", "追加完了");
        define(CURRENCY, "VND", "VND", "円");
        define("snack.combo", "Combo", "Combo", "コンボ");
        define("snack.optional", "(optional)", "(không bắt buộc)", "(任意)");
        define("cart.snack", "🍿 Snacks", "🍿 Bắp/Nước", "🍿 スナック");
    }

    private static void define(String key, String en, String vi, String jp) {
        Map<Language, String> m = new EnumMap<>(Language.class);
        m.put(Language.ENGLISH,    en);
        m.put(Language.VIETNAMESE, vi);
        m.put(Language.JAPANESE,   jp);
        table.put(key, m);
    }

    // ── Public API ────────────────────────────────────────────────────
    public String get(String key) {
        Map<Language, String> m = table.get(key);
        if (m == null) return key;
        return m.getOrDefault(current, key);
    }

    public Language getCurrent() { return current; }

    public void setLanguage(Language lang) {
        this.current = lang;
        listeners.forEach(Runnable::run);
    }

    public void addChangeListener(Runnable r) { listeners.add(r); }
    public void removeChangeListener(Runnable r) { listeners.remove(r); }

    /** Convenience shortcut */
    public static String t(String key) { return getInstance().get(key); }
}
