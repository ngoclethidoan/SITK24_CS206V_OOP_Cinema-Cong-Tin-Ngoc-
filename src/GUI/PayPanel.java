package GUI;

import model.LanguageManager;
import database.BookingDatabase;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import service.PaymentService;
import static model.LanguageManager.t;

public class PayPanel extends JPanel {

    private final MainFrame           mainFrame;
    private final List<CartItem>      ticketItems;
    private final List<SnackCartItem> snackItems;
    private final boolean             fromCart;
    private final PaymentService paymentService = new PaymentService();
    private JLabel totalLabel;

    public PayPanel(MainFrame mainFrame,
                    List<CartItem>      ticketItems,
                    List<SnackCartItem> snackItems,
                    boolean             fromCart) {
        this.mainFrame   = mainFrame;
        this.ticketItems = ticketItems;
        this.snackItems  = snackItems;
        this.fromCart    = fromCart;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        buildUI();
        LanguageManager.getInstance().addChangeListener(this::reload);
    }

    /** Constructor tương thích cũ (chỉ vé, không snack) */
    public PayPanel(MainFrame mainFrame, List<CartItem> ticketItems, boolean fromCart) {
        this(mainFrame, ticketItems, new ArrayList<>(), fromCart);
    }

    private void reload() { buildUI(); }

    private void buildUI() {
        removeAll();
        add(top(),    BorderLayout.NORTH);
        add(center(), BorderLayout.CENTER);
        add(bottom(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    // ── Top ───────────────────────────────────────────────────────────
    private JPanel top() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(getBackground());
        JButton back = new JButton(t(LanguageManager.BTN_BACK));
        back.addActionListener(e -> {
            if (fromCart) mainFrame.showCart();
            else          mainFrame.showHome();
        });
        p.add(back);
        return p;
    }

    // ── Center ────────────────────────────────────────────────────────
    private JScrollPane center() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        double ticketTotal = 0;
        double snackTotal  = 0;

        // Vé phim
        if (!ticketItems.isEmpty()) {
            panel.add(sectionLabel(LanguageManager.t("pay.ticket") + " "));
            panel.add(Box.createVerticalStrut(6));
            for (CartItem i : ticketItems) {
                panel.add(buildTicketRow(i));
                panel.add(Box.createVerticalStrut(8));
                ticketTotal += i.getSeat().computePrice();
            }
            JLabel tl = new JLabel(
                LanguageManager.t("pay.totalTicket") + " "
                + String.format("%,.0f VND", ticketTotal)
            );
            tl.setForeground(new Color(100, 220, 100));
            tl.setFont(new Font("Dialog", Font.BOLD, 13));
            panel.add(tl);
            panel.add(Box.createVerticalStrut(14));
        }

        // Bắp/Nước
        if (!snackItems.isEmpty()) {
            panel.add(separator());
            panel.add(Box.createVerticalStrut(10));
            panel.add(sectionLabel(LanguageManager.t("pay.snack")));
            panel.add(Box.createVerticalStrut(6));
            for (SnackCartItem si : snackItems) {
                panel.add(buildSnackRow(si));
                panel.add(Box.createVerticalStrut(6));
                snackTotal += si.getTotalPrice();
            }
            JLabel sl = new JLabel(
                LanguageManager.t("pay.total_snack")
                + String.format("%,.0f VND", snackTotal)
            );
            sl.setForeground(new Color(255, 200, 50));
            sl.setFont(new Font("Dialog", Font.BOLD, 13));
            panel.add(sl);
            panel.add(Box.createVerticalStrut(14));
        }

        // Grand total
        panel.add(separator());
        panel.add(Box.createVerticalStrut(10));
        double grand = ticketTotal + snackTotal;
        totalLabel = new JLabel(
            t(LanguageManager.CART_TOTAL) + ": " + String.format("%,.0f VND", grand)
        );
        totalLabel.setForeground(new Color(255, 215, 0));
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 17));
        panel.add(totalLabel);

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildTicketRow(CartItem i) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setBackground(new Color(30, 30, 30));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel poster = new JLabel();
        ImageIcon icon = new ImageIcon(i.getFilm().getImagePath());
        if (icon.getIconWidth() > 0) {
            poster.setIcon(new ImageIcon(icon.getImage()
                    .getScaledInstance(55, 78, Image.SCALE_SMOOTH)));
        } else {
            poster.setPreferredSize(new Dimension(55, 78));
            poster.setForeground(Color.GRAY);
        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel(i.getFilm().getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 13));

