package model;

import java.util.ArrayList;
import java.util.List;

/**
 * SnackCartItem – giỏ hàng cho bắp/nước (độc lập với vé phim).
 * Một SnackCartItem chứa nhiều Item (bắp + nước) cùng số lượng.
 */
public class SnackCartItem {

    private final List<Item> items;   // danh sách bắp/nước đã chọn
    private boolean selected = false; // dùng cho checkbox thanh toán

    public SnackCartItem(List<Item> items) {
        this.items = new ArrayList<>(items);
    }

    public List<Item> getItems()      { return items; }
    public boolean isSelected()       { return selected; }
    public void setSelected(boolean v){ this.selected = v; }

    /** Tổng tiền của đơn bắp/nước này */
    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    /** Tổng số món */
    public int getTotalQty() {
        return items.stream().mapToInt(Item::getQuantity).sum();
    }

    /** Tóm tắt ngắn để hiển thị trong cart */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        for (Item i : items) {
            if (i.getQuantity() > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(i.getName()).append(" x").append(i.getQuantity());
            }
        }
        return sb.toString();
    }
}