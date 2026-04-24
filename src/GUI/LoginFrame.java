package GUI;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JDialog {

    private boolean isProcessing = false;

    public LoginFrame(MainFrame mainFrame) {

        setTitle("Login");
        setSize(350, 260);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setLayout(null); // 🔥 để làm animation shake dễ hơn
        getContentPane().setBackground(new Color(50, 50, 50));

        // ===== TITLE =====
        JLabel title = new JLabel("LOGIN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe", Font.BOLD, 20));
        title.setBounds(140, 10, 100, 30);
        add(title);

        // ===== USER =====
        JLabel userLabel = new JLabel("User:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(40, 60, 80, 25);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(120, 60, 160, 25);
        add(userField);

        // 🔥 FIX: luôn reset field khi mở dialog
        userField.setText("");

        // ===== PASS =====
        JLabel passLabel = new JLabel("Pass:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(40, 100, 80, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(120, 100, 160, 25);
        add(passField);

        // 🔥 FIX: reset password
        passField.setText("");

        // ===== SHOW PASSWORD CHECK =====
        JCheckBox showPass = new JCheckBox("Show");
        showPass.setBounds(120, 125, 100, 20);
        showPass.setForeground(Color.WHITE);
        showPass.setOpaque(false);
        add(showPass);

        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('*');
            }
        });

        // ===== LOGIN BUTTON =====
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        loginBtn.setBounds(120, 160, 100, 30);
        add(loginBtn);

        // ======================
        // 🎯 ENTER FLOW
        // ======================

        userField.addActionListener(e -> passField.requestFocus());

        passField.addActionListener(e ->
                doLogin(mainFrame, userField, passField)
        );

        loginBtn.addActionListener(e ->
                doLogin(mainFrame, userField, passField)
        );

        // 🎯 AUTO FOCUS
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                userField.requestFocus();
            }
        });

        setVisible(true);
    }

    // ======================
    // 🔥 LOGIN LOGIC
    // ======================
    private void doLogin(MainFrame mainFrame,
                         JTextField userField,
                         JPasswordField passField) {

        if (isProcessing) return; // 🔥 chặn double event

        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.equals("Admin") && pass.equals("123")) {

            isProcessing = true;

            mainFrame.setLoggedIn(true);
            mainFrame.refreshUI();

            // 🔥 QUAN TRỌNG: dispose đúng thread, tránh reopen bug
            SwingUtilities.invokeLater(() -> {
                dispose();
            });

        } else {
            shake();
        }
    }

    // ======================
    // 🎬 SHAKE ANIMATION
    // ======================
    private void shake() {

        Point original = getLocation();

        Timer timer = new Timer(20, null);
        final int[] count = {0};

        timer.addActionListener(e -> {

            int xOffset = (count[0] % 2 == 0) ? 10 : -10;
            setLocation(original.x + xOffset, original.y);

            count[0]++;

            if (count[0] > 10) {
                timer.stop();
                setLocation(original);
            }
        });

        timer.start();
    }

    // ======================
    // 🎨 STYLE BUTTON
    // ======================
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}