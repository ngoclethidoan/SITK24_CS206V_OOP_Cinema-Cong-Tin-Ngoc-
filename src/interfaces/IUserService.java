// src/interfaces/IUserService.java
package interfaces;
import model.User;
import java.util.List;
public interface IUserService {
    User login(String userId, String password);
    boolean register(String userId, String name, String password);
    void upgradeVIP(User user);
    void changeName(User user, String newName);
    void changePassword(User user, String newPassword);
    
}