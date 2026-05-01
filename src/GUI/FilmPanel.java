package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;

public class FilmPanel extends JPanel {

    // We store the MainFrame so we can go back to it later
    private MainFrame parent;
    private Film film;

    // Constructor: takes Film object and MainFrame reference
    public FilmPanel(Film film, MainFrame parent) {
        this.film = film;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));

        // TOP PANEL (BACK BUTTON)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JButton btnBack = new JButton("⬅ BACK");
        styleButton(btnBack, new Color(40, 40, 40));

        // BACK LOGIC: return to HOME screen in MainFrame
        btnBack.addActionListener(e -> parent.showHome());

        topPanel.add(btnBack);

        // CONTENT PANEL
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

        JLabel lblInfo = new JLabel(
                "<html><div style='text-align: center; color: #CCCCCC;'>" +
                        "<b>Director:</b> " + film.getDirector() + "<br>" +
                        "<b>Cast:</b> " + castDisplay + "<br>" +
                        "<b>Duration:</b> " + film.getDuration() + " mins</div></html>"
        );

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

        // ACTION BUTTON PANEL
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        actionButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        actionButtonPanel.setBackground(new Color(19, 19, 19));

        JButton btnAddToCart = new JButton("ADD TO CART 🛒");
        styleButton(btnAddToCart, new Color(52, 152, 219));

        // ADD TO CART LOGIC
        btnAddToCart.addActionListener(e -> {
            if (!parent.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "You have not Login!");
            } else {
                parent.showSeatPanel(film);
            }
        });

        JButton btnBookNow = new JButton("BOOK NOW 🎫");
        styleButton(btnBookNow, new Color(46, 204, 113));

        // BOOK NOW LOGIC
        btnBookNow.addActionListener(e -> {
            if (!parent.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "You have not Login!");
            } else {
                System.out.println("Opening booking for: " + film.getTitle());
            }
            
        });

        actionButtonPanel.add(btnAddToCart);
        actionButtonPanel.add(btnBookNow);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionButtonPanel, BorderLayout.SOUTH);
    }

    // Helper method to style buttons
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }
}