/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import database.UserDatabase;
import interfaces.IUserService;
import java.util.ArrayList;
/**
 *
 * @author Administrator
 */
import model.*;
public class UserService implements IUserService {
    
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
    
    public User login(String userId, String password) {
    return UserDatabase.login(userId, password);
}

public boolean register(String userId, String name, String password) {
    if (UserDatabase.userIdExists(userId)) return false;
    User u = new User(name, userId, password, new ArrayList<>());
    UserDatabase.addUser(u);
    UserDatabase.save();
    return true;
}
}
