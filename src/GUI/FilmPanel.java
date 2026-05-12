package GUI;

import model.LanguageManager;
import static model.LanguageManager.Language.JAPANESE;
import static model.LanguageManager.Language.VIETNAMESE;
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
    
    private JLabel infoLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setForeground(new Color(200, 200, 200));
    lbl.setFont(new Font("Dialog", Font.PLAIN, 16));
    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
    return lbl;
}
    // ───────────────────────── BUILD UI ─────────────────────────
    private void buildUI() {
        String display = switch (LanguageManager.getInstance().getCurrent()) {
        case VIETNAMESE -> film.getTitleVI();
        case JAPANESE   -> film.getTitleJP();
        default         -> film.getTitle();
};
        // FilmPanel.java — also localize the summary:
        String summary = switch (LanguageManager.getInstance().getCurrent()) {
            case VIETNAMESE -> film.getSummaryVI();  // ← now works
            case JAPANESE   -> film.getSummaryJP();  // ← now works
            default         -> film.getSummary();
        };



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
        
        /// FilmPanel.java -> buildUI()
        // Khởi tạo FilmService để dùng check logic 
        service.FilmService filmService = new service.FilmService(); 

        // 1. Kiểm tra phim có đặt được không
        boolean canBook = filmService.isBookable(film); 

        // 2. Nếu không đặt được, hiện dòng chữ thông báo trạng thái
        if (!canBook) {
            String msg = (film.getState() == Film.State.COMING_SOON) ? "📅 SẮP CÔNG CHIẾU" : "🚫 ĐÃ DỪNG CHIẾU";
            JLabel statusNotify = new JLabel(msg);
            statusNotify.setForeground(film.getState() == Film.State.COMING_SOON ? Color.ORANGE : Color.GRAY);
            statusNotify.setFont(new Font("Dialog", Font.BOLD, 18));
            statusNotify.setAlignmentX(Component.CENTER_ALIGNMENT);

            centerPanel.add(statusNotify); // Thêm vào màn hình chi tiết [cite: 489]
            centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        //Film's state Label
        JLabel lblStatusNotify = new JLabel();
        lblStatusNotify.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusNotify.setFont(new Font("Dialog", Font.BOLD, 14));
        if (film.getState() == Film.State.COMING_SOON) {
            lblStatusNotify.setText("📅 SẮP CÔNG CHIẾU");
            lblStatusNotify.setForeground(new Color(230, 126, 34)); // Màu cam
        } else if (film.getState() == Film.State.ENDED) {
            lblStatusNotify.setText("🚫 ĐÃ DỪNG CHIẾU");
            lblStatusNotify.setForeground(Color.GRAY);
        }
        
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
        JLabel lblName = new JLabel(display);
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Dialog", Font.BOLD, 32));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblName);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // INFO (i18n)
        JLabel lblInfo = new JLabel();
        centerPanel.add(infoLabel(LanguageManager.t(LanguageManager.FILM_DIRECTOR) + ": " + film.getDirector()));
        centerPanel.add(infoLabel(LanguageManager.t(LanguageManager.FILM_CAST) + ": " + film.getCast().replace('|', ',')));
        centerPanel.add(infoLabel(LanguageManager.t(LanguageManager.FILM_DURATION) + ": " + film.getDuration() + " mins"));

        lblInfo.setFont(new Font("Dialog", Font.PLAIN, 16));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

//        centerPanel.add(lblInfo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Summary
        JTextArea txtSummary = new JTextArea(summary); // ← use summary variable
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
        
        if (!canBook) {
        btnAddToCart.setEnabled(false);
        btnAddToCart.setBackground(new Color(70, 70, 70)); // Ghi đè màu xanh bằng màu xám
        }
        
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
        
        if (!canBook) {
        btnBookNow.setEnabled(false);
        btnBookNow.setBackground(new Color(70, 70, 70)); // Ghi đè màu xanh lá bằng màu xám
        }

        btnBookNow.addActionListener(e -> {
        if (!parent.isLoggedIn()) {
        JOptionPane.showMessageDialog(this, LanguageManager.t(LanguageManager.MSG_NOT_LOGGED_IN));
        return;
        }
        parent.showSeatPanel(film, true);

        });

        

        // ADD TO PANEL
        actionPanel.add(btnAddToCart);
        actionPanel.add(btnBookNow);
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