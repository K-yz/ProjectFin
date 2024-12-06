package khly.codelean.project2.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "sizes")
    private List<Product> products;

    private BigDecimal additionalPrice;


    public Size() {}

    public Size(String name, BigDecimal additionalPrice) {
        this.name = name;
        this.additionalPrice = additionalPrice;
    }

    public Size(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}
