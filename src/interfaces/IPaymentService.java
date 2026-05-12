package interfaces;

import model.*;
import java.util.List;

public interface IPaymentService {
    double calcTotal(List<CartItem> tickets, List<SnackCartItem> snacks);
    void processPayment(User user, List<CartItem> tickets, 
                        List<SnackCartItem> snacks, boolean fromCart); 
    List<String[]> getPaymentMethods(); 
    boolean isValidPaymentMethod(String code);
}

