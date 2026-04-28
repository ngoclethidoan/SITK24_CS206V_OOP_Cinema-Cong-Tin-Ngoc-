package GUI;

import database.FilmDatabase;
import model.Film;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import model.User;
public class MainFrame extends JFrame {
    private JPanel currentScreen;
    private boolean isLoggedIn = false;
    private String currentUsername = "Guest";
    private User currentUser = null;
    
    public void refreshUI() {
        setTitle("CNTcinema - " + currentUsername);
        getContentPane().removeAll();
        
        // Always add the TopBar
        add(createTopBar(), BorderLayout.NORTH);
        
        // Show the movie grid
        currentScreen = createMovieArea();
        add(currentScreen, BorderLayout.CENTER);
        
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
//        setTitle("Cinema App - " + (isLoggedIn ? "Admin Mode" : "Guest"));
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initial UI Setup
        refreshUI();
        setVisible(true);
        
        // Debugging print to console - look at your output window!
        System.out.println("DEBUG: Java is looking for images in: " + System.getProperty("user.dir"));
    }

    

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(19, 19, 19));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo
        JLabel logo = new JLabel("CNT CINEMA BOOKING SYSTEM");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        
        topBar.add(logo, BorderLayout.WEST);
        ImageIcon logoIcon = new ImageIcon("assets/logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
logo.setIcon(new ImageIcon(scaledImage));
logo.setIconTextGap(15);

        // Right side buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        right.setOpaque(false);
        JButton settingsBtn = new JButton("⚙ Settings");
styleButton(settingsBtn, new Color(40, 40, 40));
settingsBtn.addActionListener(e -> {
    JOptionPane.showMessageDialog(this, "Settings coming soon!");
    // Later you can do: new SettingsFrame(this).setVisible(true);
});
right.add(settingsBtn);
        if (!isLoggedIn) {
            JButton loginBtn = new JButton("Login");
            styleButton(loginBtn, new Color(40, 40, 40));
            loginBtn.addActionListener(e -> new LoginFrame(this));
            right.add(loginBtn);
        } else {
            // Nút giỏ hàng — hiển thị số vé đã đặt
            int cartCount = (currentUser != null) ? currentUser.getBookingHistory().size() : 0;
            String cartLabel = cartCount > 0 ? "🛒 Giỏ hàng (" + cartCount + ")" : "🛒 Giỏ hàng";
            JButton cartBtn = new JButton(cartLabel);
            styleButton(cartBtn, new Color(39, 120, 80));
            cartBtn.addActionListener(e -> new CartFrame(this));
            right.add(cartBtn);

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
            
            right.add(adminBtn);
            right.add(logoutBtn);
        }

        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createMovieArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(19, 19, 19));

        JPanel movieGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        movieGrid.setBackground(new Color(19, 19, 19));
        movieGrid.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        java.util.List<Film> films = FilmDatabase.getUniqueFilms(); 

        for (Film film : films) {
            movieGrid.add(createMovieCard(film));
        }

        JScrollPane scrollPane = new JScrollPane(movieGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(19, 19, 19));
        container.add(scrollPane, BorderLayout.CENTER);
        // 🔥 THE SMOOTH SCROLL FIX:
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Standard speed
        scrollPane.getVerticalScrollBar().setBlockIncrement(50); // Speed when clicking the track
        return container;
    }

    private JPanel createMovieCard(Film film) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Poster Logic with Debugging
        JLabel picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        File imgFile = new File(film.getImagePath());
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(film.getImagePath());
            Image img = icon.getImage().getScaledInstance(220, 310, Image.SCALE_SMOOTH);
            picLabel.setIcon(new ImageIcon(img));
        } else {
            picLabel.setText("<html><center>Missing Image:<br>" + film.getImagePath() + "</center></html>");
            picLabel.setForeground(Color.GRAY);
            picLabel.setPreferredSize(new Dimension(220, 310));
        }

        JLabel nameLabel = new JLabel(film.getTitle());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 10))); // Add a little space at the top
        card.add(picLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10))); // Space between poster and name
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10))); // Space at the bottom
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
           
            new FilmFrame(film, MainFrame.this).setVisible(true);
//            MainFrame.this.setVisible(false); // Hide main instead of closing it
        }
    });
    return card;
    }

    

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    public boolean isLoggedIn() {
    return isLoggedIn;
}

public User getCurrentUser() {
    return currentUser;
}
}