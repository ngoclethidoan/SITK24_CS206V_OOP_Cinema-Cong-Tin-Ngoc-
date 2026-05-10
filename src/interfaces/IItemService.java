// src/interfaces/IItemService.java
package interfaces;
import model.Item;

public interface IItemService {
    void select(Item item, int quantity);
    void cancel(Item item, int quantity);
}