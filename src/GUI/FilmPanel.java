package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;

public class FilmPanel extends JPanel {

    private MainFrame parent;
    private Film film;

    public FilmPanel(Film film, MainFrame parent) {
        FontConfig.init();  
        this.film = film;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));

        // listen language change
        LanguageManager.getInstance().addChangeListener(this::reloadText);

        buildUI();
    }

    // ───────────────────────── RELOAD ─────────────────────────
    private void reloadText() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    // ───────────────────────── BUILD UI ─────────────────────────
    private void buildUI() {

        // TOP PANEL
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JButton btnBack = new JButton(
            LanguageManager.t(LanguageManager.BTN_BACK)
        );
        styleButton(btnBack, new Color(40, 40, 40));

        btnBack.addActionListener(e -> parent.showHome());

        topPanel.add(btnBack);

        // CENTER PANEL
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(new Color(19, 19, 19));

        // Poster
        ImageIcon icon = new ImageIcon(film.getImagePath());
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage()
                    .getScaledInstance(-1, 350, Image.SCALE_SMOOTH);

            JLabel lblPoster = new JLabel(new ImageIcon(img));
            lblPoster.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(lblPoster);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // Title
        JLabel lblName = new JLabel(film.getTitle());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Dialog", Font.BOLD, 32));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblName);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // INFO (i18n)
        JLabel lblInfo = new JLabel(
            "<html><div style='text-align:center; color:#CCCCCC;'>" +

            "<b>" + LanguageManager.t(LanguageManager.FILM_DIRECTOR) + ":</b> "
            + film.getDirector() + "<br>" +

            "<b>" + LanguageManager.t(LanguageManager.FILM_CAST) + ":</b> "
            + film.getCast().replace('|', ',') + "<br>" +

            "<b>" + LanguageManager.t(LanguageManager.FILM_DURATION) + ":</b> "
            + film.getDuration() + " "
            + LanguageManager.t(LanguageManager.FILM_MINS) +

            "</div></html>"
        );

        lblInfo.setFont(new Font("Dialog", Font.PLAIN, 16));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblInfo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Summary
        JTextArea txtSummary = new JTextArea(film.getSummary());
        txtSummary.setLineWrap(true);
        txtSummary.setWrapStyleWord(true);
        txtSummary.setEditable(false);
        txtSummary.setOpaque(false);
        txtSummary.setForeground(new Color(200, 200, 200));
        txtSummary.setFont(new Font("Dialog", Font.ITALIC, 15));
        txtSummary.setMaximumSize(new Dimension(600, 200));

        centerPanel.add(txtSummary);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ACTION PANEL
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        actionPanel.setBackground(new Color(19, 19, 19));

        // ADD TO CART
        JButton btnAddToCart = new JButton(
            LanguageManager.t(LanguageManager.BTN_ADD_CART)
        );
        styleButton(btnAddToCart, new Color(52, 152, 219));

        btnAddToCart.addActionListener(e -> {
            if (!parent.isLoggedIn()) {
                JOptionPane.showMessageDialog(
                    this,
                    LanguageManager.t(LanguageManager.MSG_NOT_LOGGED_IN)
                );
            } else {
                parent.showSeatPanel(film, false);
            }
        });

        // BOOK NOW
        JButton btnBookNow = new JButton(
            LanguageManager.t(LanguageManager.BTN_BOOK_NOW)
        );
        styleButton(btnBookNow, new Color(46, 204, 113));

        btnBookNow.addActionListener(e -> {
            if (!parent.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "You have not Login!");
            } else {
                System.out.println("Opening booking for: " + film.getTitle());
            }
            if (!parent.isLoggedIn()) {
                JOptionPane.showMessageDialog(
                        this,
                        LanguageManager.t(LanguageManager.MSG_NOT_LOGGED_IN)
                );
                return;
            }

            parent.showSeatPanel(film, true); // 🔵 BOOK MODE
        });

        actionPanel.add(btnAddToCart);
        actionPanel.add(btnBookNow);

        // ADD TO PANEL
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // ───────────────────────── STYLE ─────────────────────────
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Dialog", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }
}