package khly.codelean.project2.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productid;

    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "price", nullable = false)
    private BigDecimal   price;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Transient
    private int quantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_size",
            joinColumns = @JoinColumn(name = "productid"),
            inverseJoinColumns = @JoinColumn(name = "sizeid")
    )
    private List<Size> sizes;




    public Long getProductid() {
        return productid;
    }

    public void setProductid(Long productid) {
        this.productid = productid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }
}
