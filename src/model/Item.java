package model;

public class Item {

    private final String codeItem;     // code of items
    private final String name;         // name of items
    private final double price;  // base price
    private int quantity;              // available quantity

    public Item(String codeItem, String name, double price, int quantity) {
        this.codeItem = codeItem;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // ── Getters ─────────────────────
    public String getCodeItem() { return codeItem; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    // ── Setters ──────────────────────────────────────────────────────
    public void setQuantity(int newQuantity) {this.quantity = newQuantity;}

    @Override
    public String toString() {
        return String.format("[%s | %s | %.0f VND | qty: %d]", 
                codeItem, name, getPrice(), quantity);
    }
}