        JLabel seat = new JLabel(t(LanguageManager.CART_SEAT) + ": " + i.getSeat().getCodeSeat());
        seat.setForeground(Color.LIGHT_GRAY);

        JLabel price = new JLabel(String.format("%,.0f VND", i.getSeat().computePrice()));
        price.setForeground(new Color(100, 200, 100));

        info.add(title); info.add(seat); info.add(price);
        row.add(poster); row.add(info);
        return row;
    }

    private JPanel buildSnackRow(SnackCartItem si) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(new Color(38, 30, 20));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 55, 20)),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        for (Item it : si.getItems()) {
            JLabel lbl = new JLabel(
                "  • " + it.getName()
                + "  x" + it.getQuantity()
                + "   " + String.format("%,.0f VND", it.getPrice() * it.getQuantity())
            );
            lbl.setForeground(new Color(220, 190, 120));
            row.add(lbl);
        }
        return row;
    }

    // ── Bottom ────────────────────────────────────────────────────────
    private JPanel bottom() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(28, 28, 28));
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JButton book = new JButton(t(LanguageManager.BTN_BOOK_NOW));
        book.setBackground(new Color(46, 204, 113));
        book.setForeground(Color.WHITE);
        book.setFont(new Font("Dialog", Font.BOLD, 14));
        book.setFocusPainted(false);
        book.setCursor(new Cursor(Cursor.HAND_CURSOR));
        book.addActionListener(e -> confirmAndBook());

        p.add(book, BorderLayout.EAST);
        return p;
    }

    // ─────────────────────── BOOK LOGIC ──────────────────────────────
    private void confirmAndBook() {
        User u = mainFrame.getCurrentUser();
        double grand = paymentService.calcTotal(ticketItems, snackItems);

        int confirm = JOptionPane.showConfirmDialog(
            mainFrame,
            LanguageManager.t("pay.confirm") + String.format("%,.0f VND", grand) + "?",
            LanguageManager.t("pay.confirmTitle"),
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        paymentService.processPayment(u, ticketItems, snackItems); // ← use service

        mainFrame.refreshCartBadge();
        showSuccess(grand);
    }

    private double calcGrand() {
    return paymentService.calcTotal(ticketItems, snackItems);
    }

    private void showSuccess(double grand) {
        StringBuilder sb = new StringBuilder(
            "✅  " + LanguageManager.t("pay.success") + "\n\n"
        );
        if (!ticketItems.isEmpty())
            sb.append("🎬  ")
              .append(LanguageManager.t("pay.ticket") + " ")
              .append(ticketItems.size())
              .append("\n");

        if (!snackItems.isEmpty()) {
            sb.append("🍿  ")
              .append(LanguageManager.t("pay.snack") + " ")
              .append("\n");

            for (SnackCartItem si : snackItems)
                for (Item it : si.getItems())
                    sb.append("  • ")
                      .append(it.getName())
                      .append(" x")
                      .append(it.getQuantity())
                      .append("\n");
        }

        sb.append("\n")
          .append(LanguageManager.t("pay.total"))
          .append(String.format("%,.0f VND", grand));

        JOptionPane.showMessageDialog(
            mainFrame,
            sb.toString(),
            LanguageManager.t("pay.success"),
            JOptionPane.INFORMATION_MESSAGE
        );

        mainFrame.showHome();
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Dialog", Font.BOLD, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 55, 55));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}