package GUI;

import model.LanguageManager;
import database.BookingDatabase;
import model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
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
        add(contentPanel,   BorderLayout.CENTER);
        buildPages();
        LanguageManager.getInstance().addChangeListener(this::reload);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(25, 25, 25));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(Box.createVerticalStrut(20));

        JLabel title = new JLabel("👤 " + user.getName());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 15));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        JLabel idLbl = new JLabel(user.getUserId());
        idLbl.setForeground(new Color(120, 120, 120));
        idLbl.setFont(new Font("Dialog", Font.PLAIN, 11));
        idLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(idLbl);
        sidebar.add(Box.createVerticalStrut(16));

        sidebar.add(menuBtn("📋 Booking History", "HISTORY"));
        sidebar.add(menuBtn("🛒 My Cart", "PENDING"));
        sidebar.add(menuBtn("⚙️ Settings", "SETTINGS"));
        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton(t(LanguageManager.BTN_LOGOUT));
        styleBtn(logout, new Color(180, 40, 40));
        logout.addActionListener(e -> { mainFrame.setLoggedIn(false, null); mainFrame.showHome(); });
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(20));
        return sidebar;
    }

    private JButton menuBtn(String text, String page) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> contentLayout.show(contentPanel, page));
        return btn;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ── PAGES ────────────────────────────────────────────────────────
    private void buildPages() {
        contentPanel.removeAll();
        contentPanel.add(buildHistoryPage(), "HISTORY");
        contentPanel.add(buildPendingPage(), "PENDING");
        contentPanel.add(buildSettingsPage(), "SETTINGS");
        contentPanel.revalidate(); contentPanel.repaint();
    }

    // ── BOOKING HISTORY ──────────────────────────────────────────────
    private JScrollPane buildHistoryPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        List<String[]> rows = BookingDatabase.getByUser(user.getUserId());

        if (rows.isEmpty()) {
            JLabel empty = new JLabel("No booking history yet", SwingConstants.CENTER);
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Dialog", Font.PLAIN, 16));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(60));
            panel.add(empty);
        } else {
            JLabel header = new JLabel("📋 Booking History — " + rows.size() + " booking(s)");
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Dialog", Font.BOLD, 14));
            header.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(header);
            panel.add(Box.createVerticalStrut(12));

            for (String[] row : rows) {
                panel.add(buildInvoiceCard(row));
                panel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildInvoiceCard(String[] row) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(28, 28, 28));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(12, 16, 12, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));

        // Invoice header
        String bookingId = BookingDatabase.getBookingId(row);
        Film film   = BookingDatabase.resolveFilm(row);
        Room room   = BookingDatabase.resolveRoom(row);
        Seat seat   = BookingDatabase.resolveSeat(row);
        List<Item> items = BookingDatabase.resolveItems(row);

        // Booking ID row
        JPanel idRow = new JPanel(new BorderLayout());
        idRow.setOpaque(false);
        JLabel idLbl = new JLabel("🔖 " + bookingId);
        idLbl.setForeground(new Color(255, 215, 0));
        idLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        JLabel statusLbl = new JLabel("✅ PAID");
        statusLbl.setForeground(new Color(46, 204, 113));
        statusLbl.setFont(new Font("Dialog", Font.BOLD, 11));
        idRow.add(idLbl, BorderLayout.WEST);
        idRow.add(statusLbl, BorderLayout.EAST);
        idRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        card.add(idRow);
        card.add(Box.createVerticalStrut(6));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 55, 55));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(8));

        double total = 0;

        // Film + seat info
        if (film != null) {
            infoLine(card, "🎬 Film:", film.getTitle(), Color.WHITE);
        }
        if (room != null) {
            infoLine(card, "🏢 Room:", room.getRoomId(), Color.LIGHT_GRAY);
        }
        if (seat != null) {
            String seatType = getSeatTypeName(seat);
            double seatPrice = seat.computePrice();
            total += seatPrice;
            infoLine(card, "💺 Seat:",
                seat.getCodeSeat() + "  (" + seatType + ")  —  " + String.format("%,.0f VND", seatPrice),
                new Color(200, 220, 200));
        }

        // Snack items
        if (!items.isEmpty()) {
            card.add(Box.createVerticalStrut(6));
            infoLine(card, "🍿 Snacks:", "", new Color(255, 215, 0));
            double snackTotal = 0;
            for (Item it : items) {
                double lineTotal = it.getPrice() * it.getQuantity();
                snackTotal += lineTotal;
                total += lineTotal;
                JLabel lbl = new JLabel("      • " + it.getName()
                    + "  x" + it.getQuantity()
                    + "   " + String.format("%,.0f VND", lineTotal));
                lbl.setForeground(new Color(210, 180, 120));
                lbl.setFont(new Font("Dialog", Font.PLAIN, 11));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(lbl);
            }
        }

        // Total
        card.add(Box.createVerticalStrut(8));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(55, 55, 55));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep2);
        card.add(Box.createVerticalStrut(6));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel totalLbl = new JLabel("💰 Total Paid");
        totalLbl.setForeground(new Color(255, 215, 0));
        totalLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        JLabel totalAmt = new JLabel(String.format("%,.0f VND", total));
        totalAmt.setForeground(new Color(255, 215, 0));
        totalAmt.setFont(new Font("Dialog", Font.BOLD, 13));
        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalAmt, BorderLayout.EAST);
        card.add(totalRow);

        return card;
    }

    private void infoLine(JPanel p, String label, String value, Color valueColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel lbl = new JLabel(label); lbl.setForeground(new Color(130, 130, 130));
        lbl.setFont(new Font("Dialog", Font.PLAIN, 11));
        JLabel val = new JLabel(value); val.setForeground(valueColor);
        val.setFont(new Font("Dialog", Font.PLAIN, 12));
        row.add(lbl); row.add(val);
        p.add(row);
    }

    // ── PENDING (CART) ────────────────────────────────────────────────
    private JScrollPane buildPendingPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        List<CartItem> cart = user.getCart();
        List<SnackCartItem> snacks = user.getSnackCart();

        if (cart.isEmpty() && snacks.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty", SwingConstants.CENTER);
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Dialog", Font.PLAIN, 16));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(60)); panel.add(empty);
        } else {
            JLabel hdr = new JLabel("🛒 Cart — " + (cart.size() + snacks.size()) + " item(s)");
            hdr.setForeground(Color.WHITE); hdr.setFont(new Font("Dialog", Font.BOLD, 14));
            hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(hdr); panel.add(Box.createVerticalStrut(10));

            for (CartItem item : cart) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                row.setBackground(new Color(30, 30, 30));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                JLabel lbl = new JLabel("🎬 " + item.getFilm().getTitle()
                    + "  |  💺 " + item.getSeat().getCodeSeat()
                    + " (" + getSeatTypeName(item.getSeat()) + ")"
                    + "  |  " + String.format("%,.0f VND", item.getSeat().computePrice()));
                lbl.setForeground(Color.LIGHT_GRAY);
                row.add(lbl);
                panel.add(row); panel.add(Box.createVerticalStrut(4));
            }
            for (SnackCartItem si : snacks) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                row.setBackground(new Color(38, 30, 20));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                JLabel lbl = new JLabel("🍿 " + si.getSummary()
                    + "  |  " + String.format("%,.0f VND", si.getTotalPrice()));
                lbl.setForeground(new Color(210, 180, 120));
                row.add(lbl);
                panel.add(row); panel.add(Box.createVerticalStrut(4));
            }
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ── SETTINGS ─────────────────────────────────────────────────────
    private JPanel buildSettingsPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        JLabel lbl = new JLabel("⚙️ Go to Settings from the top bar", SwingConstants.CENTER);
        lbl.setForeground(Color.GRAY); lbl.setFont(new Font("Dialog", Font.PLAIN, 15));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private String getSeatTypeName(Seat seat) {
        if (seat instanceof VIPSeat)     return "VIP";
        if (seat instanceof PremiumSeat) return "Premium";
        if (seat instanceof ReclineSeat) return "Recliner";
        if (seat instanceof CoupleSeat)  return "Couple";
        return "Standard";
    }

    private void reload() {
        removeAll();
        add(buildSidebar(), BorderLayout.WEST);
        buildPages();
        add(contentPanel, BorderLayout.CENTER);
        revalidate(); repaint();
    }
}
