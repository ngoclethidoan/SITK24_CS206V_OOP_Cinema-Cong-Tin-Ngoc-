package GUI;

import database.ItemDatabase;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SnackOrderPanel – Trang đặt bắp/nước.
 * Click vào card để tăng số lượng +1. Nút − để giảm.
 * Tổng tiền + chi tiết hiển thị real-time ở thanh dưới.
 */
public class SnackOrderPanel extends JPanel {

    private static final Color BG       = new Color(20, 20, 20);
    private static final Color BG_CARD  = new Color(32, 32, 32);
    private static final Color BG_SEL   = new Color(45, 85, 45);
    private static final Color ACCENT   = new Color(46, 204, 113);
    private static final Color GOLD     = new Color(255, 215, 0);
    private static final Color CORN_COL = new Color(255, 210, 60);
    private static final Color BEV_COL  = new Color(80, 190, 240);

    private final MainFrame mainFrame;

    private final List<ItemEntry> cornEntries = new ArrayList<>();
    private final List<ItemEntry> bevEntries  = new ArrayList<>();

    private JLabel totalLabel;

    public SnackOrderPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top bar ───────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        p.setBackground(new Color(19, 19, 19));

        JButton back = new JButton("← " + LanguageManager.t(LanguageManager.BTN_BACK));
        back.setBackground(new Color(55, 55, 55));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> mainFrame.showHome());

        JLabel title = new JLabel("🍿  Đặt Bắp & Nước");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel hint = new JLabel("  ( click vào món để tăng số lượng )");
        hint.setForeground(new Color(150, 150, 150));
        hint.setFont(new Font("Dialog", Font.ITALIC, 12));

        p.add(back);
        p.add(Box.createHorizontalStrut(10));
        p.add(title);
        p.add(hint);
        return p;
    }

    // ── Center: 2 cột bắp | nước ─────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        center.add(buildSection("🌽  Bắp rang", CORN_COL, cornEntries, true));
        center.add(buildSection("🥤  Nước uống", BEV_COL,  bevEntries,  false));
        return center;
    }

    private JPanel buildSection(String title, Color color,
                                List<ItemEntry> entries, boolean isCorn) {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);

        JLabel hdr = new JLabel(title);
        hdr.setForeground(color);
        hdr.setFont(new Font("Dialog", Font.BOLD, 15));
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 55)),
            BorderFactory.createEmptyBorder(4, 4, 8, 4)
        ));
        wrap.add(hdr, BorderLayout.NORTH);

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));

        for (Item item : ItemDatabase.getAll()) {
            boolean match = isCorn ? (item instanceof Corn) : (item instanceof Beverage);
            if (match) {
                ItemEntry entry = new ItemEntry(item);
                entries.add(entry);
                grid.add(buildCard(entry, color));
                grid.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    // ── Card ──────────────────────────────────────────────────────────
    private JPanel buildCard(ItemEntry entry, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 55)),
            BorderFactory.createEmptyBorder(12, 14, 12, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Trái: tên + giá
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel nameLbl  = new JLabel(entry.item.getName());
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel priceLbl = new JLabel(String.format("%,.0f VND / phần", entry.item.getPrice()));
        priceLbl.setForeground(accentColor);
        priceLbl.setFont(new Font("Dialog", Font.PLAIN, 12));

        left.add(nameLbl);
        left.add(Box.createVerticalStrut(3));
        left.add(priceLbl);

        // Phải: nút − + badge số lượng
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        right.setOpaque(false);

        JLabel qtyLbl = new JLabel("0", SwingConstants.CENTER);
        qtyLbl.setForeground(Color.WHITE);
        qtyLbl.setFont(new Font("Dialog", Font.BOLD, 16));
        qtyLbl.setPreferredSize(new Dimension(30, 30));

        JButton minus = makeBtn("−", new Color(100, 40, 40));

        minus.addActionListener(e -> {
            if (entry.qty > 0) {
                entry.qty--;
                qtyLbl.setText(String.valueOf(entry.qty));
                card.setBackground(entry.qty > 0 ? BG_SEL : BG_CARD);
                refreshTotal();
            }
        });

        right.add(minus);
        right.add(qtyLbl);

        card.add(left,  BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        // Click vào card (trừ nút −) → tăng +1
        MouseAdapter addOne = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                entry.qty++;
                qtyLbl.setText(String.valueOf(entry.qty));
                card.setBackground(BG_SEL);
                refreshTotal();
            }
        };
        card.addMouseListener(addOne);
        left.addMouseListener(addOne);
        nameLbl.addMouseListener(addOne);
        priceLbl.addMouseListener(addOne);
        // qtyLbl và minus KHÔNG gắn addOne để tránh double-count

        return card;
    }

    // ── Bottom bar ────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(new Color(25, 25, 25));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(55, 55, 55)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        totalLabel = new JLabel("Tổng: 0 VND");
        totalLabel.setForeground(GOLD);
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton addBtn = new JButton("🛒  Thêm vào Giỏ hàng");
        addBtn.setBackground(ACCENT);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Dialog", Font.BOLD, 14));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addToCart());

        p.add(totalLabel, BorderLayout.CENTER);
        p.add(addBtn,     BorderLayout.EAST);
        return p;
    }

    // ─────────────────────────── LOGIC ───────────────────────────────

    private void refreshTotal() {
        double total = 0;
        StringBuilder detail = new StringBuilder();

        for (ItemEntry e : cornEntries) {
            total += e.item.getPrice() * e.qty;
            if (e.qty > 0) appendDetail(detail, e);
        }
        for (ItemEntry e : bevEntries) {
            total += e.item.getPrice() * e.qty;
            if (e.qty > 0) appendDetail(detail, e);
        }

        if (total == 0) {
            totalLabel.setText("Tổng: 0 VND");
        } else {
            totalLabel.setText("Tổng: " + String.format("%,.0f VND", total)
                + (detail.length() > 0 ? "     " + detail : ""));
        }
    }

    private void appendDetail(StringBuilder sb, ItemEntry e) {
        if (sb.length() > 0) sb.append("  ·  ");
        sb.append(e.item.getName()).append(" ×").append(e.qty);
    }

    private void addToCart() {
        if (!mainFrame.isLoggedIn() || mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "Vui lòng đăng nhập để thêm vào giỏ hàng.",
                "Chưa đăng nhập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Item> selected = new ArrayList<>();
        for (ItemEntry e : cornEntries) if (e.qty > 0) selected.add(cloneItem(e.item, e.qty));
        for (ItemEntry e : bevEntries)  if (e.qty > 0) selected.add(cloneItem(e.item, e.qty));

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Bạn chưa chọn món nào!", "Giỏ trống", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SnackCartItem cartItem = new SnackCartItem(selected);
        mainFrame.getCurrentUser().addSnackToCart(cartItem);
        mainFrame.refreshCartBadge();

        JOptionPane.showMessageDialog(mainFrame,
            "✅  Đã thêm " + cartItem.getTotalQty() + " món vào giỏ!\n"
            + "Tổng: " + String.format("%,.0f VND", cartItem.getTotalPrice()),
            "Thêm thành công", JOptionPane.INFORMATION_MESSAGE);

        resetAll();
    }

    private void resetAll() {
        cornEntries.clear();
        bevEntries.clear();
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    // ─────────────────────────── HELPERS ─────────────────────────────

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Dialog", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Item cloneItem(Item src, int qty) {
        if (src instanceof Corn)     return new Corn(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        if (src instanceof Beverage) return new Beverage(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        return new Item(src.getCodeItem(), src.getName(), src.getPrice(), qty);
    }

    private static class ItemEntry {
        final Item item;
        int qty = 0;
        ItemEntry(Item item) { this.item = item; }
    }
}