package khly.codelean.project2.cart;

import khly.codelean.project2.entity.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        for (CartItem existingItem : items) {
            if (existingItem.getProduct().getProductid().equals(item.getProduct().getProductid())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getProductid().equals(productId));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateQuantity(Long productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getProductid().equals(productId)) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    removeItem(productId); // Nếu số lượng bằng 0, xóa sản phẩm khỏi giỏ
                }
                break;
            }
        }
    }


    public void clear() {
        items.clear();
    }



}
