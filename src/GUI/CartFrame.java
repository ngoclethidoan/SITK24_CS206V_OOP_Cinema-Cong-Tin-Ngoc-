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

/**
 * CartFrame — Hiển thị giỏ hàng (vé đã đặt) và cho phép thanh toán ngân hàng.
 */
public class CartFrame extends JDialog {

    private MainFrame mainFrame;
    private BookingService bookingService = new BookingService();

    public CartFrame(MainFrame mainFrame) {
        super(mainFrame, "🛒 Giỏ hàng của bạn", true);
        this.mainFrame = mainFrame;

        setSize(600, 650);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        // Kiểm tra đăng nhập
        if (!mainFrame.isLoggedIn() || mainFrame.getCurrentUser() == null) {
            showNotLoggedIn();
        } else {
            buildCartUI();
        }

        setVisible(true);
    }

    // ── Trường hợp chưa đăng nhập ────────────────────────────────────
    private void showNotLoggedIn() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        JLabel lbl = new JLabel("⚠  Bạn cần đăng nhập để xem giỏ hàng", SwingConstants.CENTER);
        lbl.setForeground(new Color(255, 200, 60));
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        p.add(lbl, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Đăng nhập ngay");
        styleBtn(btnLogin, new Color(39, 174, 96));
        btnLogin.addActionListener(e -> {
            dispose();
            new LoginFrame(mainFrame);
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnLogin);
        p.add(btnPanel, BorderLayout.SOUTH);
        add(p);
    }

    // ── Giao diện giỏ hàng chính ──────────────────────────────────────
    private void buildCartUI() {
        User user = mainFrame.getCurrentUser();
        List<BookTicket> history = user.getBookingHistory();

        add(createHeader(user), BorderLayout.NORTH);

        if (history.isEmpty()) {
            add(createEmptyPanel(), BorderLayout.CENTER);
        } else {
            add(createTicketList(history), BorderLayout.CENTER);
            add(createBottomBar(history), BorderLayout.SOUTH);
        }
    }

    // ── Header ────────────────────────────────────────────────────────
    private JPanel createHeader(User user) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🛒  Giỏ hàng — " + user.getName());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        p.add(title, BorderLayout.WEST);

        int count = user.getBookingHistory().size();
        JLabel countLbl = new JLabel(count + " vé");
        countLbl.setForeground(new Color(255, 200, 60));
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(countLbl, BorderLayout.EAST);
        return p;
    }

