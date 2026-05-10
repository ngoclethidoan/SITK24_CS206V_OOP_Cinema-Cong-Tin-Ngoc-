// src/interfaces/IPaymentService.java
package interfaces;
import model.*;
import java.util.List;

public interface IPaymentService {
    double calcTotal(List<CartItem> tickets, List<SnackCartItem> snacks);
    void processPayment(User user, List<CartItem> tickets, List<SnackCartItem> snacks);
}