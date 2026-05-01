package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PayPanel extends JPanel {

    private MainFrame mainFrame;

    private JLabel totalLabel;

    public PayPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        add(createTop(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    // ================= TOP =================
    private JPanel createTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));

        JButton back = new JButton("⬅ BACK");
        back.addActionListener(e -> mainFrame.showCart());

        p.add(back);
        return p;
    }

    // ================= CENTER =================
    private JScrollPane createCenter() {

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(20, 20, 20));

        User user = mainFrame.getCurrentUser();
        List<CartItem> cart = user.getCart();

        for (CartItem item : cart) {

            if (!item.isSelected()) continue;

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setOpaque(false);

            JLabel lbl = new JLabel(
                    item.getFilm().getTitle()
                    + " | Room: " + item.getRoom().getRoomId()
                    + " | Seat: " + item.getSeat().getCodeSeat()
            );

            lbl.setForeground(Color.WHITE);

            row.add(lbl);
            list.add(row);
        }

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        return sp;
    }

    // ================= BOTTOM =================
    private JPanel createBottom() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));

        totalLabel = new JLabel();
        totalLabel.setForeground(Color.YELLOW);
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        updateTotal();

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(Color.RED);
        cancel.setForeground(Color.WHITE);

        cancel.addActionListener(e -> mainFrame.showCart());

        JButton book = new JButton("Book Now");
        book.setBackground(new Color(46, 204, 113));
        book.setForeground(Color.WHITE);

        book.addActionListener(e -> processBooking());

        p.add(cancel, BorderLayout.WEST);
        p.add(totalLabel, BorderLayout.CENTER);
        p.add(book, BorderLayout.EAST);

        return p;
    }

    // ================= TOTAL PRICE =================
    private void updateTotal() {

        double total = 0;

        for (CartItem item : mainFrame.getCurrentUser().getCart()) {

            if (item.isSelected()) {
                total += item.getSeat().computePrice();
            }
        }

        totalLabel.setText("Total: " + total + " VND");
    }

    // ================= BOOK LOGIC =================
    private void processBooking() {

        User user = mainFrame.getCurrentUser();

        List<CartItem> cart = user.getCart();

        // 1. MARK SEATS AS BOOKED (IMPORTANT FIX)
        for (CartItem item : cart) {

            if (item.isSelected()) {

                Seat seat = item.getSeat();

                // update seat state in ROOM (important for SeatPanel refresh)
                seat.setState(Seat.State.booked);
            }
        }

        // 2. REMOVE SELECTED ITEMS FROM CART
        cart.removeIf(CartItem::isSelected);

        // 3. RETURN HOME
        mainFrame.showHome();
    }
}