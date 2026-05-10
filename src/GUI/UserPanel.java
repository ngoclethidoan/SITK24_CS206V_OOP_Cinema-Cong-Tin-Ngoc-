package GUI;

import model.LanguageManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.BookTicket;
import model.*;

import static model.LanguageManager.t;

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
        sidebar.add(menuButton(t(LanguageManager.USER_BOOKED),  "BOOKED"));
        sidebar.add(menuButton(t(LanguageManager.USER_REFUND),  "REFUND"));
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
        contentPanel.add(simplePage("🎟 " + t("user.booked.title")),  "BOOKED");
        contentPanel.add(simplePage("💸 " + t("user.refund.title")),  "REFUND");
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

        List<BookTicket> history = user.getBookingHistory();

        if (history == null || history.isEmpty()) {

            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(new Color(20, 20, 20));

            JLabel empty = new JLabel("Chưa có lịch sử đặt vé");
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Dialog", Font.PLAIN, 16));

            emptyPanel.add(empty);
            panel.add(Box.createVerticalStrut(50));
            panel.add(emptyPanel);

        } else {

            for (BookTicket ticket : history) {
                panel.add(buildTicketCard(ticket));
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    /** Tạo card hiển thị 1 booking trong lịch sử (bao gồm bắp/nước nếu có) */
    private JPanel buildTicketCard(BookTicket ticket) {

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        double seatPrice = 0;

        // =========================================================
        // FILM SECTION
        // Chỉ hiển thị nếu booking có film
        // =========================================================
        if (ticket.getFilm() != null) {

            JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            topRow.setBackground(new Color(30, 30, 30));

            // POSTER
            JLabel poster = new JLabel();
            poster.setPreferredSize(new Dimension(60, 85));

            if (ticket.getFilm().getImagePath() != null) {
                ImageIcon icon = new ImageIcon(ticket.getFilm().getImagePath());

                if (icon.getIconWidth() > 0) {
                    Image img = icon.getImage()
                            .getScaledInstance(60, 85, Image.SCALE_SMOOTH);

                    poster.setIcon(new ImageIcon(img));
                }
            }

            // INFO
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(new Color(30, 30, 30));

            String filmName = ticket.getFilm().getTitle();
            String seatCode = ticket.getSeat() != null
                    ? ticket.getSeat().getCodeSeat()
                    : "?";

            String roomId = ticket.getRoom() != null
                    ? ticket.getRoom().getRoomId()
                    : "?";

            seatPrice = ticket.getSeat() != null
                    ? ticket.getSeat().computePrice()
                    : 0;

            JLabel filmLbl = new JLabel("🎬 " + filmName);
            filmLbl.setForeground(Color.WHITE);
            filmLbl.setFont(new Font("Dialog", Font.BOLD, 14));

            JLabel seatLbl = new JLabel(
                    "🎟 " + t(LanguageManager.CART_SEAT) + ": " + seatCode
            );
            seatLbl.setForeground(Color.LIGHT_GRAY);

            JLabel roomLbl = new JLabel("🏢 Room: " + roomId);
            roomLbl.setForeground(Color.LIGHT_GRAY);

            JLabel priceLbl = new JLabel(
                    String.format("%,.0f", seatPrice)
                            + " "
                            + t(LanguageManager.CURRENCY)
            );
            priceLbl.setForeground(Color.YELLOW);

            info.add(filmLbl);
            info.add(Box.createVerticalStrut(3));
            info.add(seatLbl);
            info.add(roomLbl);
            info.add(priceLbl);

            topRow.add(poster);
            topRow.add(info);

            card.add(topRow, BorderLayout.CENTER);
        }

        // =========================================================
        // SNACK SECTION
        // =========================================================
        List<Item> snacks = ticket.getSnackItems();

        if (snacks != null && !snacks.isEmpty()) {

            JPanel snackSection = new JPanel();
            snackSection.setLayout(new BoxLayout(snackSection, BoxLayout.Y_AXIS));
            snackSection.setBackground(new Color(38, 30, 20));

            snackSection.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            1, 0, 0, 0,
                            new Color(70, 50, 20)
                    ),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            JLabel snackTitle = new JLabel(
                    LanguageManager.t("cart.snack")
            );

            snackTitle.setForeground(new Color(255, 200, 60));
            snackTitle.setFont(new Font("Dialog", Font.BOLD, 12));

            snackSection.add(snackTitle);
            snackSection.add(Box.createVerticalStrut(4));

            double snackTotal = 0;

            for (Item s : snacks) {

                double lineTotal = s.getPrice() * s.getQuantity();
                snackTotal += lineTotal;

                JLabel snackLbl = new JLabel(
                        "  • "
                                + s.getName()
                                + " x"
                                + s.getQuantity()
                                + "   "
                                + String.format("%,.0f", lineTotal)
                                + " "
                                + t(LanguageManager.CURRENCY)
                );

                snackLbl.setForeground(new Color(210, 180, 120));
                snackSection.add(snackLbl);
            }

            double grandTotal = seatPrice + snackTotal;

            JLabel grandLbl = new JLabel(
                    "  "
                            + t(LanguageManager.CART_TOTAL)
                            + ": "
                            + String.format("%,.0f", grandTotal)
                            + " "
                            + t(LanguageManager.CURRENCY)
            );

            grandLbl.setForeground(new Color(255, 230, 80));
            grandLbl.setFont(new Font("Dialog", Font.BOLD, 13));

            snackSection.add(Box.createVerticalStrut(4));
            snackSection.add(grandLbl);

            card.add(snackSection, BorderLayout.SOUTH);
        }

        return card;
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