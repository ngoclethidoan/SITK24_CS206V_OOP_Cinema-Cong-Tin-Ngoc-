package GUI;

import database.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static GUI.LanguageManager.t;

public class MainFrame extends JFrame {

    private boolean isLoggedIn     = false;
    private String  currentUsername = "Guest";
    private User    currentUser    = null;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);
    private SearchBar searchBar;

    public void refreshUI() {
        // Luôn đồng bộ currentUsername với currentUser.getName() mới nhất
        if (currentUser != null) currentUsername = currentUser.getName();
        setTitle("CNTcinema - " + currentUsername);
        getContentPane().removeAll();
        add(createTopBar(), BorderLayout.NORTH);
        mainPanel.removeAll();
        mainPanel.add(createMovieArea(), "HOME");
        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "HOME");
        revalidate();
        repaint();
    }

    public void setLoggedIn(boolean value, User user) {
        this.isLoggedIn      = value;
        this.currentUser     = value ? user : null;
        this.currentUsername = value ? user.getName() : "Guest";
        refreshUI();
    }

    public MainFrame() {
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        LanguageManager.getInstance().addChangeListener(this::refreshUI);
        refreshUI();
        setVisible(true);
    }

    // ── Top bar ───────────────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(19, 19, 19));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        topBar.setPreferredSize(new Dimension(0, 65));

        JLabel logo = new JLabel("CNT CINEMA");
        logo.setFont(new Font("Dialog", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        ImageIcon logoIcon = new ImageIcon("assets/logo.png");
        if (logoIcon.getIconWidth() > 0) {
            logo.setIcon(new ImageIcon(logoIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
            logo.setIconTextGap(10);
        }
        topBar.add(logo, BorderLayout.WEST);

        searchBar = new SearchBar(this, this::showFilmDetail);
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(searchBar);
        topBar.add(centerWrap, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);

        if (!isLoggedIn) {
            JButton loginBtn = new JButton(t(LanguageManager.BTN_LOGIN));
            styleButton(loginBtn, new Color(52, 152, 219));
            loginBtn.addActionListener(e -> new LoginFrame(this));
            right.add(loginBtn);
        } else {
            int cartCount = currentUser != null
                ? currentUser.getCart().stream().mapToInt(CartItem::getQuantity).sum() : 0;
            String cartLabel = cartCount > 0
                ? t(LanguageManager.BTN_CART) + " (" + cartCount + ")" : t(LanguageManager.BTN_CART);

            JButton cartBtn = new JButton(cartLabel);
            styleButton(cartBtn, new Color(39, 120, 80));
            cartBtn.addActionListener(e -> showCart());

            JButton adminBtn = new JButton("👤 " + currentUsername);
            styleButton(adminBtn, new Color(70, 70, 70));

            JButton logoutBtn = new JButton(t(LanguageManager.BTN_LOGOUT));
            styleButton(logoutBtn, new Color(180, 40, 40));
            logoutBtn.addActionListener(e -> {
                isLoggedIn = false; currentUser = null; currentUsername = "Guest"; refreshUI();
            });

            right.add(cartBtn);
            right.add(adminBtn);
            right.add(logoutBtn);
        }

        JButton settingsBtn = new JButton(t(LanguageManager.BTN_SETTINGS));
        styleButton(settingsBtn, new Color(40, 40, 40));
        settingsBtn.addActionListener(e -> showSettings());
        right.add(settingsBtn);

        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    // ── Movie area ────────────────────────────────────────────────────
    private JPanel createMovieArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(19, 19, 19));
        JPanel movieGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        movieGrid.setBackground(new Color(19, 19, 19));
        movieGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        for (Film film : FilmDatabase.getUniqueFilms()) movieGrid.add(createMovieCard(film));
        JScrollPane sp = new JScrollPane(movieGrid);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(19, 19, 19));
        container.add(sp, BorderLayout.CENTER);
        return container;
    }

    private JPanel createMovieCard(Film film) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 30));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel pic = new JLabel();
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        File f = new File(film.getImagePath());
        if (f.exists()) {
            pic.setIcon(new ImageIcon(new ImageIcon(film.getImagePath())
                .getImage().getScaledInstance(220, 310, Image.SCALE_SMOOTH)));
        } else { pic.setText("No Image"); pic.setForeground(Color.GRAY); }
        JLabel name = new JLabel(film.getTitle());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Dialog", Font.PLAIN, 13));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(pic); card.add(name);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showFilmDetail(film); }
        });
        return card;
    }

    // ── Navigation ────────────────────────────────────────────────────
    public void showFilmDetail(Film film) {
        mainPanel.add(new FilmPanel(film, this), "DETAIL");
        cardLayout.show(mainPanel, "DETAIL");
    }

    public void showHome() {
        if (searchBar != null) searchBar.hideDropdown();
        cardLayout.show(mainPanel, "HOME");
    }

    public void showCart() {
        mainPanel.add(new CartPanel(this), "CART");
        cardLayout.show(mainPanel, "CART");
    }

    public void showSeatPanel(Film film) {
        mainPanel.add(new SeatPanel(film, this), "SEAT");
        cardLayout.show(mainPanel, "SEAT");
    }

    public void showPay() {
        mainPanel.add(new PayPanel(this), "PAY");
        cardLayout.show(mainPanel, "PAY");
    }

    /** Mở Settings dưới dạng Panel trong CardLayout */
    public void showSettings() {
        mainPanel.add(new SettingPanel(this), "SETTINGS");
        cardLayout.show(mainPanel, "SETTINGS");
    }

    /** Hiển thị kết quả tìm kiếm (Enter hoặc click 🔍) */
    public void showSearchResults(java.util.List<Film> results, String query) {
        mainPanel.add(new SearchResultPanel(results, query, this), "SEARCH");
        cardLayout.show(mainPanel, "SEARCH");
    }

    /** Cập nhật cart badge trên TopBar mà không reload toàn bộ UI */
    public void refreshCartBadge() {
        refreshUI();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public boolean isLoggedIn()     { return isLoggedIn; }
    public User    getCurrentUser() { return currentUser; }

    public static void main(String[] args) {
        // Cross-platform font: uu tien Segoe UI (Windows), fallback Dialog (macOS/Linux)
        // Dam bao hien thi tieng Viet dung tren ca hai OS
        String fontName = "Dialog";
        java.awt.Font[] allFonts = java.awt.GraphicsEnvironment
            .getLocalGraphicsEnvironment().getAllFonts();
        for (java.awt.Font ff : allFonts) {
            if (ff.getName().equalsIgnoreCase("Segoe UI")) { fontName = "Segoe UI"; break; }
        }
        final java.awt.Font globalFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, 13);
        javax.swing.UIManager.put("Button.font",        globalFont);
        javax.swing.UIManager.put("Label.font",         globalFont);
        javax.swing.UIManager.put("TextField.font",     globalFont);
        javax.swing.UIManager.put("TextArea.font",      globalFont);
        javax.swing.UIManager.put("PasswordField.font", globalFont);
        javax.swing.UIManager.put("ComboBox.font",      globalFont);
        javax.swing.UIManager.put("CheckBox.font",      globalFont);
        javax.swing.UIManager.put("RadioButton.font",   globalFont);
        javax.swing.UIManager.put("List.font",          globalFont);
        javax.swing.UIManager.put("Menu.font",          globalFont);
        javax.swing.UIManager.put("MenuItem.font",      globalFont);

        FilmDatabase.initDatabase();
        RoomDatabase.init();
        SwingUtilities.invokeLater(MainFrame::new);
    }
}