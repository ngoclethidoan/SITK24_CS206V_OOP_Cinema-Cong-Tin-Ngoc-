/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import interfaces.IItemService;
/**
 *
 * @author Administrator
 */
import model.*;
public class ItemService implements IItemService {
    
    
    // Manage the items selected
    public void select(Item item, int quantity) {
        if (item.getQuantity() < quantity) {
            throw new IllegalStateException("The " + item.getName() + " is sold out.");
        } else {
            int newQuantity = item.getQuantity() - quantity;
            item.setQuantity(newQuantity);
        }
    }
    
    // Manage cancellation
    public void cancel(Item item, int quantity) {
        item.setQuantity(item.getQuantity() + quantity);
    }
}
