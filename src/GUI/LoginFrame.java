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
import java.awt.event.*;
public class LoginFrame extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit, btnFogetPassword;
    
    public LoginFrame() {
        // 1. Initialize the frame
        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // on middle of the frame
        setResizable(true); // allow to change the frame size

        // 2. Use GridBagLayout to calibration input boxes
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // The distance of boxes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- First row: Username ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("User name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtUsername = new JTextField(15);
        add(txtUsername, gbc);

        // --- Second row: Password ---
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtPassword = new JPasswordField(15);
        add(txtPassword, gbc);

        // --- Third row: Panel contain buttons ---
        JPanel pnlButtons = new JPanel();
        btnLogin = new JButton("Login");
        btnExit = new JButton("Exit");
        
        pnlButtons.add(btnLogin);
        pnlButtons.add(btnExit);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2; // occupy 2 column
        add(pnlButtons, gbc);

        // 3. Handle event
        handleEvents();
    }
    
    private void handleEvents() {
        // Press exit button
        btnExit.addActionListener(e -> System.exit(0));

        // Press login button
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword()); // The way to get pass from JPasswordField

                // Logic preliminary check (can call UserService after)
                if (user.equals("Ngoc") && pass.equals("111")) {
                    JOptionPane.showMessageDialog(null, "Login successfully!");
                    
                    // Get the last size and position of login frame
                    Rectangle currentBounds = getBounds(); 
                    
                    dispose(); // Close login frame
                    
                    // Open main frame
                    // Inittialize MainFrame with Dimension from LoginFrame
                    MainFrame mainFrame = new MainFrame(currentBounds.getSize());
                    
                    // Position the MainFrame so that it matches the LoginFrame
                    mainFrame.setLocation(currentBounds.getLocation());
                    
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong user name or password!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}