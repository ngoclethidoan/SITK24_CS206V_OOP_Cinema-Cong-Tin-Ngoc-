package GUI;

import model.*;
import database.RoomDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatPanel extends JPanel {

    private Film film;
    private MainFrame mainFrame;

    // Danh sách ghế đang chọn (multi-select)
    private final List<Seat> selectedSeats = new ArrayList<>();
    private JButton[][] buttons;

    private Seat[][] seats;
    private Room room;

    // Bottom bar components
    private JLabel lblSelected;
    private JLabel lblTotal;
    private JButton addBtn;

    // ── Màu ghế ─────────────────────────────────────────────────────
    private static final Color BG           = new Color(19, 19, 19);
    private static final Color CLR_AVAILABLE = new Color(34, 139, 80);   // xanh lá
    private static final Color CLR_SELECTED  = new Color(41, 128, 185);  // xanh dương
    private static final Color CLR_BOOKED    = new Color(55, 55, 55);    // xám tối
    private static final Color CLR_BOOKED_TXT = new Color(100, 100, 100);
    private static final Color BOTTOM_BG    = new Color(28, 28, 35);

    // ── Constructor ──────────────────────────────────────────────────
    public SeatPanel(Film film, MainFrame mainFrame) {
        this.film      = film;
        this.mainFrame = mainFrame;

        this.room  = RoomDatabase.getRoom(film.getRoomId());
        this.seats = room.getSeats();

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTop(),    BorderLayout.NORTH);
        add(buildGrid(),   BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    // ── TOP BAR ──────────────────────────────────────────────────────
    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        p.setBackground(BG);

        JButton back = new JButton("⬅ Quay lại");
        styleTopBtn(back, new Color(60, 60, 60));
        back.addActionListener(e -> mainFrame.showHome());
        p.add(back);

        JLabel title = new JLabel("  " + film.getTitle() + "  —  " + room.getRoomId());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 15));
        p.add(title);

        // Chú thích màu
        p.add(Box.createHorizontalStrut(24));
        p.add(legend(CLR_AVAILABLE, "Ghế trống"));
        p.add(Box.createHorizontalStrut(10));
        p.add(legend(CLR_BOOKED,    "Ghế đã đặt"));
        return p;
    }

    private JPanel legend(Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        JLabel dot = new JLabel("■");
        dot.setForeground(color);
        dot.setFont(new Font("Dialog", Font.PLAIN, 18));
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(170, 170, 170));
        lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        row.add(dot);
        row.add(lbl);
        return row;
    }

    // ── SEAT GRID ────────────────────────────────────────────────────
    private JPanel buildGrid() {
        int rows = seats.length;
        int cols = seats[0].length;
        buttons = new JButton[rows][cols];

        JPanel grid = new JPanel(new GridLayout(rows, cols, 7, 7));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = makeBtn(seats[r][c]);
                final int fr = r, fc = c;
                btn.addActionListener(e -> onSeatClick(fr, fc));
                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    /** Tạo JButton ghế với style cross-platform (hoạt động cả macOS/Windows) */
    private JButton makeBtn(Seat seat) {
        JButton btn = new JButton(seat.getCodeSeat()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Custom paint để đảm bảo màu hiển thị đúng mọi L&F
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // tắt fill mặc định, dùng custom paint
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(56, 40));
        applyColor(btn, seat);
        return btn;
    }

    // ── XỬ LÝ CLICK GHẾ ─────────────────────────────────────────────
    private void onSeatClick(int r, int c) {
        Seat seat = seats[r][c];

        // Bỏ qua ghế đã đặt
        if (!seat.isAvailable()) return;

        // Toggle chọn/bỏ chọn
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
        } else {
            selectedSeats.add(seat);
        }

        applyColor(buttons[r][c], seat);
        refreshBottom();
    }

    // ── ÁP DỤNG MÀU NÚT ─────────────────────────────────────────────
    private void applyColor(JButton btn, Seat seat) {
        if (!seat.isAvailable()) {
            btn.setBackground(CLR_BOOKED);
            btn.setForeground(CLR_BOOKED_TXT);
            btn.setEnabled(false);
        } else if (selectedSeats.contains(seat)) {
            btn.setBackground(CLR_SELECTED);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(true);
        } else {
            btn.setBackground(CLR_AVAILABLE);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(true);
        }
        btn.repaint();
    }

    // ── BOTTOM BAR ───────────────────────────────────────────────────
    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(BOTTOM_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        lblSelected = new JLabel("Chưa chọn ghế nào");
        lblSelected.setForeground(new Color(160, 160, 160));
        lblSelected.setFont(new Font("Dialog", Font.PLAIN, 13));

        lblTotal = new JLabel(" ");
        lblTotal.setForeground(new Color(255, 220, 50));
        lblTotal.setFont(new Font("Dialog", Font.BOLD, 16));

        info.add(lblSelected);
        info.add(Box.createRigidArea(new Dimension(0, 5)));
        info.add(lblTotal);
        p.add(info, BorderLayout.CENTER);

        addBtn = new JButton("Thêm vào giỏ  🛒") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled()
                        ? (getModel().isRollover()
                            ? new Color(52, 180, 219)
                            : new Color(41, 128, 185))
                        : new Color(60, 60, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        addBtn.setFont(new Font("Dialog", Font.BOLD, 14));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setOpaque(false);
        addBtn.setEnabled(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        addBtn.addActionListener(e -> doAddToCart());
        p.add(addBtn, BorderLayout.EAST);

        return p;
    }

    // ── CẬP NHẬT BOTTOM REALTIME ─────────────────────────────────────
    private void refreshBottom() {
        if (selectedSeats.isEmpty()) {
            lblSelected.setText("Chưa chọn ghế nào");
            lblTotal.setText(" ");
            addBtn.setEnabled(false);
        } else {
            StringBuilder sb = new StringBuilder("Ghế: ");
            double total = 0;
            for (int i = 0; i < selectedSeats.size(); i++) {
                if (i > 0) sb.append("  +  ");
                Seat s = selectedSeats.get(i);
                sb.append(s.getCodeSeat())
                  .append(" (")
                  .append(String.format("%,.0f", s.computePrice()))
                  .append(" đ)");
                total += s.computePrice();
            }
            lblSelected.setText(sb.toString());
            lblTotal.setText("Tổng: " + String.format("%,.0f", total)
                    + " VND  —  " + selectedSeats.size() + " vé");
            addBtn.setEnabled(true);
        }
        addBtn.repaint();
    }

    // ── THÊM VÀO CART ────────────────────────────────────────────────
    private void doAddToCart() {
        if (selectedSeats.isEmpty()) return;

        List<CartItem> cart = mainFrame.getCurrentUser().getCart();
        for (Seat seat : selectedSeats) {
            seat.setState(Seat.State.booked);
            cart.add(new CartItem(film, room, seat));
        }

        // Refresh màu toàn bộ grid sau khi đặt
        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[0].length; c++) {
                applyColor(buttons[r][c], seats[r][c]);
            }
        }

        selectedSeats.clear();
        refreshBottom();

        mainFrame.refreshCartBadge();
        mainFrame.showCart();
    }

    // ── HELPER ───────────────────────────────────────────────────────
    private void styleTopBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Reload room data khi panel được hiển thị
        // (cùng instance từ HashMap nên seats[] vẫn đồng bộ với buttons[])
        this.room  = RoomDatabase.getRoom(film.getRoomId());
        this.seats = room.getSeats();
    }
}