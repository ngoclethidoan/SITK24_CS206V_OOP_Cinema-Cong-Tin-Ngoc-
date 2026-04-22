/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author Administrator
 */
import javax.swing.*;
import java.awt.*;

public class FilmFrame extends JFrame {

    public FilmFrame(String movieTitle, String director, String cast, int duration, String summary, Dimension size) {
        setTitle(movieTitle);
        setSize(size);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- THANH TRÊN CÙNG (Chứa nút Back) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JButton btnBack = new JButton("⬅ BACK");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Xử lý sự kiện khi nhấn nút Quay lại
        btnBack.addActionListener(e -> {
            // Get the last size and position of login frame
            Rectangle currentBounds = getBounds(); 

            // Open main frame
            // Inittialize MainFrame with Dimension from LoginFrame
            MainFrame mainFrame = new MainFrame(currentBounds.getSize());

            // Position the MainFrame so that it matches the LoginFrame
            mainFrame.setLocation(currentBounds.getLocation());

            mainFrame.setVisible(true);
            // Đóng FilmFrame hiện tại
            this.dispose();
        });

        topPanel.add(btnBack);

        // --- PHẦN NỘI DUNG CHI TIẾT ---
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Xếp chồng theo chiều dọc
    centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    
    JLabel lblName = new JLabel(movieTitle);
    lblName.setFont(new Font("Arial", Font.BOLD, 28));
    lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel lblInfo = new JLabel("<html><b>Director:</b> " + director + 
                                "<br><b>Cast:</b> " + cast + 
                                "<br><b>Duration:</b> " + duration + " mins</html>");
    lblInfo.setFont(new Font("Arial", Font.PLAIN, 16));
    lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JTextArea txtSummary = new JTextArea(summary);
    txtSummary.setLineWrap(true);
    txtSummary.setWrapStyleWord(true);
    txtSummary.setEditable(false);
    txtSummary.setOpaque(false);
    txtSummary.setFont(new Font("Arial", Font.ITALIC, 14));

    centerPanel.add(lblName);
    centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách
    centerPanel.add(lblInfo);
    centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    centerPanel.add(txtSummary);

    // Thêm vào Frame
    add(topPanel, BorderLayout.NORTH);
    add(new JScrollPane(centerPanel), BorderLayout.CENTER); // Thêm scroll nếu nội dung dài
        
        // --- PHẦN NÚT BẤM Ở DƯỚI (South) ---
        // GridLayout(1, 2, 10, 0): 1 hàng, 2 cột, khoảng cách giữa 2 nút là 10px
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Tạo khoảng cách với viền ngoài
        actionButtonPanel.setOpaque(false);

        // 1. Nút Thêm vào giỏ hàng (Bên trái)
        JButton btnAddToCart = new JButton("ADD TO CART 🛒");
        btnAddToCart.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));
        btnAddToCart.setBackground(new Color(52, 152, 219)); // Màu xanh dương
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. Nút Đặt vé (Bên phải)
        JButton btnBookNow = new JButton("BOOK NOW 🎫");
        btnBookNow.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));
        btnBookNow.setBackground(new Color(46, 204, 113)); // Màu xanh lá
        btnBookNow.setForeground(Color.WHITE);
        btnBookNow.setFocusPainted(false);
        btnBookNow.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm vào panel theo thứ tự: Cart trước, Book sau
        actionButtonPanel.add(btnAddToCart);
        actionButtonPanel.add(btnBookNow);

        // Thêm panel nút bấm vào vùng SOUTH của FilmFrame
        add(actionButtonPanel, BorderLayout.SOUTH);
    }
}