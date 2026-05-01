package GUI;

import database.UserDatabase;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RegisterFrame extends JDialog {

    public RegisterFrame(MainFrame mainFrame) {
        super(mainFrame, "Create Account", true);
        setSize(380, 320);
        setLocationRelativeTo(mainFrame);
        setLayout(null);
        getContentPane().setBackground(new Color(45, 45, 45));

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        title.setBounds(90, 14, 220, 28);
        add(title);

        JLabel idLbl = new JLabel("User ID (login):");
        idLbl.setForeground(new Color(200,200,200));
        idLbl.setBounds(30, 58, 120, 24);
        idLbl.setFont(new Font("Dialog", Font.PLAIN, 13));
        add(idLbl);
        JTextField idField = field(160, 58);  add(idField);

        JLabel nameLbl = new JLabel("Display Name:");
        nameLbl.setForeground(new Color(200,200,200));
        nameLbl.setBounds(30, 96, 120, 24);
        nameLbl.setFont(new Font("Dialog", Font.PLAIN, 13));
        add(nameLbl);
        JTextField nameField = field(160, 96);  add(nameField);

        JLabel passLbl = new JLabel("Password:");
        passLbl.setForeground(new Color(200,200,200));
        passLbl.setBounds(30, 134, 120, 24);
        passLbl.setFont(new Font("Dialog", Font.PLAIN, 13));
        add(passLbl);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(160, 134, 185, 26);
        styleField(passField);
        add(passField);

        JLabel statusLbl = new JLabel("");
        statusLbl.setBounds(20, 170, 340, 18);
        statusLbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLbl);

        JButton createBtn = new JButton("Create");
        createBtn.setBounds(60, 200, 120, 34);
        createBtn.setBackground(new Color(46, 204, 113));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Dialog", Font.BOLD, 13));
        createBtn.setFocusPainted(false);
        add(createBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(200, 200, 110, 34);
        cancelBtn.setBackground(new Color(80,80,80));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Dialog", Font.PLAIN, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn);

        createBtn.addActionListener(e -> {
            String userId = idField.getText().trim();
            String name   = nameField.getText().trim();
            String pass   = new String(passField.getPassword());

            if (userId.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                statusLbl.setText("Please fill in all fields.");
                statusLbl.setForeground(new Color(255,100,100)); return;
            }
            if (UserDatabase.userIdExists(userId)) {
                statusLbl.setText("User ID already taken.");
                statusLbl.setForeground(new Color(255,100,100)); return;
            }
            User newUser = new User(name, userId, pass, new ArrayList<>());
            UserDatabase.addUser(newUser);   // ← saves to file
            statusLbl.setText("Account created! You can now log in.");
            statusLbl.setForeground(new Color(100,220,100));
            Timer t = new Timer(1200, ev -> dispose()); t.setRepeats(false); t.start();
        });

        setVisible(true);
    }

    private JTextField field(int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 185, 26);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(65,65,65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90,90,90)),
            BorderFactory.createEmptyBorder(2,6,2,6)));
    }
}