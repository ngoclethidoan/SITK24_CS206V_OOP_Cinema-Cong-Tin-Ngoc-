package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static GUI.LanguageManager.t;

public class PayPanel extends JPanel {

    private final MainFrame mainFrame;
    private final List<CartItem> items;   // ⭐ nhận từ SeatPanel / CartPanel

    private JLabel totalLabel;
    private JPanel listPanel;
    
    private final boolean fromCart;

    public PayPanel(MainFrame mainFrame, List<CartItem> items, boolean fromCart) {
        this.mainFrame = mainFrame;
        this.items = items;
        this.fromCart = fromCart;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        buildUI();

        LanguageManager.getInstance()
                .addChangeListener(this::reload);
    }
    // ================= ROOT =================
    private void buildUI() {
        removeAll();

        add(top(), BorderLayout.NORTH);
        add(center(), BorderLayout.CENTER);
        add(bottom(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void reload() {
        buildUI();
    }

    // ================= TOP =================
    private JPanel top() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(getBackground());

        JButton back = new JButton(t(LanguageManager.BTN_BACK));

        back.addActionListener(e -> {
            if (fromCart) {
                mainFrame.showCart();
            } else {
                mainFrame.showHome();
            }
        });

        p.add(back);
        return p;
    }

    // ================= CENTER =================
    private JScrollPane center() {

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(getBackground());

        double total = 0;

        for (CartItem i : items) {

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row.setBackground(getBackground());

            // ⭐ POSTER
            JLabel poster = new JLabel();
            ImageIcon icon = new ImageIcon(i.getFilm().getImagePath());

            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage()
                        .getScaledInstance(60, 85, Image.SCALE_SMOOTH);
                poster.setIcon(new ImageIcon(img));
            } else {
                poster.setPreferredSize(new Dimension(60, 85));
                poster.setText("No Image");
                poster.setForeground(Color.GRAY);
            }

            // ⭐ INFO
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(getBackground());

            JLabel title = new JLabel(i.getFilm().getTitle());
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Dialog", Font.BOLD, 14));

            JLabel seat = new JLabel(
                    t(LanguageManager.CART_SEAT) + ": " + i.getSeat().getCodeSeat()
            );
            seat.setForeground(Color.LIGHT_GRAY);

            info.add(title);
            info.add(seat);

            row.add(poster);
            row.add(info);

            listPanel.add(row);

            total += i.getSeat().computePrice();
        }

        totalLabel = new JLabel(
                t(LanguageManager.CART_TOTAL)
                        + ": " + String.format("%,.0f", total) + " VND"
        );

        totalLabel.setForeground(Color.YELLOW);

        listPanel.add(Box.createVerticalStrut(10));
        listPanel.add(totalLabel);

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(20); // ⭐ smooth scroll
        sp.getHorizontalScrollBar().setUnitIncrement(20);


        return sp;
    }

    // ================= BOTTOM =================
    private JPanel bottom() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));

        JButton book = new JButton(t(LanguageManager.BTN_BOOK_NOW));

        book.addActionListener(e -> book());

        p.add(book, BorderLayout.EAST);

        return p;
    }

    // ================= BOOK ACTION =================
    private void book() {

        User u = mainFrame.getCurrentUser();

        for (CartItem i : items) {

            // 1. set ghế đã đặt
            i.getSeat().setState(Seat.State.booked);

            // 2. tạo ticket history
            BookTicket ticket = new BookTicket(
                    i.getSeat().getRoom(),
                    i.getSeat(),
                    i.getFilm(),
                    i.getSeat().computePrice()
            );

                // 3. thêm vào lịch sử
                u.addBooking(ticket);
            }

        // Chỉ xoá cart nếu đi từ cart
        if (fromCart) {
            u.getCart().removeIf(CartItem::isSelected);
        }

        mainFrame.refreshCartBadge();
        mainFrame.showHome();
    }
}