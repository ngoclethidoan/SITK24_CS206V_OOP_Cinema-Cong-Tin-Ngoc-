package GUI;

import static GUI.LanguageManager.REGISTER_DISPLAY_NAME;
import database.UserDatabase;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static GUI.LanguageManager.t;

public class RegisterFrame extends JDialog {

    public RegisterFrame(MainFrame mainFrame) {

        super(mainFrame, t(LanguageManager.LOGIN_CREATE_ACCT), true);

        setSize(380, 320);
        setLocationRelativeTo(mainFrame);
        setLayout(null);
        getContentPane().setBackground(new Color(45, 45, 45));

        Font font = new Font("SansSerif", Font.PLAIN, 13);

        // ── TITLE ─────────────────────────────
        JLabel title = new JLabel(t(LanguageManager.LOGIN_CREATE_ACCT));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBounds(90, 14, 220, 28);
        add(title);

        // ── USER ID ───────────────────────────
        JLabel idLbl = new JLabel(t(LanguageManager.REGISTER_USER_ID));
        idLbl.setForeground(Color.LIGHT_GRAY);
        idLbl.setBounds(30, 58, 120, 24);
        idLbl.setFont(font);
        add(idLbl);

        JTextField idField = field(160, 58);
        add(idField);

        // ── DISPLAY NAME ──────────────────────
        JLabel nameLbl = new JLabel(t(LanguageManager.REGISTER_DISPLAY_NAME));
        nameLbl.setForeground(Color.LIGHT_GRAY);
        nameLbl.setBounds(30, 96, 120, 24);
        nameLbl.setFont(font);
        add(nameLbl);

        JTextField nameField = field(160, 96);
        add(nameField);

        // ── PASSWORD ──────────────────────────
        JLabel passLbl = new JLabel(t(LanguageManager.LOGIN_PASSWORD));
        passLbl.setForeground(Color.LIGHT_GRAY);
        passLbl.setBounds(30, 134, 120, 24);
        passLbl.setFont(font);
        add(passLbl);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(160, 134, 185, 26);
        styleField(passField);
        add(passField);

        // ── STATUS ────────────────────────────
        JLabel statusLbl = new JLabel("");
        statusLbl.setBounds(20, 170, 340, 18);
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLbl);

        // ── BUTTONS ───────────────────────────
        JButton createBtn = new JButton(t(LanguageManager.BTN_REGISTER));
        createBtn.setBounds(60, 200, 120, 34);
        styleBtn(createBtn, new Color(46, 204, 113));
        add(createBtn);

        JButton cancelBtn = new JButton(t(LanguageManager.BTN_CANCEL));
        cancelBtn.setBounds(200, 200, 110, 34);
        styleBtn(cancelBtn, new Color(80, 80, 80));
        add(cancelBtn);

        // ── EVENTS ────────────────────────────
        cancelBtn.addActionListener(e -> dispose());

        createBtn.addActionListener(e -> {

            String userId = idField.getText().trim();
            String name = nameField.getText().trim();
            String pass = new String(passField.getPassword());

            // ❌ EMPTY CHECK (i18n)
            if (userId.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                statusLbl.setText(t(LanguageManager.REGISTER_EMPTY));
                statusLbl.setForeground(Color.RED);
                return;
            }

            // ❌ EXIST CHECK (i18n)
            if (UserDatabase.userIdExists(userId)) {
                statusLbl.setText(t(LanguageManager.REGISTER_USER_EXISTS));
                statusLbl.setForeground(Color.RED);
                return;
            }

            // ✅ CREATE USER
            User u = new User(name, userId, pass, new ArrayList<>());
            UserDatabase.addUser(u);
            UserDatabase.save();

            // ✅ SUCCESS (i18n)
            statusLbl.setText(t(LanguageManager.MSG_SUCCESS));
            statusLbl.setForeground(Color.GREEN);

            Timer timer = new Timer(1200, ev -> dispose());
            timer.setRepeats(false);
            timer.start();
        });

        setVisible(true);
    }

    // ── FIELD STYLE ────────────────────────
    private JTextField field(int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 185, 26);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(65, 65, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90)));
    }

    private void styleBtn(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }
}