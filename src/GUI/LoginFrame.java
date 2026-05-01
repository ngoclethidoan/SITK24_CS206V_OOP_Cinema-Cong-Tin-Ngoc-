package GUI;

import database.UserDatabase;
import model.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JDialog {

    private boolean isProcessing = false;

    public LoginFrame(MainFrame mainFrame) {
        setTitle("Login");
        setSize(370, 310);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setLayout(null);
        getContentPane().setBackground(new Color(50, 50, 50));

        // ── TITLE ─────────────────────────────────────────────────────
        JLabel title = new JLabel("LOGIN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setBounds(148, 12, 100, 30);
        add(title);

        // ── USERNAME ──────────────────────────────────────────────────
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(new Color(200, 200, 200));
        userLabel.setBounds(35, 62, 85, 25);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(125, 62, 195, 27);
        styleField(userField);
        add(userField);

        // ── PASSWORD ──────────────────────────────────────────────────
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(new Color(200, 200, 200));
        passLabel.setBounds(35, 103, 85, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(125, 103, 195, 27);
        styleField(passField);
        add(passField);

        // ── SHOW PASSWORD ─────────────────────────────────────────────
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setBounds(125, 136, 150, 20);
        showPass.setForeground(new Color(180, 180, 180));
        showPass.setOpaque(false);
        showPass.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(showPass);
        showPass.addActionListener(e ->
                passField.setEchoChar(showPass.isSelected() ? (char) 0 : '*'));

        // ── STATUS LABEL ──────────────────────────────────────────────
        JLabel lblStatus = new JLabel("");
        lblStatus.setBounds(25, 163, 310, 18);
        lblStatus.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblStatus);

        // ── LOGIN BUTTON ──────────────────────────────────────────────
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(45, 192, 120, 34);
        styleButton(loginBtn, new Color(52, 152, 219));
        add(loginBtn);

        // ── REGISTER BUTTON ───────────────────────────────────────────
        JButton registerBtn = new JButton("Create Account");
        registerBtn.setBounds(185, 192, 145, 34);
        styleButton(registerBtn, new Color(70, 70, 70));
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame(mainFrame);
        });

        // ── ENTER KEY FLOW ────────────────────────────────────────────
        userField.addActionListener(e -> passField.requestFocus());
        passField.addActionListener(e -> doLogin(mainFrame, userField, passField, lblStatus));
        loginBtn.addActionListener(e -> doLogin(mainFrame, userField, passField, lblStatus));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                userField.requestFocus();
            }
        });

        setVisible(true);
    }

    // ── Login logic ───────────────────────────────────────────────────
    private void doLogin(MainFrame mainFrame, JTextField userField,
                         JPasswordField passField, JLabel lblStatus) {
        if (isProcessing) return;

        String userId = userField.getText().trim();
        String pass   = new String(passField.getPassword());

        if (userId.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Please enter username and password.");
            lblStatus.setForeground(new Color(255, 120, 120));
            return;
        }

        User user = UserDatabase.login(userId, pass);
        if (user != null) {
            isProcessing = true;
            mainFrame.setLoggedIn(true, user);
            SwingUtilities.invokeLater(this::dispose);
        } else {
            lblStatus.setText("Incorrect username or password.");
            lblStatus.setForeground(new Color(255, 100, 100));
            shake();
        }
    }

    // ── Shake animation ───────────────────────────────────────────────
    private void shake() {
        Point original = getLocation();
        Timer timer = new Timer(20, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            setLocation(original.x + (count[0] % 2 == 0 ? 10 : -10), original.y);
            if (++count[0] > 10) { timer.stop(); setLocation(original); }
        });
        timer.start();
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(65, 65, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
}