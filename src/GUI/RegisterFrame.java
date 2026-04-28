/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import javax.swing.*;

public class RegisterFrame extends JDialog {
    public RegisterFrame(MainFrame mainFrame) {
        setTitle("Register");
        setSize(300, 200);
        setLocationRelativeTo(mainFrame);
        setModal(true);
        setVisible(true);
    }
}
