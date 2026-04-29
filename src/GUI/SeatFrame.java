/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatFrame extends JDialog {

    // Layout: 6 rows (A-F) x 8 columns
    private static final int ROWS = 6;
    private static final int COLS = 8;
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F"};

    // Seat types by row:
    // A = VIP, B-C = Premium, D-E = Standard, F = Couple
    private Seat[][] seats = new Seat[ROWS][COLS];
    private JButton[][] seatBtns = new JButton[ROWS][COLS];

    private Seat selectedSeat = null;
    private JLabel lblSelected;
    private JLabel lblPrice;

    private Film film;
    private MainFrame mainFrame;

    public SeatFrame(Film film, MainFrame mainFrame) {
        this.film      = film;
        this.mainFrame = mainFrame;

        setTitle("Select Seat — " + film.getTitle());
        setSize(720, 580);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(19, 19, 19));

        initSeats();

        add(createScreenPanel(), BorderLayout.NORTH);
        add(createSeatGrid(),    BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Khởi tạo toàn bộ ghế ─────────────────────────────────────────
    private void initSeats() {
        double basePrice = film.getPrice();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                String code = ROW_LABELS[r] + (c + 1);
                seats[r][c] = makeSeat(code, r, c, basePrice);
            }
        }
        // Demo: mark some seats as booked
        seats[0][2].setState(Seat.State.booked);
        seats[0][5].setState(Seat.State.booked);
        seats[1][3].setState(Seat.State.booked);
        seats[2][1].setState(Seat.State.booked);
        seats[2][6].setState(Seat.State.booked);
        seats[3][4].setState(Seat.State.booked);
    }

    private Seat makeSeat(String code, int row, int col, double base) {
        switch (row) {
            case 0: return new VIPSeat(code, row, col, base);
            case 1: case 2: return new PremiumSeat(code, row, col, base);
            case 3: case 4: return new StandardSeat(code, row, col, base);
            default: return new CoupleSeat(code, row, col, base);
        }
    }

    // ── "SCREEN" banner ───────────────────────────────────────────────
    private JPanel createScreenPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(19, 19, 19));
        p.setBorder(BorderFactory.createEmptyBorder(18, 60, 6, 60));

        JLabel screen = new JLabel("▬▬▬▬▬▬  SCREEN  ▬▬▬▬▬▬", SwingConstants.CENTER);
        screen.setForeground(new Color(180, 180, 180));
        screen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        screen.setBackground(new Color(50, 50, 50));
        screen.setOpaque(true);
        screen.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        p.add(screen, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        legend.setOpaque(false);
        legend.add(legendItem(new Color(212, 175, 55),  "VIP (×1.5)"));
        legend.add(legendItem(new Color(52, 152, 219),  "Premium (×2)"));
        legend.add(legendItem(new Color(46, 204, 113),  "Standard"));
        legend.add(legendItem(new Color(155, 89, 182),  "Couple (×1.8)"));
        legend.add(legendItem(new Color(100, 100, 100), "Booked"));
        p.add(legend, BorderLayout.SOUTH);
        return p;
    }

    private JLabel legendItem(Color color, String text) {
        JLabel lbl = new JLabel("■ " + text);
        lbl.setForeground(color);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    // ── Grid ghế ─────────────────────────────────────────────────────
    private JPanel createSeatGrid() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(19, 19, 19));

        JPanel grid = new JPanel(new GridLayout(ROWS, COLS + 1, 8, 8));
        grid.setBackground(new Color(19, 19, 19));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        for (int r = 0; r < ROWS; r++) {
            // Row label
            JLabel rowLbl = new JLabel(ROW_LABELS[r], SwingConstants.CENTER);
            rowLbl.setForeground(new Color(160, 160, 160));
            rowLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            grid.add(rowLbl);

            for (int c = 0; c < COLS; c++) {
                final int fr = r, fc = c;
                JButton btn = new JButton(String.valueOf(c + 1));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(62, 44));
                btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

                colorSeatBtn(btn, seats[r][c]);

                btn.addActionListener(e -> onSeatClick(fr, fc, btn));
                seatBtns[r][c] = btn;
                grid.add(btn);
            }
        }
        wrapper.add(grid);
        return wrapper;
    }

    private void colorSeatBtn(JButton btn, Seat seat) {
        if (!seat.isAvailable()) {
            btn.setBackground(new Color(100, 100, 100));
            btn.setForeground(new Color(60, 60, 60));
            btn.setEnabled(false);
            return;
        }
        btn.setEnabled(true);
        btn.setForeground(Color.WHITE);
        if (seat instanceof VIPSeat)      btn.setBackground(new Color(150, 120, 30));
        else if (seat instanceof PremiumSeat) btn.setBackground(new Color(30, 100, 160));
        else if (seat instanceof CoupleSeat)  btn.setBackground(new Color(110, 60, 150));
        else                                  btn.setBackground(new Color(30, 130, 80));
    }

    private void onSeatClick(int r, int c, JButton btn) {
        // Deselect cũ
        if (selectedSeat != null) {
            int pr = selectedSeat.getRow(), pc = selectedSeat.getColumn();
            colorSeatBtn(seatBtns[pr][pc], seats[pr][pc]);
        }
        // Chọn mới
        selectedSeat = seats[r][c];
        btn.setBackground(new Color(255, 200, 0));
        btn.setForeground(new Color(30, 30, 30));

        String type = selectedSeat.getClass().getSimpleName().replace("Seat","");
        lblSelected.setText("Selected: " + selectedSeat.getCodeSeat() + "  (" + type + ")");
        lblPrice.setText(String.format("Price: %.0f VND", selectedSeat.computePrice()));
    }

    // ── Bottom: info + Next ───────────────────────────────────────────
    private JPanel createBottomPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);

        lblSelected = new JLabel("No seat selected");
        lblSelected.setForeground(new Color(200, 200, 200));
        lblSelected.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblPrice = new JLabel("");
        lblPrice.setForeground(new Color(255, 220, 80));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));

        info.add(lblSelected);
        info.add(lblPrice);
        p.add(info, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnBack = new JButton("← Back");
        styleBtn(btnBack, new Color(70, 70, 70));
        btnBack.addActionListener(e -> dispose());

        JButton btnNext = new JButton("Next: Food & Drinks →");
        styleBtn(btnNext, new Color(46, 204, 113));
        btnNext.addActionListener(e -> {
            if (selectedSeat == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a seat first!", "No seat", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dispose();
            new FoodFrame(film, selectedSeat, mainFrame);
        });

        btnPanel.add(btnBack);
        btnPanel.add(btnNext);
        p.add(btnPanel, BorderLayout.EAST);
        return p;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }
}
