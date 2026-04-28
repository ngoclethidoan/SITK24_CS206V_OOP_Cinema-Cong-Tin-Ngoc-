/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import model.User;
import java.util.*;

public class UserDatabase {

    private static List<User> users = new ArrayList<>();

    static {
        users.add(new User("Admin", "admin", "123", new ArrayList<>()));
    }

    public static User login(String userId, String password) {
        for (User u : users) {
            if (u.getUserId().equals(userId) &&
                u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}
