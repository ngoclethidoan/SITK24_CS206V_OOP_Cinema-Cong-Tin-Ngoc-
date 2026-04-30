package GUI;

import database.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import model.User;

public class MainFrame extends JFrame {
    private JPanel currentScreen;
    private boolean isLoggedIn = false;
    private String currentUsername = "Guest";
    private User currentUser = null;

    // Use CardLayout to chang the screen(not create new window)
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    public void refreshUI() {
        setTitle("CNTcinema - " + currentUsername);

        getContentPane().removeAll();

        // Always add the TopBar
        add(createTopBar(), BorderLayout.NORTH);

        // Show HOME screen (CardLayout version)
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
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        refreshUI();
        setVisible(true);

        System.out.println("DEBUG: Java is looking for images in: " + System.getProperty("user.dir"));
    }

    // ================= TOP BAR (GIỮ NGUYÊN LOGIC LOGIN) =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(19, 19, 19));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("CNT CINEMA BOOKING SYSTEM");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);

        ImageIcon logoIcon = new ImageIcon("assets/logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(scaledImage));
        logo.setIconTextGap(15);

        topBar.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        right.setOpaque(false);

        JButton settingsBtn = new JButton("⚙ Settings");
        styleButton(settingsBtn, new Color(40, 40, 40));

        settingsBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Settings coming soon!")
        );

        // ================= LOGIN UI LOGIC =================
        if (!isLoggedIn) {

            JButton loginBtn = new JButton("Login");
            styleButton(loginBtn, new Color(40, 40, 40));

            loginBtn.addActionListener(e -> new LoginFrame(this));

            right.add(loginBtn);

        } else {

            int cartCount = (currentUser != null)
                    ? currentUser.getCart().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum()
                    : 0;

            String cartLabel = cartCount > 0
                    ? "🛒 Giỏ hàng (" + cartCount + ")"
                    : "🛒 Giỏ hàng";

            JButton cartBtn = new JButton(cartLabel);
            styleButton(cartBtn, new Color(39, 120, 80));

            cartBtn.addActionListener(e -> showCart());

            JButton adminBtn = new JButton("👤 " + currentUsername);

            JButton logoutBtn = new JButton("Logout");
            styleButton(adminBtn, new Color(70, 70, 70));
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
        
        right.add(settingsBtn);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    // ================= MOVIE AREA =================
    private JPanel createMovieArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(19, 19, 19));

        JPanel movieGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        movieGrid.setBackground(new Color(19, 19, 19));

        java.util.List<Film> films = FilmDatabase.getUniqueFilms();

        for (Film film : films) {
            movieGrid.add(createMovieCard(film));
        }

        JScrollPane scrollPane = new JScrollPane(movieGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(19, 19, 19));

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    // ================= MOVIE CARD =================
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

        // ================= FIX: OPEN DETAIL IN CENTER =================
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showFilmDetail(film);
            }
        });

        return card;
    }

    // ================= DETAIL SCREEN (CARDLAYOUT) =================
    public void showFilmDetail(Film film) {

        mainPanel.add(new FilmPanel(film, this), "DETAIL");

        cardLayout.show(mainPanel, "DETAIL");
    }

    // ================= BACK HOME =================
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

    // ================= STYLE =================
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        FilmDatabase.initDatabase();
        RoomDatabase.init();
        SwingUtilities.invokeLater(MainFrame::new);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}