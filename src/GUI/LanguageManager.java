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

    // Messages
    public static final String MSG_NOT_LOGGED_IN   = "msg.notLoggedIn";
    public static final String MSG_SUCCESS          = "msg.success";
    public static final String MSG_ERROR            = "msg.error";
    public static final String MSG_PASS_MISMATCH    = "msg.passMismatch";
    public static final String MSG_WRONG_OLD_PASS   = "msg.wrongOldPass";
    public static final String MSG_MUST_LOGIN_FIRST = "msg.mustLoginFirst";
    public static final String MSG_UPDATED          = "msg.updated";
    public static final String MSG_SETTINGS_COMING  = "msg.settingsComing";

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

        define(MSG_NOT_LOGGED_IN,    "You have not logged in!",    "Bạn chưa đăng nhập!",        "ログインしていません！");
        define(MSG_SUCCESS,          "Success",                    "Thành công",                 "成功");
        define(MSG_ERROR,            "Error",                      "Lỗi",                        "エラー");
        define(MSG_PASS_MISMATCH,    "Passwords do not match.",    "Mật khẩu không khớp.",       "パスワードが一致しません。");
        define(MSG_WRONG_OLD_PASS,   "Current password is wrong.", "Mật khẩu hiện tại không đúng.", "現在のパスワードが正しくありません。");
        define(MSG_MUST_LOGIN_FIRST, "Please log in first.",       "Vui lòng đăng nhập trước.",  "先にログインしてください。");
        define(MSG_UPDATED,          "Updated successfully!",      "Cập nhật thành công!",       "更新しました！");
        define(MSG_SETTINGS_COMING,  "Settings coming soon!",      "Tính năng sắp ra mắt!",      "近日公開！");
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
