package GUI;

import model.LanguageManager;
import database.BookingDatabase;
import database.ItemDatabase;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SnackOrderPanel
 * Modes:
 *  - CART mode   (pendingTickets == null): seats already in cart → add snacks → showCart()
 *  - BOOKING mode (pendingTickets != null): seats reserved       → add snacks → showPay()
 */
public class SnackOrderPanel extends JPanel {

    private static final Color BG      = new Color(20, 20, 20);
    private static final Color BG_CARD = new Color(32, 32, 32);
    private static final Color ACCENT  = new Color(46, 204, 113);
    private static final Color GOLD    = new Color(255, 215, 0);

    private final MainFrame      mainFrame;
    private final List<CartItem> pendingTickets;
    private final List<ItemEntry> cornEntries = new ArrayList<>();
    private final List<ItemEntry> bevEntries  = new ArrayList<>();
    private JLabel totalLabel;

    /** Cart mode */
    public SnackOrderPanel(MainFrame mainFrame) { this(mainFrame, null); }

    /** Booking mode */
    public SnackOrderPanel(MainFrame mainFrame, List<CartItem> pendingTickets) {
        this.mainFrame      = mainFrame;
        this.pendingTickets = pendingTickets;
        setLayout(new BorderLayout()); setBackground(BG);
        buildUI();
    }

    private boolean isBookingMode() { return pendingTickets != null; }

