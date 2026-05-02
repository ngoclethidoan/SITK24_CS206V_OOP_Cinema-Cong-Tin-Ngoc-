package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.BookTicket;
import model.*;

import static GUI.LanguageManager.t;

public class UserPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User user;

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    public UserPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        buildPages();

        LanguageManager.getInstance().addChangeListener(this::reload);
    }

    // ================= SIDEBAR =================
    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(25, 25, 25));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(20));

        JLabel title = new JLabel("👤 " + user.getName());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(menuButton(t(LanguageManager.USER_PENDING), "PENDING"));
        sidebar.add(menuButton(t(LanguageManager.USER_BOOKED), "BOOKED"));
        sidebar.add(menuButton(t(LanguageManager.USER_REFUND), "REFUND"));
        sidebar.add(menuButton(t(LanguageManager.USER_HISTORY), "HISTORY"));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton(t(LanguageManager.BTN_LOGOUT));
        styleBtn(logout, new Color(180, 40, 40));

        logout.addActionListener(e -> {
            mainFrame.setLoggedIn(false, null);
            mainFrame.showHome();
        });

        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    // ================= MENU BUTTON =================
    private JButton menuButton(String text, String page) {

        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> contentLayout.show(contentPanel, page));

        return btn;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ================= PAGES =================
    private void buildPages() {

        contentPanel.removeAll();

        contentPanel.add(simplePage("⏳ " + t("user.pending.title")), "PENDING");
        contentPanel.add(simplePage("🎟 " + t("user.booked.title")), "BOOKED");
        contentPanel.add(simplePage("💸 " + t("user.refund.title")), "REFUND");
        contentPanel.add(buildHistoryPage(), "HISTORY");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel simplePage(String text) {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 18));

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ================= HISTORY =================
    private JScrollPane buildHistoryPage() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 20));

        java.util.List<BookTicket> history = user.getBookingHistory();

        if (history == null || history.isEmpty()) {

            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(new Color(20, 20, 20));

            JLabel empty = new JLabel("No booking history");
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Dialog", Font.PLAIN, 16));

            emptyPanel.add(empty);
            panel.add(Box.createVerticalStrut(50));
            panel.add(emptyPanel);

        } else {

            for (BookTicket t : history) {

                JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                card.setBackground(new Color(30, 30, 30));

                // ================= POSTER =================
                JLabel poster = new JLabel();
                poster.setPreferredSize(new Dimension(60, 85));

                if (t.getFilm() != null && t.getFilm().getImagePath() != null) {

                    ImageIcon icon = new ImageIcon(t.getFilm().getImagePath());

                    if (icon.getIconWidth() > 0) {
                        Image img = icon.getImage()
                                .getScaledInstance(60, 85, Image.SCALE_SMOOTH);
                        poster.setIcon(new ImageIcon(img));
                    } else {
                        poster.setText("No Img");
                        poster.setForeground(Color.GRAY);
                    }

                } else {
                    poster.setText("No Img");
                    poster.setForeground(Color.GRAY);
                }

                // ================= INFO =================
                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setBackground(new Color(30, 30, 30));

                String filmName = (t.getFilm() != null) ? t.getFilm().getTitle() : "Unknown Film";
                String seatCode = (t.getSeat() != null) ? t.getSeat().getCodeSeat() : "?";
                String roomId = (t.getRoom() != null) ? t.getRoom().getRoomId() : "?";
                double price = (t.getSeat() != null) ? t.getSeat().computePrice() : 0;

                JLabel film = new JLabel("🎬 " + filmName);
                film.setForeground(Color.WHITE);

                JLabel seat = new JLabel("🎟 Seat: " + seatCode);
                seat.setForeground(Color.LIGHT_GRAY);

                JLabel room = new JLabel("🏢 Room: " + roomId);
                room.setForeground(Color.LIGHT_GRAY);

                JLabel priceLbl = new JLabel("💰 " + String.format("%,.0f", price));
                priceLbl.setForeground(Color.YELLOW);

                info.add(film);
                info.add(seat);
                info.add(room);
                info.add(priceLbl);

                // ================= CARD =================
                card.add(poster);
                card.add(info);

                panel.add(card);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    // ================= LANGUAGE =================
    private void reload() {
        removeAll();

        add(buildSidebar(), BorderLayout.WEST);
        buildPages();
        add(contentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}