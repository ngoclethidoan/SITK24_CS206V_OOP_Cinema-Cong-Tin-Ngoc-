package GUI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel currentScreen;
    private boolean isLoggedIn = false;

    // 🔥 chỉ cần flag này nếu muốn chống mở nhiều dialog cùng lúc
    private boolean loginOpened = false;

    public MainFrame() {

        // configue main window
        setTitle("Cinema App");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🎨 main layout
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        // 🎬 the first screen(temporary)
        setScreen(createHomeScreen());

        setVisible(true);
    }

    // 🔥 Change current screen
    public void setScreen(JPanel screen) {

        if (currentScreen != null) {
            remove(currentScreen);
        }

        currentScreen = screen;
        add(currentScreen, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // The first screen temporarily created in construstor
    private JPanel createHomeScreen() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(19, 19, 19));

        JLabel title = new JLabel("🎬 Cinema App", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe", Font.BOLD, 24));

        panel.add(title, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopBar() {

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(19, 19, 19));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // ===== LOGO =====
        JLabel logo = new JLabel();
        ImageIcon icon = new ImageIcon("assets/logo.png");

        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage()
                    .getScaledInstance(120, 90, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(img));
        } else {
            logo.setText("NO LOGO");
            logo.setForeground(Color.WHITE);
        }

        logo.setOpaque(false);
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(Box.createHorizontalGlue());
        left.add(logo);
        left.add(Box.createHorizontalGlue());

        // ===== SEARCH =====
        JTextField searchBar = new JTextField("🔍 Searching...");
        searchBar.setPreferredSize(new Dimension(400, 40));
        searchBar.setForeground(Color.GRAY);
        searchBar.setBackground(new Color(40, 40, 40));
        searchBar.setCaretColor(Color.WHITE);
        searchBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchBar.getText().equals("🔍 Searching...")) {
                    searchBar.setText("");
                    searchBar.setForeground(Color.WHITE);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText("🔍 Searching...");
                    searchBar.setForeground(Color.GRAY);
                }
            }
        });

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(searchBar);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(centerWrapper, BorderLayout.CENTER);
        topBar.add(createRightPanel(), BorderLayout.EAST);

        return topBar;
    }

    private JPanel createRightPanel() {

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        right.add(Box.createHorizontalGlue());

        if (!isLoggedIn) {

            JButton loginBtn = new JButton("Login");
            JButton signUpBtn = new JButton("Sign In");
            JButton settingBtn = new JButton("⚙ Settings");

            styleButton(loginBtn);
            styleButton(signUpBtn);
            styleButton(settingBtn);

            right.add(loginBtn);
            right.add(Box.createHorizontalStrut(10));
            right.add(signUpBtn);
            right.add(Box.createHorizontalStrut(10));
            right.add(settingBtn);

            // 🔥 FIX: open login safely
            loginBtn.addActionListener(e -> openLogin());

        } else {

            JButton cardBtn = new JButton("🎴 Card");
            JButton settingBtn = new JButton("⚙ Settings");
            JButton userBtn = new JButton("👤 User");

            styleButton(cardBtn);
            styleButton(settingBtn);
            styleButton(userBtn);

            right.add(cardBtn);
            right.add(Box.createHorizontalStrut(10));
            right.add(settingBtn);
            right.add(Box.createHorizontalStrut(10));
            right.add(userBtn);
        }

        right.add(Box.createHorizontalGlue());

        return right;
    }

    //CHUẨN LOGIN OPEN FLOW
    public void openLogin() {

        if (loginOpened) return;

        loginOpened = true;

        new LoginFrame(this); // modal → block tại đây

        loginOpened = false; // 🔥 chạy sau khi dialog đóng
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setLoggedIn(boolean value) {
        this.isLoggedIn = value;
    }

    public void refreshUI() {

        SwingUtilities.invokeLater(() -> {

            getContentPane().removeAll();
            setLayout(new BorderLayout());

            add(createTopBar(), BorderLayout.NORTH);
            add(currentScreen, BorderLayout.CENTER);

            revalidate();
            repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}