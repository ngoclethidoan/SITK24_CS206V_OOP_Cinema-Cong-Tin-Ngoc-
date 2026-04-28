/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import model.*;
import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class FoodFrame extends JDialog {

    // Danh sách đồ ăn/nước cố định
    private static final Object[][] MENU = {
        // {loai, code, ten, gia, so_luong_kho}
        {"Corn",     "CRN01", "Popcorn (Salted)",   45000, 50},
        {"Corn",     "CRN02", "Popcorn (Caramel)",  50000, 50},
        {"Corn",     "CRN03", "Popcorn (Cheese)",   55000, 50},
        {"Beverage", "BVG01", "Coca-Cola",          30000, 100},
        {"Beverage", "BVG02", "Pepsi",              30000, 100},
        {"Beverage", "BVG03", "Orange Juice",       35000, 80},
        {"Beverage", "BVG04", "Water",              15000, 200},
    };

    private Map<Item, JSpinner> spinners = new LinkedHashMap<>();
    private Film film;
    private Seat seat;
    private MainFrame mainFrame;

    public FoodFrame(Film film, Seat seat, MainFrame mainFrame) {
        this.film      = film;
        this.seat      = seat;
        this.mainFrame = mainFrame;

        setTitle("Food & Drinks");
        setSize(540, 520);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 25, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────
    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("🍿 Food & Drinks");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        p.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Seat: " + seat.getCodeSeat()
                + "  |  Film: " + film.getTitle());
        sub.setForeground(new Color(160, 160, 160));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    // ── Menu panel ────────────────────────────────────────────────────
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 25));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        String lastType = "";
        for (Object[] row : MENU) {
            String type    = (String) row[0];
            String code    = (String) row[1];
            String name    = (String) row[2];
            double price   = ((Number) row[3]).doubleValue();
            int    qty     = ((Number) row[4]).intValue();

            // Section header
            if (!type.equals(lastType)) {
                JLabel section = new JLabel(type.equals("Corn") ? "🍿 Popcorn" : "🥤 Beverages");
                section.setForeground(new Color(255, 200, 60));
                section.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
                section.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
                panel.add(section);
                lastType = type;
            }

            // Item row
            Item item = type.equals("Corn")
                    ? new Corn(code, name, price, qty)
                    : new Beverage(code, name, price, qty);

            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setBackground(new Color(35, 35, 35));
            rowPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 55, 55)),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel lblName = new JLabel(name);
            lblName.setForeground(Color.WHITE);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel lblPrice = new JLabel(String.format("%.0f VND", price));
            lblPrice.setForeground(new Color(255, 200, 60));
            lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JPanel leftPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            leftPanel.setOpaque(false);
            leftPanel.add(lblName);
            leftPanel.add(lblPrice);

            SpinnerNumberModel spinModel = new SpinnerNumberModel(0, 0, 10, 1);
            JSpinner spinner = new JSpinner(spinModel);
            spinner.setPreferredSize(new Dimension(65, 28));
            styleSpinner(spinner);

            spinners.put(item, spinner);

            rowPanel.add(leftPanel,   BorderLayout.CENTER);
            rowPanel.add(spinner,     BorderLayout.EAST);
            panel.add(rowPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(25, 25, 25));
        wrapper.add(scroll);
        return wrapper;
    }

    // ── Bottom bar ────────────────────────────────────────────────────
    private JPanel createBottomBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel lblSkip = new JLabel("(You can skip and proceed without ordering food)");
        lblSkip.setForeground(new Color(130, 130, 130));
        lblSkip.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        p.add(lblSkip, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);

        JButton btnBack = new JButton("← Back");
        styleBtn(btnBack, new Color(70, 70, 70));
        btnBack.addActionListener(e -> {
            dispose();
            new SeatFrame(film, mainFrame);
        });

        JButton btnNext = new JButton("Proceed to Payment →");
        styleBtn(btnNext, new Color(231, 76, 60));
        btnNext.addActionListener(e -> {
            List<Item> selectedItems = getSelectedItems();
            dispose();
            new InvoiceFrame(film, seat, selectedItems, mainFrame);
        });

        btns.add(btnBack);
        btns.add(btnNext);
        p.add(btns, BorderLayout.EAST);
        return p;
    }

    // ── Lấy danh sách item đã chọn (qty > 0) ─────────────────────────
    private List<Item> getSelectedItems() {
        List<Item> list = new ArrayList<>();
        for (Map.Entry<Item, JSpinner> entry : spinners.entrySet()) {
            int qty = (int) entry.getValue().getValue();
            if (qty > 0) {
                Item original = entry.getKey();
                // Tạo item copy với quantity = số lượng chọn
                Item ordered = original instanceof Corn
                        ? new Corn(original.getCodeItem(), original.getName(), original.getPrice(), qty)
                        : new Beverage(original.getCodeItem(), original.getName(), original.getPrice(), qty);
                list.add(ordered);
            }
        }
        return list;
    }

    private void styleSpinner(JSpinner sp) {
        sp.setBackground(new Color(50, 50, 50));
        sp.setForeground(Color.WHITE);
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(new Color(50, 50, 50));
            tf.setForeground(Color.WHITE);
            tf.setHorizontalAlignment(JTextField.CENTER);
        }
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
