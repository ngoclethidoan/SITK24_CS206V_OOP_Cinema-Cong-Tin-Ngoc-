package GUI;

import model.LanguageManager;
import database.*;
import model.*;
import model.SnackCartItem;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import database.CartDatabase;

import static model.LanguageManager.t;

public class MainFrame extends JFrame {

    private boolean isLoggedIn     = false;
    private String  currentUsername = "Guest";
    private User    currentUser    = null;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);
    private SearchBar searchBar;
    private boolean isUserPanelOpen = false;
    private static final String HOME = "HOME";
    private static final String USER = "USER";

    public void refreshUI() {
        // Luôn đồng bộ currentUsername với currentUser.getName() mới nhất
        if (currentUser != null) currentUsername = currentUser.getName();
        setTitle("CNTcinema - " + currentUsername);
        getContentPane().removeAll();
        add(createTopBar(), BorderLayout.NORTH);
        mainPanel.removeAll();
        mainPanel.add(createMovieArea(), "HOME");
        if (currentUser != null) {
            mainPanel.add(new UserPanel(this, currentUser), USER);
        }
        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "HOME");
        revalidate();
        repaint();
    }

    public void setLoggedIn(boolean value, User user) {
        // ← Save cart before logging out
        if (!value && this.currentUser != null) {
            CartDatabase.save(this.currentUser);
        }
        this.isLoggedIn      = value;
        this.currentUser     = value ? user : null;
        this.currentUsername = value ? user.getName() : "Guest";
        
        // ← Restore cart after logging in
        if (value && user != null) {
            CartDatabase.load(user);
        }
        refreshUI();
    }

    public MainFrame() {
        FontConfig.init();  
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        LanguageManager.getInstance().addChangeListener(this::refreshUI);
        refreshUI();
        setVisible(true);
    }
    public void showAdminPanel() {
    if (currentUser == null || !currentUser.isAdmin()) return;
    mainPanel.add(new AdminPanel(this), "ADMIN");
    cardLayout.show(mainPanel, "ADMIN");
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
    if (currentUser != null && currentUser.isAdmin()) {
        // ── ADMIN: only show Admin button ──────────────────
        JButton adminPanelBtn = new JButton("🛠 Admin");
        styleButton(adminPanelBtn, new Color(150, 50, 50));
        adminPanelBtn.addActionListener(e -> showAdminPanel());
        right.add(adminPanelBtn);
        
        JButton logoutBtn = new JButton("🚪 Logout");
        styleButton(logoutBtn, new Color(100, 40, 40));
        logoutBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to log out?",
            "Logout", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) setLoggedIn(false, null);
    });
        
        right.add(logoutBtn);

    } else {
        // ── NORMAL USER: show cart, snack, profile ─────────
        int cartCount = currentUser != null
            ? currentUser.getCart().stream().mapToInt(CartItem::getQuantity).sum()
              + currentUser.getSnackCart().stream().mapToInt(SnackCartItem::getTotalQty).sum()
            : 0;
        String cartLabel = cartCount > 0
            ? t(LanguageManager.BTN_CART) + " (" + cartCount + ")"
            : t(LanguageManager.BTN_CART);

        JButton snackBtn = new JButton("🍿 Bắp & Nước");
        styleButton(snackBtn, new Color(160, 110, 20));
        snackBtn.addActionListener(e -> showSnackOrder());

        JButton cartBtn = new JButton(cartLabel);
        styleButton(cartBtn, new Color(39, 120, 80));
        cartBtn.addActionListener(e -> showCart());

        JButton profileBtn = new JButton("👤 " + currentUsername);
        styleButton(profileBtn, new Color(70, 70, 70));
        profileBtn.addActionListener(e -> showUserPanel());

        right.add(snackBtn);
        right.add(cartBtn);
        right.add(profileBtn);
    }
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
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.getHorizontalScrollBar().setUnitIncrement(20);
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
        isUserPanelOpen = false;
    }
    
    public void showUserPanel() {
        if (currentUser == null) return;

        if (isUserPanelOpen) {
            cardLayout.show(mainPanel, HOME);
            isUserPanelOpen = false;
        } else {
            cardLayout.show(mainPanel, USER);
            isUserPanelOpen = true;
        }
    }

    public void showCart() {
        mainPanel.add(new CartPanel(this), "CART");
        cardLayout.show(mainPanel, "CART");
    }

    public void showSnackOrder() {
        mainPanel.add(new SnackOrderPanel(this), "SNACK");
        cardLayout.show(mainPanel, "SNACK");
    }

    public void showSeatPanel(Film film, boolean bookMode) {
        mainPanel.add(new SeatPanel(film, this, bookMode), "SEAT");
        cardLayout.show(mainPanel, "SEAT");
    }

    public void showPay(List<CartItem> items, boolean fromCart) {
        mainPanel.add(new PayPanel(this, items, fromCart), "PAY");
        cardLayout.show(mainPanel, "PAY");
    }

    public void showPay(List<CartItem> items, List<SnackCartItem> snacks, boolean fromCart) {
        mainPanel.add(new PayPanel(this, items, snacks, fromCart), "PAY");
        cardLayout.show(mainPanel, "PAY");
    }

    /** Mở Settings dưới d
     * ạng Panel trong CardLayout */
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
    
//    public void showItemPanel(Film film, List<CartItem> selectedSeats) {
//    mainPanel.add(new ItemPanel(this, film, selectedSeats), "ITEM");
//    cardLayout.show(mainPanel, "ITEM");
//}
    
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
        ItemDatabase.initDatabase();
        SwingUtilities.invokeLater(MainFrame::new);
    }
}