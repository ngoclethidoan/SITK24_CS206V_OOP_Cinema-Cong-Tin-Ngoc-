package GUI;

import model.LanguageManager;
import model.*;
import database.BookingDatabase;
import database.RoomDatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatPanel extends JPanel {

    private final Film film;
    private final MainFrame mainFrame;
    private final boolean bookMode;
    private final List<Seat> selectedSeats = new ArrayList<>();
    private JButton[][] buttons;
    private final Seat[][] seats;
    private final Room room;
    private JLabel lblSelected, lblTotal;
    private JButton actionBtn;

    private static final Color BG       = new Color(19, 19, 19);
    private static final Color BOOKED   = new Color(55, 55, 55);
    private static final Color SELECTED = new Color(41, 128, 185);
    private static final Color C_STD    = new Color(34, 139, 80);
    private static final Color C_REC    = new Color(52, 152, 219);
    private static final Color C_VIP    = new Color(200, 150, 0);
    private static final Color C_PREM   = new Color(160, 50, 180);
    private static final Color C_COUPLE = new Color(210, 60, 100);

    public SeatPanel(Film film, MainFrame mainFrame, boolean bookMode) {
        this.film = film; this.mainFrame = mainFrame; this.bookMode = bookMode;
        this.room = RoomDatabase.getRoom(film.getRoomId());
        this.seats = room.getSeats();
        setLayout(new BorderLayout()); setBackground(BG);
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        LanguageManager.getInstance().addChangeListener(this::refreshLanguage);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(BG);
        JButton back = new JButton(LanguageManager.t(LanguageManager.BTN_BACK));
        back.addActionListener(e -> mainFrame.showFilmDetail(film)); // ← back to film, not home
        JLabel title = new JLabel("🎬 " + film.getTitle() + "  |  🏢 " + room.getRoomId());
        title.setForeground(Color.WHITE); title.setFont(new Font("Dialog", Font.BOLD, 14));
        p.add(back); p.add(Box.createHorizontalStrut(10)); p.add(title);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(BG); p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(buildGridArea(), BorderLayout.CENTER);
        p.add(buildLegend(), BorderLayout.EAST);
        return p;
    }

    private JPanel buildGridArea() {
        JPanel area = new JPanel(new BorderLayout(0, 8)); area.setBackground(BG);
        JPanel screen = new JPanel(); screen.setBackground(new Color(70, 70, 70));
        screen.setPreferredSize(new Dimension(0, 28));
        JLabel sc = new JLabel("◀  S C R E E N  ▶");
        sc.setForeground(new Color(200, 200, 200)); sc.setFont(new Font("Dialog", Font.BOLD, 12));
        screen.add(sc); area.add(screen, BorderLayout.NORTH);

        int rows = seats.length, cols = seats[0].length;
        buttons = new JButton[rows][cols];
        JPanel outer = new JPanel(new BorderLayout(4, 4)); outer.setBackground(BG);
        JPanel colHdr = new JPanel(new GridLayout(1, cols + 1, 4, 0)); colHdr.setBackground(BG);
        colHdr.add(new JLabel());
        for (int c = 0; c < cols; c++) {
            JLabel l = new JLabel(String.valueOf(c + 1), SwingConstants.CENTER);
            l.setForeground(new Color(140, 140, 140)); l.setFont(new Font("Dialog", Font.PLAIN, 10));
            colHdr.add(l);
        }
        outer.add(colHdr, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(rows, cols + 1, 4, 4)); grid.setBackground(BG);
        for (int r = 0; r < rows; r++) {
            JLabel rl = new JLabel(String.valueOf((char)('A' + r)), SwingConstants.CENTER);
            rl.setForeground(new Color(140, 140, 140)); rl.setFont(new Font("Dialog", Font.BOLD, 11));
            grid.add(rl);
            for (int c = 0; c < cols; c++) {
                Seat seat = seats[r][c];
                JButton btn = new JButton(seat.getCodeSeat());
                btn.setFont(new Font("Dialog", Font.PLAIN, 9)); btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false); btn.setToolTipText(tipText(seat));
                applyColor(btn, seat);
                int rr = r, cc = c;
                btn.addActionListener(e -> toggleSeat(rr, cc));
                buttons[r][c] = btn; grid.add(btn);
            }
        }
        outer.add(grid, BorderLayout.CENTER);
        JScrollPane sp = new JScrollPane(outer); sp.setBorder(null);
        sp.getViewport().setBackground(BG); area.add(sp, BorderLayout.CENTER);
        return area;
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(28, 28, 28));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)), new EmptyBorder(12, 14, 12, 14)));
        p.setPreferredSize(new Dimension(180, 0));
        JLabel t = new JLabel("Seat Types"); t.setForeground(Color.WHITE);
        t.setFont(new Font("Dialog", Font.BOLD, 12)); p.add(t); p.add(Box.createVerticalStrut(10));
        double base = 80_000;
        addLR(p, C_STD, "Standard", base); addLR(p, C_REC, "Recliner", base*1.3);
        addLR(p, C_VIP, "VIP", base*1.5); addLR(p, C_PREM, "Premium", base*2.0);
        addLR(p, C_COUPLE, "Couple", base*1.8);
        p.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); p.add(sep);
        p.add(Box.createVerticalStrut(8));
        addLR(p, SELECTED, "Selected", -1); addLR(p, BOOKED, "Booked", -1);
        return p;
    }

    private void addLR(JPanel p, Color c, String name, double price) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JPanel sw = new JPanel(); sw.setBackground(c); sw.setPreferredSize(new Dimension(16, 12));
        sw.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
        JLabel nl = new JLabel(name + (price > 0 ? ": "+String.format("%,.0f",price) : ""));
        nl.setForeground(Color.LIGHT_GRAY); nl.setFont(new Font("Dialog", Font.PLAIN, 10));
        row.add(sw); row.add(nl); p.add(row);
    }

    private void toggleSeat(int r, int c) {
        Seat seat = seats[r][c]; if (!seat.isAvailable()) return;
        if (selectedSeats.contains(seat)) selectedSeats.remove(seat);
        else selectedSeats.add(seat);
        refreshAll(); refreshBottom();
    }

    private void applyColor(JButton btn, Seat seat) {
        if (!seat.isAvailable())               { btn.setBackground(BOOKED);        btn.setEnabled(false); }
        else if (selectedSeats.contains(seat)) { btn.setBackground(SELECTED);      btn.setEnabled(true); }
        else                                   { btn.setBackground(typeColor(seat));btn.setEnabled(true); }
        btn.setOpaque(true); btn.setBorderPainted(false);
    }

    private void refreshAll() {
        for (int r=0;r<seats.length;r++) for (int c=0;c<seats[0].length;c++) applyColor(buttons[r][c],seats[r][c]);
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(new Color(30,30,30));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblSelected = new JLabel(LanguageManager.t(LanguageManager.SEAT_NOT_SELECTED));
        lblSelected.setForeground(Color.LIGHT_GRAY);
        lblTotal = new JLabel(" "); lblTotal.setForeground(Color.YELLOW);
        JPanel info = new JPanel(); info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(lblSelected); info.add(lblTotal); p.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); btnPanel.setOpaque(false);
        String label = bookMode ? LanguageManager.t(LanguageManager.BTN_BOOK_NOW)
                                : LanguageManager.t(LanguageManager.BTN_ADD_CART);
        actionBtn = new JButton(label); actionBtn.setEnabled(false);
        actionBtn.setBackground(bookMode ? new Color(46,204,113) : new Color(52,152,219));
        actionBtn.setForeground(Color.WHITE); actionBtn.setFocusPainted(false);
        actionBtn.addActionListener(e -> handleAction());
        btnPanel.add(actionBtn); p.add(btnPanel, BorderLayout.EAST);
        return p;
    }

    private void refreshBottom() {
        if (selectedSeats.isEmpty()) {
            lblSelected.setText(LanguageManager.t(LanguageManager.SEAT_NOT_SELECTED));
            lblTotal.setText(" "); actionBtn.setEnabled(false); return;
        }
        double total = 0;
        StringBuilder sb = new StringBuilder(LanguageManager.t(LanguageManager.SEAT_SELECTED)+": ");
        for (int i=0;i<selectedSeats.size();i++) {
            Seat s = selectedSeats.get(i); if (i>0) sb.append(", ");
            sb.append(s.getCodeSeat()).append("(").append(typeName(s)).append(")");
            total += s.computePrice();
        }
        lblSelected.setText(sb.toString());
        lblTotal.setText(LanguageManager.t(LanguageManager.CART_TOTAL)+": "+String.format("%,.0f",total)+" VND");
        actionBtn.setEnabled(true);
    }

    private void handleAction() {
        if (selectedSeats.isEmpty()) return;
        List<CartItem> tickets = selectedSeats.stream().map(s -> new CartItem(film, room, s)).toList();
        if (bookMode) {
            // Book Now → reserve seats → snack selection → pay
            for (CartItem c : tickets) synchronized (c.getSeat()) { c.getSeat().setState(Seat.State.booked); }
            mainFrame.showSnackForBooking(tickets);
        } else {
            // Add to Cart → add seats → snack selection → cart
            addSeatsToCart();
            mainFrame.showSnackAfterCart();
        }
    }

    private void addSeatsToCart() {
        User user = mainFrame.getCurrentUser();
        for (Seat s : selectedSeats) {
            synchronized (s) { if (!s.isAvailable()) continue; s.setState(Seat.State.booked); }
            user.getCart().add(new CartItem(film, room, s));
            BookingDatabase.savePendingTicket(user.getUserId(), film, room, s);
        }
    }

    private Color typeColor(Seat s) {
        if (s instanceof VIPSeat)     return C_VIP;  if (s instanceof PremiumSeat) return C_PREM;
        if (s instanceof ReclineSeat) return C_REC;  if (s instanceof CoupleSeat)  return C_COUPLE;
        return C_STD;
    }
    private String typeName(Seat s) {
        if (s instanceof VIPSeat)     return "VIP";     if (s instanceof PremiumSeat) return "Premium";
        if (s instanceof ReclineSeat) return "Recliner"; if (s instanceof CoupleSeat)  return "Couple";
        return "Std";
    }
    private String tipText(Seat s) { return typeName(s)+" Seat — "+String.format("%,.0f VND",s.computePrice()); }

    private void refreshLanguage() {
        removeAll(); add(buildTop(),BorderLayout.NORTH); add(buildCenter(),BorderLayout.CENTER);
        add(buildBottom(),BorderLayout.SOUTH); revalidate(); repaint();
    }
}
