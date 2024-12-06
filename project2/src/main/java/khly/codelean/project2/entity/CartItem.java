package khly.codelean.project2.entity;

import java.math.BigDecimal;

public class CartItem {
    private Product product;
    private int quantity;
    private Size size;
    private BigDecimal finalPrice;



    public CartItem(Product product, Size size, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.finalPrice = product.getPrice().add(size.getAdditionalPrice());
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

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public BigDecimal getTotalPrice() {
        return finalPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
