package GUI;

import database.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static GUI.LanguageManager.t;

public class MainFrame extends JFrame {

    private boolean isLoggedIn = false;
    private String currentUsername = "Guest";
    private User currentUser = null;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private SearchBar searchBar;

    public void refreshUI() {
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
        this.isLoggedIn = value;
        this.currentUser = value ? user : null;
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
        System.out.println("DEBUG: working dir = " + System.getProperty("user.dir"));
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(19, 19, 19));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        topBar.setPreferredSize(new Dimension(0, 65));

        // WEST: Logo
        JLabel logo = new JLabel("CNT CINEMA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        ImageIcon logoIcon = new ImageIcon("assets/logo.png");
        if (logoIcon.getIconWidth() > 0) {
            logo.setIcon(new ImageIcon(
                    logoIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
            logo.setIconTextGap(10);
        }
        topBar.add(logo, BorderLayout.WEST);

        // CENTER: SearchBar
        searchBar = new SearchBar(this, this::showFilmDetail);
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(searchBar);
        topBar.add(centerWrap, BorderLayout.CENTER);

        // EAST: Buttons
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
                    ? t(LanguageManager.BTN_CART) + " (" + cartCount + ")"
                    : t(LanguageManager.BTN_CART);

            JButton cartBtn = new JButton(cartLabel);
            styleButton(cartBtn, new Color(39, 120, 80));
            cartBtn.addActionListener(e -> showCart());

            JButton adminBtn = new JButton("👤 " + currentUsername);
            styleButton(adminBtn, new Color(70, 70, 70));

            JButton logoutBtn = new JButton(t(LanguageManager.BTN_LOGOUT));
            styleButton(logoutBtn, new Color(180, 40, 40));
            logoutBtn.addActionListener(e -> {
                isLoggedIn = false;
                currentUser = null;
                currentUsername = "Guest";
                refreshUI();
            });

            right.add(cartBtn);
            right.add(adminBtn);
            right.add(logoutBtn);
        }

        // Settings - luôn hiện dù đã login hay chưa
        JButton settingsBtn = new JButton(t(LanguageManager.BTN_SETTINGS));
        styleButton(settingsBtn, new Color(40, 40, 40));
        settingsBtn.addActionListener(e -> new SettingPanel(this));
        right.add(settingsBtn);

        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createMovieArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(19, 19, 19));

        JPanel movieGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        movieGrid.setBackground(new Color(19, 19, 19));
        movieGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Film film : FilmDatabase.getUniqueFilms()) {
            movieGrid.add(createMovieCard(film));
        }

        JScrollPane scrollPane = new JScrollPane(movieGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(19, 19, 19));
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createMovieCard(Film film) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 30));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        File imgFile = new File(film.getImagePath());
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(film.getImagePath());
            Image img = icon.getImage().getScaledInstance(220, 310, Image.SCALE_SMOOTH);
            picLabel.setIcon(new ImageIcon(img));
        } else {
            picLabel.setText("No Image");
            picLabel.setForeground(Color.GRAY);
        }

        JLabel nameLabel = new JLabel(film.getTitle());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(picLabel);
        card.add(nameLabel);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showFilmDetail(film);
            }
        });
        return card;
    }

    public void showFilmDetail(Film film) {
        mainPanel.add(new FilmPanel(film, this), "DETAIL");
        cardLayout.show(mainPanel, "DETAIL");
    }

    public void showHome() {
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

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        FilmDatabase.initDatabase();
        RoomDatabase.init();
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
