package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {

    private MainFrame mainFrame;
    private JLabel totalLabel = new JLabel("Total: 0");

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
        back.addActionListener(e -> mainFrame.showHome());

        p.add(back);
        return p;
    }

    private JPanel createNotLogin() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));

        JLabel lbl = new JLabel("⚠ You have not logged in!", SwingConstants.CENTER);
        lbl.setForeground(Color.YELLOW);

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private void buildCartUI() {

        User user = mainFrame.getCurrentUser();
        List<CartItem> cart = user.getCart();

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(20, 20, 20));

        for (CartItem item : cart) {
            list.add(createCartCard(item));
            list.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);

        add(sp, BorderLayout.CENTER);
        add(createBottomBar(user), BorderLayout.SOUTH);

        updateTotal(user);
    }

    private JPanel createCartCard(CartItem item) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(35, 35, 35));
        card.setPreferredSize(new Dimension(900, 140));

        // CHECKBOX & POSTER
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);

        JCheckBox check = new JCheckBox();
        check.setSelected(item.isSelected());
        check.addActionListener(e -> {
            item.setSelected(check.isSelected());
            updateTotal(mainFrame.getCurrentUser());
        });

        left.add(check);

        JLabel poster = new JLabel();
        ImageIcon icon = new ImageIcon(item.getFilm().getImagePath());
        Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
        poster.setIcon(new ImageIcon(img));

        left.add(poster);

        card.add(left, BorderLayout.WEST);
        // CENTER INFO
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(item.getFilm().getTitle());
        title.setForeground(Color.WHITE);

        JLabel seat = new JLabel("Seat: " + item.getSeat().getCodeSeat());
        seat.setForeground(Color.GRAY);

        center.add(title);
        center.add(seat);

        card.add(center, BorderLayout.CENTER);

        // DELETE
        JButton del = new JButton("X");
        del.setBackground(Color.RED);
        del.setForeground(Color.WHITE);

        del.addActionListener(e -> {
            mainFrame.getCurrentUser().getCart().remove(item);
            mainFrame.showCart();
        });

        card.add(del, BorderLayout.EAST);

        return card;
    }

    private JPanel createBottomBar(User user) {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));

        totalLabel.setForeground(Color.YELLOW);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton pay = new JButton("PAY");
        pay.setBackground(new Color(46, 204, 113));

        pay.addActionListener(e -> openPayPanel());

        p.add(totalLabel, BorderLayout.WEST);
        p.add(pay, BorderLayout.EAST);

        return p;
    }

    private void updateTotal(User user) {

        double total = 0;

        for (CartItem item : user.getCart()) {
            if (item.isSelected()) {
                total += item.getSeat().computePrice() * item.getQuantity();
            }
        }

        totalLabel.setText("Total: " + total + " VND");
    }

    private void openPayPanel() {
        mainFrame.showPay();
    }
}