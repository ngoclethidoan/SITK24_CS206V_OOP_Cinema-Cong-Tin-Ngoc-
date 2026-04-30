package model;

public class Item {

    private final String codeItem;
    private final String name;
    private final double price;
    private int quantity;

    public Item(String codeItem, String name, double price, int quantity) {
        this.codeItem = codeItem;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getCodeItem() { return codeItem; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void increase() {
        this.quantity++;
    }

    public void decrease() {
        if (this.quantity > 0) this.quantity--;
    }

    public void setQuantity(int newQuantity) {
        this.quantity = Math.max(0, newQuantity);
    }

    @Override
    public String toString() {
        return String.format("[%s | %s | %.0f VND | qty: %d]",
                codeItem, name, price, quantity);
    }
}