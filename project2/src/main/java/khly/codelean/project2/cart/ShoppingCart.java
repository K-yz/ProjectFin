package khly.codelean.project2.cart;

import khly.codelean.project2.entity.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        for (CartItem existingItem : items) {
            if (existingItem.getProduct().getProductid().equals(item.getProduct().getProductid()) &&
                    existingItem.getSize().getId().equals(item.getSize().getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }


    public void removeItem(Long productId, Long sizeId) {
        items.removeIf(item -> item.getProduct().getProductid().equals(productId) && item.getSize().getId().equals(sizeId));
    }


    public List<CartItem> getItems() {
        return items;
    }


    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .add(item.getSize().getAdditionalPrice() != null
                                ? item.getSize().getAdditionalPrice()
                                : BigDecimal.ZERO) // Đảm bảo `additionalPrice` không bị null
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    public void updateQuantity(Long productId, Long sizeId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getProductid().equals(productId) && item.getSize().getId().equals(sizeId)) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    removeItem(productId, sizeId);
                }
                break;
            }
        }
    }



    public void clear() {
        items.clear();
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }


}