    private void buildUI() {
        removeAll();
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
        revalidate(); repaint();
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));

        JButton back = new JButton("← " + LanguageManager.t(LanguageManager.BTN_BACK));
        back.setForeground(Color.WHITE); back.setBackground(new Color(55, 55, 55));
        back.addActionListener(e -> handleBack());

        JLabel title = new JLabel("🍿 " + LanguageManager.t("snack.title"));
        title.setForeground(Color.WHITE); title.setFont(new Font("Dialog", Font.BOLD, 16));

        String hint = isBookingMode()
            ? "  Step 2: choose snacks or skip"
            : "  Optional: add snacks to your cart";
        JLabel hintLbl = new JLabel(hint);
        hintLbl.setForeground(new Color(150, 150, 150));
        hintLbl.setFont(new Font("Dialog", Font.ITALIC, 12));

        p.add(back); p.add(Box.createHorizontalStrut(10)); p.add(title); p.add(hintLbl);
        return p;
    }

    private void handleBack() {
        if (isBookingMode()) {
            // Release temporarily booked seats
            for (CartItem c : pendingTickets)
                synchronized (c.getSeat()) { c.getSeat().setState(Seat.State.available); }
            mainFrame.showHome();
        } else {
            // In cart mode: go back to cart (seats already added)
            mainFrame.showCart();
        }
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 0));
        center.setBackground(BG);
        center.add(buildSection(LanguageManager.t("snack.corn"), true));
        center.add(buildSection(LanguageManager.t("snack.drink"), false));
        return center;
    }

    private JPanel buildSection(String title, boolean isCorn) {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.LIGHT_GRAY); lbl.setFont(new Font("Dialog", Font.BOLD, 14));
        JPanel grid = new JPanel(); grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(BG);
        for (Item item : ItemDatabase.getAll()) {
            boolean match = isCorn ? (item instanceof Corn) : (item instanceof Beverage);
            if (match) {
                ItemEntry e = new ItemEntry(item);
                if (isCorn) cornEntries.add(e); else bevEntries.add(e);
                grid.add(buildCard(e)); grid.add(Box.createVerticalStrut(8));
            }
        }
        wrap.add(lbl, BorderLayout.NORTH); wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCard(ItemEntry entry) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD); card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel name  = new JLabel(entry.item.getName()); name.setForeground(Color.WHITE);
        JLabel price = new JLabel(String.format("%,.0f %s", entry.item.getPrice(),
            LanguageManager.t("currency"))); price.setForeground(new Color(200, 200, 200));
        JLabel qty = new JLabel("0"); qty.setForeground(Color.WHITE);
        JButton plus = new JButton("+"), minus = new JButton("-");
        plus.addActionListener(e  -> { entry.qty++;               qty.setText(String.valueOf(entry.qty)); refreshTotal(); });
        minus.addActionListener(e -> { if (entry.qty>0) entry.qty--; qty.setText(String.valueOf(entry.qty)); refreshTotal(); });
        JPanel right = new JPanel(); right.setOpaque(false); right.add(minus); right.add(qty); right.add(plus);
        JPanel left = new JPanel(new GridLayout(2, 1)); left.setOpaque(false); left.add(name); left.add(price);
        card.add(left, BorderLayout.CENTER); card.add(right, BorderLayout.EAST);
        return card;
    }

    private void refreshTotal() {
        double total = 0;
        for (ItemEntry e : cornEntries) total += e.item.getPrice() * e.qty;
        for (ItemEntry e : bevEntries)  total += e.item.getPrice() * e.qty;
        totalLabel.setText(LanguageManager.t("snack.total") + ": "
            + String.format("%,.0f %s", total, LanguageManager.t("currency")));
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(25, 25, 25));
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        totalLabel = new JLabel(LanguageManager.t("snack.total") + ": 0");
        totalLabel.setForeground(GOLD);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(totalLabel);

        // Skip button (both modes)
        JButton skip = new JButton("Skip →");
        skip.setBackground(new Color(70, 70, 70));
        skip.setForeground(Color.WHITE); skip.setFocusPainted(false);
        skip.addActionListener(e -> handleSkip());
        right.add(skip);

        // Main confirm button
        String btnText = isBookingMode() ? "Continue to Payment →" : "Add to Cart ✓";
        JButton confirm = new JButton(btnText);
        confirm.setBackground(ACCENT); confirm.setForeground(Color.WHITE); confirm.setFocusPainted(false);
        confirm.addActionListener(e -> handleConfirm());
        right.add(confirm);

        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void handleSkip() {
        if (isBookingMode()) {
            mainFrame.showPay(pendingTickets, new ArrayList<>(), false);
        } else {
            // Skip snacks, just go to cart
            JOptionPane.showMessageDialog(this, "Seats added to cart! ✅",
                "Added to Cart", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showCart();
        }
    }

    private void handleConfirm() {
        if (!mainFrame.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                LanguageManager.t(LanguageManager.MSG_MUST_LOGIN_FIRST),
                LanguageManager.t(LanguageManager.MSG_ERROR), JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Item> selected = new ArrayList<>();
        for (ItemEntry e : cornEntries) if (e.qty > 0) selected.add(clone(e.item, e.qty));
        for (ItemEntry e : bevEntries)  if (e.qty > 0) selected.add(clone(e.item, e.qty));

        if (selected.isEmpty()) {
            // No snacks selected — treat same as skip
            handleSkip();
            return;
        }

        if (isBookingMode()) {
            List<SnackCartItem> snacks = new ArrayList<>();
            snacks.add(new SnackCartItem(selected));
            mainFrame.showPay(pendingTickets, snacks, false);
        } else {
            // Cart mode: add snacks to user cart + persist
            SnackCartItem snackItem = new SnackCartItem(selected);
            mainFrame.getCurrentUser().addSnackToCart(snackItem);
            BookingDatabase.savePendingSnack(mainFrame.getCurrentUser().getUserId(), selected);

            JOptionPane.showMessageDialog(this,
                "Seats and snacks added to cart! ✅", "Added to Cart", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showCart();
        }
    }

    private Item clone(Item src, int qty) {
        if (src instanceof Corn)     return new Corn(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        if (src instanceof Beverage) return new Beverage(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        return new Item(src.getCodeItem(), src.getName(), src.getPrice(), qty);
    }

    private static class ItemEntry { Item item; int qty = 0; ItemEntry(Item i) { this.item = i; } }
}
