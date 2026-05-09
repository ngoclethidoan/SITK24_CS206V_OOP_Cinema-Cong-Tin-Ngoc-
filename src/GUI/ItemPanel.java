package GUI;

import javax.swing.*;
import java.awt.*;
import model.Item;
import model.CartItem;
import model.Film;
import java.util.ArrayList;
import java.util.List;

public class ItemPanel extends JPanel {
    private List<Item> availableItems;
    private MainFrame mainFrame;
    private List<CartItem> seatItems; // Holds seats passed from SeatPanel
    private Film currentFilm;

    public ItemPanel(MainFrame mainFrame, Film film, List<CartItem> seatItems) {
        this.mainFrame = mainFrame;
        this.seatItems = seatItems;
        this.currentFilm = film;

        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));

        // --- Center: Items List ---
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(19, 19, 19));
        
        availableItems = new ArrayList<>();
        availableItems.add(new Item("COMBO1", "Combo Bắp Rang + Pepsi", 75000, 0));
        availableItems.add(new Item("WA1", "Nước suối", 20000, 0));

        for (Item item : availableItems) {
            listContainer.add(createItemRow(item));
            listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        scrollPane.getViewport().setBackground(new Color(19, 19, 19));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Navigation Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setOpaque(false);

        JButton btnBack = new JButton("Quay lại chọn ghế");
        styleButton(btnBack, new Color(70, 70, 70));
        btnBack.addActionListener(e -> mainFrame.showSeatPanel(currentFilm, true));

        JButton btnNext = new JButton("Tiếp tục thanh toán");
        styleButton(btnNext, new Color(39, 120, 80));
        btnNext.addActionListener(e -> proceedToPay());

        buttonPanel.add(btnBack);
        buttonPanel.add(btnNext);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createItemRow(Item item) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(800, 60));
        row.setBackground(new Color(30, 30, 30));
        row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblName = new JLabel(item.getName() + " - " + item.getPrice() + " VNĐ");
        lblName.setForeground(Color.WHITE);
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinner.setPreferredSize(new Dimension(60, 30));
        spinner.addChangeListener(e -> item.setQuantity((int) spinner.getValue()));

        row.add(lblName, BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        return row;
    }

    private void proceedToPay() {
        // Merge seats and selected snacks into one list for the PayPanel
        List<CartItem> allItems = new ArrayList<>(seatItems);
        
        for (Item item : availableItems) {
            if (item.getQuantity() > 0) {
                // Converting Item to CartItem for the payment system
                allItems.add(new CartItem(item.getName(), item.getQuantity(), item.getPrice()));
            }
        }
        
        mainFrame.showPay(allItems, false);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}