    // ── Giỏ rỗng ─────────────────────────────────────────────────────
    private JPanel createEmptyPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));

        JLabel lbl = new JLabel("Bạn chưa đặt vé nào 🎬", SwingConstants.CENTER);
        lbl.setForeground(new Color(120, 120, 120));
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);

        JButton btnBrowse = new JButton("Xem phim ngay");
        styleBtn(btnBrowse, new Color(231, 76, 60));
        btnBrowse.addActionListener(e -> dispose());
        JPanel bp = new JPanel();
        bp.setOpaque(false);
        bp.add(btnBrowse);
        p.add(bp, BorderLayout.SOUTH);
        return p;
    }

    // ── Danh sách vé ─────────────────────────────────────────────────
    private JScrollPane createTicketList(List<BookTicket> history) {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(20, 20, 20));
        list.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        for (int i = 0; i < history.size(); i++) {
            BookTicket ticket = history.get(i);
            list.add(createTicketCard(ticket, i));
            list.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(20, 20, 20));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // ── Card từng vé ─────────────────────────────────────────────────
    private JPanel createTicketCard(BookTicket ticket, int index) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // LEFT — thông tin vé
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel filmLbl = new JLabel("🎬  " + ticket.getFilm().getTitle());
        filmLbl.setForeground(Color.WHITE);
        filmLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        info.add(filmLbl);
        info.add(Box.createRigidArea(new Dimension(0, 5)));

        String seatType = ticket.getSeat().getClass().getSimpleName().replace("Seat", "");
        JLabel seatLbl = new JLabel("Ghế: " + ticket.getSeat().getCodeSeat() + "  (" + seatType + ")");
        seatLbl.setForeground(new Color(180, 180, 180));
        seatLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.add(seatLbl);

        // Items (đồ ăn)
        if (!ticket.getItems().isEmpty()) {
            StringBuilder sb = new StringBuilder("Đồ ăn: ");
            for (Item it : ticket.getItems()) {
                sb.append(it.getName()).append(" x").append(it.getQuantity()).append("  ");
            }
            JLabel itemLbl = new JLabel(sb.toString().trim());
            itemLbl.setForeground(new Color(150, 200, 150));
            itemLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            info.add(itemLbl);
        }

        card.add(info, BorderLayout.CENTER);

        // RIGHT — giá + nút huỷ
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        double total = bookingService.calcTotal(ticket);
        JLabel priceLbl = new JLabel(String.format("%.0f VND", total));
        priceLbl.setForeground(new Color(255, 200, 60));
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(priceLbl);
        right.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnCancel = new JButton("Huỷ vé");
        styleBtn(btnCancel, new Color(150, 40, 40));
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnCancel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn chắc chắn muốn huỷ vé \"" + ticket.getFilm().getTitle() + "\"?",
                    "Xác nhận huỷ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bookingService.cancel(mainFrame.getCurrentUser(), ticket);
                dispose();
                new CartFrame(mainFrame);
            }
        });
        right.add(btnCancel);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // ── Bottom bar — tổng tiền + thanh toán ──────────────────────────
    private JPanel createBottomBar(List<BookTicket> history) {
        double grandTotal = history.stream()
                .mapToDouble(t -> bookingService.calcTotal(t))
                .sum();

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));

        JLabel totalLbl = new JLabel(String.format("Tổng cộng:  %.0f VND", grandTotal));
        totalLbl.setForeground(new Color(255, 220, 60));
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(totalLbl, BorderLayout.WEST);

        JButton btnPay = new JButton("💳  Thanh toán ngân hàng");
        styleBtn(btnPay, new Color(39, 174, 96));
        btnPay.addActionListener(e -> openBankPayment(grandTotal, history));
        p.add(btnPay, BorderLayout.EAST);
        return p;
    }

    // ── Màn hình nhập số tài khoản ngân hàng ─────────────────────────
    private void openBankPayment(double total, List<BookTicket> history) {
        JDialog payDialog = new JDialog(this, "💳  Thanh toán ngân hàng", true);
        payDialog.setSize(440, 420);
        payDialog.setLocationRelativeTo(this);
        payDialog.setLayout(new BorderLayout());
        payDialog.getContentPane().setBackground(new Color(22, 22, 22));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(22, 22, 22));
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        // Tiêu đề
        JLabel header = new JLabel("Nhập thông tin thanh toán");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel subHeader = new JLabel(String.format("Số tiền cần thanh toán:  %.0f VND", total));
        subHeader.setForeground(new Color(255, 200, 60));
        subHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        subHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subHeader);
        content.add(Box.createRigidArea(new Dimension(0, 22)));

        // Chọn ngân hàng
        content.add(makeFieldLabel("Ngân hàng:"));
        String[] banks = {"Vietcombank", "VietinBank", "BIDV", "Techcombank",
                          "MB Bank", "VPBank", "ACB", "TPBank"};
        JComboBox<String> bankCombo = new JComboBox<>(banks);
        styleCombo(bankCombo);
        bankCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(bankCombo);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        // Số tài khoản
        content.add(makeFieldLabel("Số tài khoản (đầy đủ):"));
        JTextField accountField = makeTextField("VD: 0123456789012");
        content.add(accountField);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        // Tên chủ tài khoản
        content.add(makeFieldLabel("Tên chủ tài khoản:"));
        JTextField nameField = makeTextField("VD: NGUYEN VAN A");
        content.add(nameField);
        content.add(Box.createRigidArea(new Dimension(0, 22)));

        // Nút thanh toán
        JButton btnConfirm = new JButton("✔  Xác nhận thanh toán");
        styleBtn(btnConfirm, new Color(39, 174, 96));
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConfirm.addActionListener(e -> {
            String acct = accountField.getText().trim();
            String name = nameField.getText().trim();
            String bank = (String) bankCombo.getSelectedItem();

            // Validate: chỉ số, đủ 8-20 ký tự
            if (acct.isEmpty() || !acct.matches("\\d{8,20}")) {
                JOptionPane.showMessageDialog(payDialog,
                        "Số tài khoản phải là dãy số từ 8 đến 20 chữ số!",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(payDialog,
                        "Vui lòng nhập tên chủ tài khoản!",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            payDialog.dispose();
            showPaymentSuccess(bank, acct, name, total, history);
        });
        content.add(btnConfirm);

        payDialog.add(content, BorderLayout.CENTER);
        payDialog.setVisible(true);
    }

    // ── Thông báo thanh toán thành công ───────────────────────────────
    private void showPaymentSuccess(String bank, String acct,
                                    String ownerName, double total,
                                    List<BookTicket> history) {
        JDialog dlg = new JDialog(this, "✅  Thanh toán thành công!", true);
        dlg.setSize(400, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(new Color(18, 50, 30));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(18, 50, 30));
        p.setBorder(BorderFactory.createEmptyBorder(28, 36, 16, 36));

        addCenteredLabel(p, "✅  THANH TOÁN THÀNH CÔNG!", 18, Font.BOLD, new Color(80, 255, 130));
        p.add(Box.createRigidArea(new Dimension(0, 14)));
        addCenteredLabel(p, bank + "  —  " + maskAccount(acct), 13, Font.PLAIN, new Color(200, 230, 200));
        addCenteredLabel(p, "Chủ TK: " + ownerName.toUpperCase(), 13, Font.PLAIN, new Color(200, 230, 200));
        p.add(Box.createRigidArea(new Dimension(0, 8)));
        addCenteredLabel(p, String.format("Đã thanh toán:  %.0f VND", total),
                15, Font.BOLD, new Color(255, 220, 60));
        addCenteredLabel(p, history.size() + " vé của bạn đã được xác nhận!", 13, Font.PLAIN, Color.WHITE);
        p.add(Box.createRigidArea(new Dimension(0, 22)));

        JButton btnHome = new JButton("Về trang chủ");
        btnHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleBtn(btnHome, new Color(39, 174, 96));
        btnHome.addActionListener(e -> {
            dlg.dispose();
            dispose();
            mainFrame.setVisible(true);
            mainFrame.refreshUI();
        });
        p.add(btnHome);
        dlg.add(p, BorderLayout.CENTER);
        dlg.setVisible(true);
    }

    // ── Ẩn số tài khoản (chỉ hiện 4 số cuối) ─────────────────────────
    private String maskAccount(String acct) {
        if (acct.length() <= 4) return acct;
        return "*".repeat(acct.length() - 4) + acct.substring(acct.length() - 4);
    }

    // ── Helper UI ─────────────────────────────────────────────────────
    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(45, 45, 45));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Placeholder simulation
        tf.setText(placeholder);
        tf.setForeground(new Color(100, 100, 100));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(new Color(100, 100, 100));
                }
            }
        });
        return tf;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(new Color(45, 45, 45));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
    }

    private void addCenteredLabel(JPanel p, String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(color);
        lbl.setFont(new Font("Segoe UI Emoji", style, size));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, lbl.getPreferredSize().height + 4));
        p.add(lbl);
    }
}