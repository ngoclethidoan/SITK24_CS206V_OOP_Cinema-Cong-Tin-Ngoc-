package GUI;

import database.ItemDatabase;
import model.Item;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SnackPanel – Panel chọn bắp/nước trước khi thanh toán.
 *
 * Cách dùng:
 *   SnackPanel snackPanel = new SnackPanel();
 *   // sau khi user xác nhận:
 *   List<Item> selected = snackPanel.getSelectedItems();
 */
public class SnackPanel extends JPanel {

    // Mỗi hàng: item gốc từ DB + spinner
    private final List<Item>    catalogItems = new ArrayList<>();
    private final List<JSpinner> spinners    = new ArrayList<>();
    private JLabel totalLabel;

    public SnackPanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(28, 28, 28));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildUI();
    }

    // ─────────────────────────── BUILD UI ────────────────────────────
    private void buildUI() {
        // ── Title ──
        JLabel title = new JLabel("🍿  Thêm bắp/nước (không bắt buộc)");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        add(title, BorderLayout.NORTH);

        // ── Danh sách items ──
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(getBackground());

        // Phân nhóm
        listPanel.add(sectionLabel("🌽  Bắp rang"));
        for (Item item : ItemDatabase.getAll()) {
            if (isCorn(item)) addRow(listPanel, item);
        }

        listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        listPanel.add(sectionLabel("🥤  Nước uống"));
        for (Item item : ItemDatabase.getAll()) {
            if (isBeverage(item)) addRow(listPanel, item);
        }

        listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        listPanel.add(sectionLabel("🎁  Combo"));
        for (Item item : ItemDatabase.getAll()) {
            if (isCombo(item)) addRow(listPanel, item);
        }

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(getBackground());
        sp.setPreferredSize(new Dimension(480, 280));
        add(sp, BorderLayout.CENTER);

        // ── Tổng tiền phía dưới ──
        totalLabel = new JLabel("Tổng bắp/nước: 0 VND");
        totalLabel.setForeground(new Color(255, 215, 0));
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        add(totalLabel, BorderLayout.SOUTH);
    }

    // ─────────────────────────── HELPERS ─────────────────────────────
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Dialog", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 2, 2, 0));
        return lbl;
    }

    private void addRow(JPanel parent, Item item) {
        // Clone để không ảnh hưởng cache
        Item clone = cloneItem(item, 0);
        catalogItems.add(clone);

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(new Color(38, 38, 38));
        row.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Tên + giá
        JLabel nameLbl = new JLabel(
            item.getName() + "  –  " + String.format("%,.0f", item.getPrice()) + " VND"
        );
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("Dialog", Font.PLAIN, 13));

        // Spinner số lượng (0-10)
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinner.setPreferredSize(new Dimension(60, 28));
        spinner.addChangeListener(e -> {
            clone.setQuantity((int) spinner.getValue());
            refreshTotal();
        });

        spinners.add(spinner);

        row.add(nameLbl,  BorderLayout.CENTER);
        row.add(spinner, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private void refreshTotal() {
        double total = catalogItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        totalLabel.setText("Tổng bắp/nước: " + String.format("%,.0f", total) + " VND");
    }

    // ─────────────────────────── PUBLIC API ──────────────────────────

    /**
     * Trả về danh sách items người dùng đã chọn (quantity > 0).
     */
    public List<Item> getSelectedItems() {
        List<Item> result = new ArrayList<>();
        for (Item item : catalogItems) {
            if (item.getQuantity() > 0) {
                result.add(cloneItem(item, item.getQuantity()));
            }
        }
        return result;
    }

    /**
     * Tổng tiền bắp/nước đang chọn.
     */
    public double getSnackTotal() {
        return catalogItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    // ─────────────────────────── TYPE CHECKS ─────────────────────────
    private boolean isCorn(Item item) {
        return item instanceof model.Corn && !item.getCodeItem().toUpperCase().startsWith("COMBO");
    }
    private boolean isBeverage(Item item) {
        return item instanceof model.Beverage;
    }
    private boolean isCombo(Item item) {
        return item.getCodeItem().toUpperCase().startsWith("COMBO");
    }

    private Item cloneItem(Item src, int qty) {
        if (src instanceof model.Corn)     return new model.Corn(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        if (src instanceof model.Beverage) return new model.Beverage(src.getCodeItem(), src.getName(), src.getPrice(), qty);
        return new model.Item(src.getCodeItem(), src.getName(), src.getPrice(), qty);
    }

    // ─────────────────────────── DIALOG HELPER ───────────────────────

    /**
     * Mở dialog chọn bắp/nước. Trả về danh sách items đã chọn,
     * hoặc null nếu user nhấn Cancel/đóng.
     *
     * @param parent  Frame cha
     * @return List&lt;Item&gt; hoặc null
     */
    public static List<Item> showDialog(Component parent) {
        SnackPanel panel = new SnackPanel();

        int result = JOptionPane.showConfirmDialog(
            parent,
            panel,
            "Thêm bắp/nước vào đơn hàng",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return panel.getSelectedItems();
        }
        return null; // user nhấn Cancel
    }
}