package GUI;

import model.Film; // Import your Film model
import javax.swing.*;
import java.awt.*;

public class FilmFrame extends JFrame {

    // We store the 'MainFrame' so we can go back to it later
    private MainFrame parent;
    private Film film;

    // 1. UPDATED CONSTRUCTOR: It now takes the Film object and the MainFrame object
    public FilmFrame(Film film, MainFrame parent) {
        this.film = film;
        this.parent = parent;

        setTitle(film.getTitle());
        setSize(1000, 750); // Match your MainFrame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(19, 19, 19));

        // --- TOP PANEL (BACK BUTTON) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JButton btnBack = new JButton("⬅ BACK");
        styleButton(btnBack, new Color(40, 40, 40));

        // ADJUSTED BACK LOGIC: Instead of 'new MainFrame()', we just show the old one
        btnBack.addActionListener(e -> {
            parent.setVisible(true); // Show the original window (keeps login state!)
            this.dispose();          // Close this detail window
        });

        topPanel.add(btnBack);

        // --- CONTENT PANEL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); 
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(new Color(19, 19, 19));
        
        // 1. Poster
        ImageIcon icon = new ImageIcon(film.getImagePath());
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(-1, 350, Image.SCALE_SMOOTH);
            JLabel lblPoster = new JLabel(new ImageIcon(img));
            lblPoster.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(lblPoster);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // 2. Title
        JLabel lblName = new JLabel(film.getTitle());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Arial", Font.BOLD, 32));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblName);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Info
        String castDisplay = film.getCast().replace('|', ','); 
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; color: #CCCCCC;'>" +
                "<b>Director:</b> " + film.getDirector() + "<br>" +
                "<b>Cast:</b> " + castDisplay + "<br>" +
                "<b>Duration:</b> " + film.getDuration() + " mins</div></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblInfo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 4. Summary
        JTextArea txtSummary = new JTextArea(film.getSummary());
        txtSummary.setLineWrap(true);
        txtSummary.setWrapStyleWord(true);
        txtSummary.setEditable(false);
        txtSummary.setOpaque(false);
        txtSummary.setForeground(new Color(200, 200, 200));
        txtSummary.setFont(new Font("Arial", Font.ITALIC, 15));
        txtSummary.setMaximumSize(new Dimension(600, 200));
        centerPanel.add(txtSummary);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // --- ACTION BUTTONS (SOUTH) ---
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        actionButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        actionButtonPanel.setBackground(new Color(19, 19, 19));

        JButton btnAddToCart = new JButton("ADD TO CART 🛒");
        styleButton(btnAddToCart, new Color(52, 152, 219));
        
        // ADJUSTED CART LOGIC
        btnAddToCart.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Added " + film.getTitle() + " to cart!");
            // Later: new CartFrame(film, parent).setVisible(true);
        });

        JButton btnBookNow = new JButton("BOOK NOW 🎫");
        styleButton(btnBookNow, new Color(46, 204, 113));
        
        // ADJUSTED BOOKING LOGIC
        btnBookNow.addActionListener(e -> {
            // Later: new BookingFrame(film, parent).setVisible(true);
            // this.dispose();
            System.out.println("Opening booking for: " + film.getTitle());
        });

        actionButtonPanel.add(btnAddToCart);
        actionButtonPanel.add(btnBookNow);

        add(actionButtonPanel, BorderLayout.SOUTH);
    }

    // Helper to keep code clean
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }
}