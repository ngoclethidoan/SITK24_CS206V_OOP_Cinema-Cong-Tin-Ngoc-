package GUI;

import model.LanguageManager;
import database.BookingDatabase;
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

    private JPanel createTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));
        JButton back = new JButton(LanguageManager.t(LanguageManager.BTN_BACK));
        back.setFont(new Font("Dialog", Font.PLAIN, 13));
        back.addActionListener(e -> mainFrame.showHome());
        JLabel title = new JLabel(LanguageManager.t(LanguageManager.BTN_CART));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        p.add(back);
        p.add(Box.createHorizontalStrut(12));
        p.add(title);
        return p;
    }

    private JPanel createNotLogin() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        JLabel lbl = new JLabel(LanguageManager.t(LanguageManager.CART_NOT_LOGGED_IN), SwingConstants.CENTER);
        lbl.setForeground(Color.YELLOW);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

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

    private void rebuildList(User user) {
        listPanel.removeAll();
        List<CartItem>      tickets = user.getCart();
        List<SnackCartItem> snacks  = user.getSnackCart();
        boolean empty = tickets.isEmpty() && snacks.isEmpty();

        if (!tickets.isEmpty()) {
            listPanel.add(sectionHeader(LanguageManager.t(LanguageManager.USER_BOOKED)));
            for (CartItem item : tickets) {
                listPanel.add(createTicketCard(item, tickets));
                listPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (!snacks.isEmpty()) {
            listPanel.add(sectionHeader(LanguageManager.t("cart.snack")));
            for (SnackCartItem si : snacks) {
                listPanel.add(createSnackCard(si, snacks, user));
                listPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (empty) {
            JLabel lbl = new JLabel(LanguageManager.t(LanguageManager.CART_EMPTY), SwingConstants.CENTER);
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

    private JPanel createTicketCard(CartItem item, List<CartItem> cart) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

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
            poster.setIcon(new ImageIcon(new ImageIcon(item.getFilm().getImagePath())
                .getImage().getScaledInstance(60, 85, java.awt.Image.SCALE_SMOOTH)));
        }
        left.add(check);
        left.add(poster);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        JLabel title = new JLabel(item.getFilm().getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        JLabel seat = new JLabel(LanguageManager.t(LanguageManager.CART_SEAT) + ": " + item.getSeat().getCodeSeat());
        seat.setForeground(Color.GRAY);
        JLabel price = new JLabel(LanguageManager.t(LanguageManager.CART_PRICE)
            + ": " + String.format("%,.0f", item.getSeat().computePrice()) + " VND");
        price.setForeground(new Color(100, 200, 100));
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 3)));
        center.add(seat);
        center.add(price);

        JButton del = new JButton("✕");
        del.setBackground(new Color(180, 40, 40));
        del.setForeground(Color.WHITE);
        del.setFocusPainted(false);
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));
        del.addActionListener(e -> {
            User user = mainFrame.getCurrentUser();

            // ✅ Release the seat back to available
            synchronized (item.getSeat()) {
                item.getSeat().setState(Seat.State.available);
            }

            // ✅ Remove PENDING row from bookings.csv
            BookingDatabase.removePendingTicket(
                user.getUserId(),
                item.getFilm().getCodeFilm(),
                item.getSeat().getCodeSeat()
            );

            cart.remove(item);
            rebuildList(user);
            mainFrame.refreshCartBadge();
        });

        card.add(left,   BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(del,    BorderLayout.EAST);
        return card;
    }

    private JPanel createSnackCard(SnackCartItem si, List<SnackCartItem> snackList, User user) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(38, 30, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 60, 20)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JCheckBox check = new JCheckBox();
        check.setSelected(si.isSelected());
        check.setOpaque(false);
        check.addActionListener(e -> { si.setSelected(check.isSelected()); updateTotal(user); });
        JLabel icon = new JLabel("🍿");
        icon.setFont(new Font("Dialog", Font.PLAIN, 32));
        left.add(check);
        left.add(icon);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        for (Item it : si.getItems()) {
            JLabel lbl = new JLabel(it.getName() + "  x" + it.getQuantity()
                + "   " + String.format("%,.0f", it.getPrice() * it.getQuantity()) + " VND");
            lbl.setForeground(new Color(220, 190, 120));
            lbl.setFont(new Font("Dialog", Font.PLAIN, 13));
            center.add(lbl);
        }
        JLabel totalLbl = new JLabel(LanguageManager.t(LanguageManager.SNACK_TOTAL)
            + ": " + String.format("%,.0f", si.getTotalPrice())
            + " " + LanguageManager.t(LanguageManager.CURRENCY));
        totalLbl.setForeground(new Color(255, 215, 0));
        totalLbl.setFont(new Font("Dialog", Font.BOLD, 13));
        center.add(Box.createVerticalStrut(3));
        center.add(totalLbl);

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
            List<CartItem>      selectedTickets = u.getCart().stream().filter(CartItem::isSelected).toList();
            List<SnackCartItem> selectedSnacks  = u.getSnackCart().stream().filter(SnackCartItem::isSelected).toList();
            if (selectedTickets.isEmpty() && selectedSnacks.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                    LanguageManager.t(LanguageManager.MSG_NOT_LOGGED_IN),
                    LanguageManager.t(LanguageManager.MSG_ERROR),
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainFrame.showPay(selectedTickets, selectedSnacks, true);
        });

        p.add(totalLabel, BorderLayout.WEST);
        p.add(pay,        BorderLayout.EAST);
        return p;
    }

    private void updateTotal(User user) {
        if (totalLabel == null || user == null) return;
        double ticketTotal = user.getCart().stream()
            .filter(CartItem::isSelected)
            .mapToDouble(i -> i.getSeat().computePrice() * i.getQuantity()).sum();
        double snackTotal = user.getSnackCart().stream()
            .filter(SnackCartItem::isSelected)
            .mapToDouble(SnackCartItem::getTotalPrice).sum();
        double grand = ticketTotal + snackTotal;
        String text = LanguageManager.t(LanguageManager.CART_TOTAL)
            + ": " + String.format("%,.0f", grand) + " VND";
        totalLabel.setText(text);
    }

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
