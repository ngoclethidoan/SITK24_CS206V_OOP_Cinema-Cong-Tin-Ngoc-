package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel totalLabel;
    private JPanel listPanel;

    public CartPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        LanguageManager.getInstance().addChangeListener(this::reloadText);
        buildUI();
    }

    // ─────────────────────── ROOT ────────────────────────────────────
    private void buildUI() {
        removeAll();
        add(createTopBar(), BorderLayout.NORTH);

        if (!mainFrame.isLoggedIn() || mainFrame.getCurrentUser() == null) {
            add(createNotLogin(), BorderLayout.CENTER);
        } else {
            buildCartUI();
        }

        revalidate();
        repaint();
    }

    private void reloadText() { buildUI(); }

    // ─────────────────────── TOP BAR ─────────────────────────────────
    private JPanel createTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));

        JButton back = new JButton(LanguageManager.t(LanguageManager.BTN_BACK));
        back.setFont(new Font("Dialog", Font.PLAIN, 13));
        back.addActionListener(e -> mainFrame.showHome());

        JLabel title = new JLabel("🛒  Giỏ hàng");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 16));

        p.add(back);
        p.add(Box.createHorizontalStrut(12));
        p.add(title);
        return p;
    }

    // ─────────────────────── NOT LOGIN ───────────────────────────────
    private JPanel createNotLogin() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        JLabel lbl = new JLabel(
            LanguageManager.t(LanguageManager.CART_NOT_LOGGED_IN),
            SwingConstants.CENTER
        );
        lbl.setForeground(Color.YELLOW);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────────── CART UI ─────────────────────────────────
    private void buildCartUI() {
        User user = mainFrame.getCurrentUser();

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(20, 20, 20));

        rebuildList(user);

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(20, 20, 20));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        add(sp, BorderLayout.CENTER);
        add(createBottomBar(user), BorderLayout.SOUTH);
    }

    // ─────────────────────── LIST ────────────────────────────────────
    private void rebuildList(User user) {
        listPanel.removeAll();

        List<CartItem>      tickets = user.getCart();
        List<SnackCartItem> snacks  = user.getSnackCart();

        boolean empty = tickets.isEmpty() && snacks.isEmpty();

        // ── Section: Vé phim ──────────────────────────────────────────
        if (!tickets.isEmpty()) {
            listPanel.add(sectionHeader("🎬  Vé phim"));
            for (CartItem item : tickets) {
                listPanel.add(createTicketCard(item, tickets));
                listPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // ── Section: Bắp/Nước ────────────────────────────────────────
        if (!snacks.isEmpty()) {
            listPanel.add(sectionHeader("🍿  Bắp & Nước"));
            for (SnackCartItem si : snacks) {
                listPanel.add(createSnackCard(si, snacks, user));
                listPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // ── Empty state ───────────────────────────────────────────────
        if (empty) {
            JLabel lbl = new JLabel(
                LanguageManager.t(LanguageManager.CART_EMPTY),
                SwingConstants.CENTER
            );
            lbl.setForeground(Color.GRAY);
            lbl.setFont(new Font("Dialog", Font.ITALIC, 15));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            listPanel.add(lbl);
        }

        listPanel.revalidate();
        listPanel.repaint();
        updateTotal(user);
    }

    // ─────────────────────── TICKET CARD ─────────────────────────────
    private JPanel createTicketCard(CartItem item, List<CartItem> cart) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // LEFT: checkbox + poster
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JCheckBox check = new JCheckBox();
        check.setSelected(item.isSelected());
        check.setOpaque(false);
        check.addActionListener(e -> {
            item.setSelected(check.isSelected());
            updateTotal(mainFrame.getCurrentUser());
        });

        JLabel poster = new JLabel();
        java.io.File imgFile = new java.io.File(item.getFilm().getImagePath());
        if (imgFile.exists()) {
            Image img = new ImageIcon(item.getFilm().getImagePath())
                    .getImage().getScaledInstance(60, 85, Image.SCALE_SMOOTH);
            poster.setIcon(new ImageIcon(img));
        }
        left.add(check);
        left.add(poster);

        // CENTER: info
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(item.getFilm().getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel seat = new JLabel(
            LanguageManager.t(LanguageManager.CART_SEAT) + ": " + item.getSeat().getCodeSeat()
        );
        seat.setForeground(Color.GRAY);

        JLabel price = new JLabel(
            LanguageManager.t(LanguageManager.CART_PRICE)
            + ": " + String.format("%,.0f", item.getSeat().computePrice()) + " VND"
        );
        price.setForeground(new Color(100, 200, 100));

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 3)));
        center.add(seat);
        center.add(price);

        // RIGHT: delete
        JButton del = new JButton("✕");
        del.setBackground(new Color(180, 40, 40));
        del.setForeground(Color.WHITE);
        del.setFocusPainted(false);
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));
        del.addActionListener(e -> {
            cart.remove(item);
            rebuildList(mainFrame.getCurrentUser());
            mainFrame.refreshCartBadge();
        });

        card.add(left,   BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(del,    BorderLayout.EAST);
        return card;
    }

    // ─────────────────────── SNACK CARD ──────────────────────────────
    private JPanel createSnackCard(SnackCartItem si, List<SnackCartItem> snackList, User user) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(38, 30, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 60, 20)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // LEFT: checkbox + icon
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JCheckBox check = new JCheckBox();
        check.setSelected(si.isSelected());
        check.setOpaque(false);
        check.addActionListener(e -> {
            si.setSelected(check.isSelected());
            updateTotal(user);
        });

        JLabel icon = new JLabel("🍿");
        icon.setFont(new Font("Dialog", Font.PLAIN, 32));

        left.add(check);
        left.add(icon);

        // CENTER: chi tiết từng món
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        for (Item it : si.getItems()) {
            JLabel lbl = new JLabel(
                it.getName() + "  x" + it.getQuantity()
                + "   " + String.format("%,.0f", it.getPrice() * it.getQuantity()) + " VND"
            );
            lbl.setForeground(new Color(220, 190, 120));
            lbl.setFont(new Font("Dialog", Font.PLAIN, 13));
            center.add(lbl);
        }

        JLabel totalLbl = new JLabel(
            "Tổng: " + String.format("%,.0f", si.getTotalPrice()) + " VND"
        );
        totalLbl.setForeground(new Color(255, 215, 0));
        totalLbl.setFont(new Font("Dialog", Font.BOLD, 13));
        center.add(Box.createVerticalStrut(3));
        center.add(totalLbl);

        // RIGHT: delete
        JButton del = new JButton("✕");
        del.setBackground(new Color(160, 40, 40));
        del.setForeground(Color.WHITE);
        del.setFocusPainted(false);
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));
        del.addActionListener(e -> {
            snackList.remove(si);
            rebuildList(user);
            mainFrame.refreshCartBadge();
        });

        card.add(left,   BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(del,    BorderLayout.EAST);
        return card;
    }

    // ─────────────────────── BOTTOM BAR ──────────────────────────────
    private JPanel createBottomBar(User user) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(28, 28, 28));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(55, 55, 55)),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        totalLabel = new JLabel();
        totalLabel.setForeground(new Color(255, 215, 0));
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        updateTotal(user);

        JButton pay = new JButton(LanguageManager.t(LanguageManager.BTN_PAY));
        pay.setBackground(new Color(46, 204, 113));
        pay.setForeground(Color.WHITE);
        pay.setFont(new Font("Dialog", Font.BOLD, 14));
        pay.setFocusPainted(false);
        pay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pay.addActionListener(e -> {
            User u = mainFrame.getCurrentUser();

            // Vé được chọn
            List<CartItem> selectedTickets = u.getCart().stream()
                    .filter(CartItem::isSelected).toList();

            // Snack được chọn
            List<SnackCartItem> selectedSnacks = u.getSnackCart().stream()
                    .filter(SnackCartItem::isSelected).toList();

            if (selectedTickets.isEmpty() && selectedSnacks.isEmpty()) {
                JOptionPane.showMessageDialog(
                    mainFrame, "Chưa chọn món nào để thanh toán.",
                    "Giỏ trống", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            mainFrame.showPay(selectedTickets, selectedSnacks, true);
        });

        p.add(totalLabel, BorderLayout.WEST);
        p.add(pay,        BorderLayout.EAST);
        return p;
    }

    // ─────────────────────── TOTAL ───────────────────────────────────
    private void updateTotal(User user) {
        if (totalLabel == null || user == null) return;

        double ticketTotal = user.getCart().stream()
                .filter(CartItem::isSelected)
                .mapToDouble(i -> i.getSeat().computePrice() * i.getQuantity())
                .sum();

        double snackTotal = user.getSnackCart().stream()
                .filter(SnackCartItem::isSelected)
                .mapToDouble(SnackCartItem::getTotalPrice)
                .sum();

        double grand = ticketTotal + snackTotal;

        String text = LanguageManager.t(LanguageManager.CART_TOTAL)
                + ": " + String.format("%,.0f", grand) + " VND";
        if (snackTotal > 0 && ticketTotal > 0)
            text += "  (vé + bắp/nước)";
        else if (snackTotal > 0)
            text += "  (chỉ bắp/nước)";

        totalLabel.setText(text);
    }

    // ─────────────────────── HELPERS ─────────────────────────────────
    private JPanel sectionHeader(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        p.setBackground(new Color(28, 28, 28));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Dialog", Font.BOLD, 13));
        p.add(lbl);
        return p;
    }
}