package khly.codelean.project2.entity;

import java.math.BigDecimal;

public class CartItem {
    private Product product;
    private int quantity;

    // Constructor, getters, setters, and other methods...

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

}
