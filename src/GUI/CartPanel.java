package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel totalLabel;
    private JPanel listPanel;   // rebuildable

    public CartPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        add(createTopBar(), BorderLayout.NORTH);

        if (!mainFrame.isLoggedIn() || mainFrame.getCurrentUser() == null) {
            add(createNotLogin(), BorderLayout.CENTER);
        } else {
            buildCartUI();
        }
    }

    private JPanel createTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));
        JButton back = new JButton("⬅ BACK");
        back.setFont(new Font("Dialog", Font.PLAIN, 13));
        back.addActionListener(e -> mainFrame.showHome());
        p.add(back);
        return p;
    }

    private JPanel createNotLogin() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        JLabel lbl = new JLabel("⚠ You have not logged in!", SwingConstants.CENTER);
        lbl.setForeground(Color.YELLOW);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private void buildCartUI() {
        User user = mainFrame.getCurrentUser();
        List<CartItem> cart = user.getCart();

        // Scrollable list
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(20, 20, 20));
        rebuildList(cart);

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(20, 20, 20));

        add(sp, BorderLayout.CENTER);
        add(createBottomBar(user), BorderLayout.SOUTH);
    }

    /** Xây lại danh sách cart items – gọi mỗi khi thêm/xóa */
    private void rebuildList(List<CartItem> cart) {
        listPanel.removeAll();
        for (CartItem item : cart) {
            listPanel.add(createCartCard(item, cart));
            listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty.", SwingConstants.CENTER);
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Dialog", Font.ITALIC, 15));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            listPanel.add(empty);
        }
        listPanel.revalidate();
        listPanel.repaint();
        updateTotal(mainFrame.getCurrentUser());
    }

    private JPanel createCartCard(CartItem item, List<CartItem> cart) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Left: checkbox + poster
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JCheckBox check = new JCheckBox();
        check.setSelected(item.isSelected());
        check.setOpaque(false);
        check.addActionListener(e -> {
            item.setSelected(check.isSelected());
            updateTotal(mainFrame.getCurrentUser());
        });
        left.add(check);

        JLabel poster = new JLabel();
        java.io.File imgFile = new java.io.File(item.getFilm().getImagePath());
        if (imgFile.exists()) {
            Image img = new ImageIcon(item.getFilm().getImagePath())
                            .getImage().getScaledInstance(70, 100, Image.SCALE_SMOOTH);
            poster.setIcon(new ImageIcon(img));
        }
        left.add(poster);
        card.add(left, BorderLayout.WEST);

        // Center: info
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(item.getFilm().getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel seat = new JLabel("Seat: " + item.getSeat().getCodeSeat());
        seat.setForeground(Color.GRAY);
        seat.setFont(new Font("Dialog", Font.PLAIN, 13));

        JLabel price = new JLabel("Price: " + (int) item.getSeat().computePrice() + " VND");
        price.setForeground(new Color(100, 200, 100));
        price.setFont(new Font("Dialog", Font.PLAIN, 13));

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(seat);
        center.add(price);
        card.add(center, BorderLayout.CENTER);

        // Right: delete
        JButton del = new JButton("✕");
        del.setFont(new Font("Dialog", Font.BOLD, 14));
        del.setBackground(new Color(180, 40, 40));
        del.setForeground(Color.WHITE);
        del.setFocusPainted(false);
        del.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));
        del.addActionListener(e -> {
            cart.remove(item);
            rebuildList(cart);          // ← live update không cần reset UI
            mainFrame.refreshCartBadge(); // cập nhật số trên TopBar
        });
        card.add(del, BorderLayout.EAST);

        return card;
    }

    private JPanel createBottomBar(User user) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        totalLabel = new JLabel("Total: 0 VND");
        totalLabel.setForeground(Color.YELLOW);
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        updateTotal(user);

        JButton pay = new JButton("PAY");
        pay.setBackground(new Color(46, 204, 113));
        pay.setForeground(Color.WHITE);
        pay.setFont(new Font("Dialog", Font.BOLD, 14));
        pay.setFocusPainted(false);
        pay.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        pay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pay.addActionListener(e -> mainFrame.showPay());

        p.add(totalLabel, BorderLayout.WEST);
        p.add(pay, BorderLayout.EAST);
        return p;
    }

    private void updateTotal(User user) {
        if (totalLabel == null || user == null) return;
        double total = user.getCart().stream()
            .filter(CartItem::isSelected)
            .mapToDouble(i -> i.getSeat().computePrice() * i.getQuantity())
            .sum();
        totalLabel.setText("Total: " + (long) total + " VND");
    }
}