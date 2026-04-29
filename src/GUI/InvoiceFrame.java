/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import model.*;
import service.BookingService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InvoiceFrame extends JDialog {

    private Film       film;
    private Seat       seat;
    private List<Item> items;
    private MainFrame  mainFrame;

    public InvoiceFrame(Film film, Seat seat, List<Item> items, MainFrame mainFrame) {
        this.film      = film;
        this.seat      = seat;
        this.items     = items;
        this.mainFrame = mainFrame;

        setTitle("Invoice & Payment");
        setSize(500, 620);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        add(createInvoicePanel(), BorderLayout.CENTER);
        add(createPaymentPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Invoice ───────────────────────────────────────────────────────
    private JPanel createInvoicePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(20, 20, 20));
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // ── Receipt card ─────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        // Header
        addLine(card, "CNT CINEMA",             18, Font.BOLD, Color.WHITE,             CENTER);
        addLine(card, "─────────────────────",   12, Font.PLAIN, new Color(80,80,80),   CENTER);
        addLine(card, "BOOKING INVOICE",         14, Font.BOLD, new Color(255,200,60),  CENTER);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Film info
        addLine(card, "FILM",                    11, Font.BOLD, new Color(150,150,150), LEFT);
        addLine(card, film.getTitle(),            14, Font.BOLD, Color.WHITE,            LEFT);
        addLine(card, "Duration: " + film.getDuration() + " mins",
                                                  12, Font.PLAIN,new Color(180,180,180), LEFT);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        addLine(card, "─────────────────────",   12, Font.PLAIN, new Color(60,60,60),   CENTER);

        // Seat info
        addLine(card, "SEAT",                    11, Font.BOLD, new Color(150,150,150), LEFT);
        String seatType = seat.getClass().getSimpleName().replace("Seat", "");
        addLine(card, seat.getCodeSeat() + "  (" + seatType + ")",
                                                  14, Font.BOLD, Color.WHITE,            LEFT);
        addLineRow(card, "Seat price:",
                String.format("%.0f VND", seat.computePrice()),
                new Color(255, 200, 60));
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Food items
        if (!items.isEmpty()) {
            addLine(card, "─────────────────────",12, Font.PLAIN, new Color(60,60,60),  CENTER);
            addLine(card, "FOOD & DRINKS",        11, Font.BOLD, new Color(150,150,150),LEFT);
            for (Item item : items) {
                String label = item.getName() + " x" + item.getQuantity();
                String val   = String.format("%.0f VND", item.getPrice() * item.getQuantity());
                addLineRow(card, label, val, new Color(200, 200, 200));
            }
        }

        card.add(Box.createRigidArea(new Dimension(0, 10)));
        addLine(card, "═════════════════════",   12, Font.BOLD, new Color(90,90,90),    CENTER);

        // Total
        double total = calcTotal();
        addLineRow(card, "TOTAL",
                String.format("%.0f VND", total),
                new Color(255, 220, 60));

        // User info
        if (mainFrame.isLoggedIn() && mainFrame.getCurrentUser() != null) {
            card.add(Box.createRigidArea(new Dimension(0, 12)));
            addLine(card, "─────────────────────",12, Font.PLAIN, new Color(60,60,60),  CENTER);
            addLine(card, "Customer: " + mainFrame.getCurrentUser().getName(),
                    12, Font.PLAIN, new Color(160,160,160), LEFT);
        }

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ── Payment buttons ───────────────────────────────────────────────
    private JPanel createPaymentPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(14, 30, 20, 30));

        JLabel totalLbl = new JLabel(String.format("Total:  %.0f VND", calcTotal()));
        totalLbl.setForeground(new Color(255, 220, 60));
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(totalLbl, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);

        JButton btnBack = new JButton("← Back");
        styleBtn(btnBack, new Color(70, 70, 70));
        btnBack.addActionListener(e -> {
            dispose();
            new FoodFrame(film, seat, mainFrame);
        });

        JButton btnPay = new JButton("✔ CONFIRM & PAY");
        styleBtn(btnPay, new Color(39, 174, 96));
        btnPay.addActionListener(e -> confirmPayment());

        btns.add(btnBack);
        btns.add(btnPay);
        p.add(btns, BorderLayout.EAST);
        return p;
    }

    // ── Xác nhận thanh toán ───────────────────────────────────────────
    private void confirmPayment() {
        // Kiểm tra đăng nhập
        if (!mainFrame.isLoggedIn() || mainFrame.getCurrentUser() == null) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You need to be logged in to complete booking.\nGo to login?",
                    "Login Required", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame(mainFrame);
            }
            return;
        }

        // Gọi BookingService để đặt vé
        BookingService service = new BookingService();
        Room room = new Room("R01", 48); // phòng mặc định
        BookTicket ticket = service.book(mainFrame.getCurrentUser(), room, seat, film, items);

        if (ticket != null) {
            // Đánh dấu ghế đã đặt
            seat.setState(Seat.State.booked);

            // Hiện thông báo thành công
            showSuccessDialog();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Booking failed. Seat may already be taken.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccessDialog() {
        JDialog success = new JDialog(this, "Booking Confirmed!", true);
        success.setSize(380, 220);
        success.setLocationRelativeTo(this);
        success.setLayout(new BorderLayout());
        success.getContentPane().setBackground(new Color(25, 60, 35));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(25, 60, 35));
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        addLine(content, "✅ BOOKING CONFIRMED!", 18, Font.BOLD, new Color(100, 255, 140), CENTER);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        addLine(content, film.getTitle(),          14, Font.BOLD, Color.WHITE,             CENTER);
        addLine(content, "Seat: " + seat.getCodeSeat()
                + "   Total: " + String.format("%.0f VND", calcTotal()),
                13, Font.PLAIN, new Color(200, 230, 200), CENTER);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JButton btnOk = new JButton("Back to Home");
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleBtn(btnOk, new Color(39, 174, 96));
        btnOk.addActionListener(e -> {
            success.dispose();
            dispose();
            mainFrame.setVisible(true);
            mainFrame.refreshUI();
        });
        content.add(btnOk);
        success.add(content, BorderLayout.CENTER);
        success.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private double calcTotal() {
        double total = seat.computePrice();
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private static final int LEFT = SwingConstants.LEFT;
    private static final int CENTER = SwingConstants.CENTER;

    private void addLine(JPanel p, String text, int size, int style,
                         Color color, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(color);
        lbl.setFont(new Font("Segoe UI Emoji", style, size));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, lbl.getPreferredSize().height + 4));
        p.add(lbl);
    }

    private void addLineRow(JPanel p, String left, String right, Color rightColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel lLeft = new JLabel(left);
        lLeft.setForeground(new Color(190, 190, 190));
        lLeft.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lRight = new JLabel(right);
        lRight.setForeground(rightColor);
        lRight.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(lLeft,  BorderLayout.WEST);
        row.add(lRight, BorderLayout.EAST);
        p.add(row);
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
    }
}
