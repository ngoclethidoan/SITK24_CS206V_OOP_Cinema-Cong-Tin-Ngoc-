package GUI;

import model.LanguageManager;
import model.*;
import database.BookingDatabase;
import database.RoomDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatPanel extends JPanel {

    private Film film;
    private MainFrame mainFrame;
    private final boolean bookMode;

    private final List<Seat> selectedSeats = new ArrayList<>();
    private JButton[][] buttons;
    private Seat[][] seats;
    private Room room;

    private JLabel lblSelected;
    private JLabel lblTotal;
    private JButton actionBtn;
    private JButton snackBtn; // ← snack button

    private static final Color BG        = new Color(19, 19, 19);
    private static final Color AVAILABLE = new Color(34, 139, 80);
    private static final Color SELECTED  = new Color(41, 128, 185);
    private static final Color BOOKED    = new Color(55, 55, 55);

    public SeatPanel(Film film, MainFrame mainFrame, boolean bookMode) {
        this.film      = film;
        this.mainFrame = mainFrame;
        this.bookMode  = bookMode;
        this.room      = RoomDatabase.getRoom(film.getRoomId());
        this.seats     = room.getSeats();

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTop(),    BorderLayout.NORTH);
        add(buildGrid(),   BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        LanguageManager.getInstance().addChangeListener(this::refreshLanguage);
    }

    // ── TOP ──────────────────────────────────────────────────────────
    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(BG);
        JButton back = new JButton(LanguageManager.t(LanguageManager.BTN_BACK));
        back.addActionListener(e -> mainFrame.showHome());
        JLabel title = new JLabel(film.getTitle() + " - " + room.getRoomId());
        title.setForeground(Color.WHITE);
        p.add(back);
        p.add(title);
        return p;
    }

    // ── GRID ─────────────────────────────────────────────────────────
    private JScrollPane buildGrid() {
        int rows = seats.length, cols = seats[0].length;
        buttons = new JButton[rows][cols];
        JPanel grid = new JPanel(new GridLayout(rows, cols, 6, 6));
        grid.setBackground(BG);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Seat seat = seats[r][c];
                JButton btn = new JButton(seat.getCodeSeat());
                btn.setForeground(Color.WHITE);
                applyColor(btn, seat);
                int rr = r, cc = c;
                btn.addActionListener(e -> toggleSeat(rr, cc));
                buttons[r][c] = btn;
                grid.add(btn);
            }
        }
        return new JScrollPane(grid);
    }

    // ── TOGGLE ───────────────────────────────────────────────────────
    private void toggleSeat(int r, int c) {
        Seat seat = seats[r][c];
        if (!seat.isAvailable()) return;
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
        } else {
            selectedSeats.add(seat);
        }
        refreshAll();
        refreshBottom();
    }

    private void applyColor(JButton btn, Seat seat) {
        if (!seat.isAvailable()) {
            btn.setBackground(BOOKED); btn.setEnabled(false);
        } else if (selectedSeats.contains(seat)) {
            btn.setBackground(SELECTED); btn.setEnabled(true);
        } else {
            btn.setBackground(AVAILABLE); btn.setEnabled(true);
        }
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private void refreshAll() {
        for (int r = 0; r < seats.length; r++)
            for (int c = 0; c < seats[0].length; c++)
                applyColor(buttons[r][c], seats[r][c]);
    }

    // ── BOTTOM ───────────────────────────────────────────────────────
    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));

        lblSelected = new JLabel(LanguageManager.t(LanguageManager.SEAT_NOT_SELECTED));
        lblSelected.setForeground(Color.LIGHT_GRAY);
        lblTotal = new JLabel(" ");
        lblTotal.setForeground(Color.YELLOW);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(lblSelected);
        info.add(lblTotal);
        p.add(info, BorderLayout.CENTER);

        // Buttons panel (right side)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        
        // 🍿 Snack button — only shown when seats are selected AND cart mode
        snackBtn = new JButton("🍿 " + LanguageManager.t("🍿 Bắp & Nước"));
        snackBtn.setBackground(new Color(160, 110, 20));
        snackBtn.setForeground(Color.WHITE);
        snackBtn.setFocusPainted(false);
        snackBtn.setEnabled(false); // disabled until seat selected
        snackBtn.addActionListener(e -> {
                                addSeatsToCart();
                                mainFrame.refreshCartBadge();
                                mainFrame.showSnackOrder();
                            });
        

        // Main action button
        actionBtn = new JButton(
            bookMode ? LanguageManager.t(LanguageManager.BTN_BOOK_NOW)
                     : LanguageManager.t(LanguageManager.BTN_ADD_CART)
        );
        actionBtn.setEnabled(false);
        actionBtn.addActionListener(e -> handleAction());
        btnPanel.add(snackBtn); 
        btnPanel.add(actionBtn);

        p.add(btnPanel, BorderLayout.EAST);
        return p;
    }

    private void refreshBottom() {
        if (selectedSeats.isEmpty()) {
            lblSelected.setText(LanguageManager.t(LanguageManager.SEAT_NOT_SELECTED));
            lblTotal.setText(" ");
            actionBtn.setEnabled(false);
            snackBtn.setEnabled(false);
            return;
        }
        double total = 0;
        StringBuilder sb = new StringBuilder(LanguageManager.t(LanguageManager.SEAT_SELECTED) + ": ");
        for (int i = 0; i < selectedSeats.size(); i++) {
            Seat s = selectedSeats.get(i);
            if (i > 0) sb.append(", ");
            sb.append(s.getCodeSeat());
            total += s.computePrice();
        }
        lblSelected.setText(sb.toString());
        lblTotal.setText(LanguageManager.t(LanguageManager.CART_TOTAL) + ": " + total + " VND");
        actionBtn.setEnabled(true);
        snackBtn.setEnabled(true);
        
    }

    // ── ACTION ───────────────────────────────────────────────────────
    private void handleAction() {
        if (selectedSeats.isEmpty()) return;

        if (bookMode) {
            // Book mode: go straight to payment
            mainFrame.showPay(
                selectedSeats.stream().map(s -> new CartItem(film, room, s)).toList(),
                false
            );
            return;
        }

        // Cart mode: reserve seats + save PENDING
        addSeatsToCart();
        mainFrame.refreshCartBadge();
        mainFrame.showCart();
    }

    /** Add seats to cart then open snack panel */
//    private void handleAddToCartThenSnack() {
//        if (selectedSeats.isEmpty()) return;
//        addSeatsToCart();
//        mainFrame.refreshCartBadge();
//        mainFrame.showSnackOrder(); // ← open snack panel after adding seats
//    }

    /** Marks seats as booked, adds to user cart, saves PENDING to bookings.csv */
    private void addSeatsToCart() {
        User user = mainFrame.getCurrentUser();
        List<CartItem> cart = user.getCart();

        for (Seat s : selectedSeats) {
            // Reserve the seat so other users can't pick it
            synchronized (s) {
                if (!s.isAvailable()) continue; // already taken
                s.setState(Seat.State.booked);
            }
            cart.add(new CartItem(film, room, s));

            // Persist as PENDING so it survives logout
            BookingDatabase.savePendingTicket(user.getUserId(), film, room, s);
        }
    }

    // ── LANGUAGE ─────────────────────────────────────────────────────
    private void refreshLanguage() {
        removeAll();
        add(buildTop(),    BorderLayout.NORTH);
        add(buildGrid(),   BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}
