/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Administrator
 */
import model.*;
public class UserService {
    
    // Upgrade to VIP
    public void upgradeVIP(User user) {
        user.setVIP(true);
    }
    
    // Change user name
    public void changeName(User user, String newName) {
        user.setName(newName);
    }
    
    // Change user password
    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
    }
}
