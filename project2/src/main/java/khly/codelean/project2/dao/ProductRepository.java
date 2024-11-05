package khly.codelean.project2.dao;

import khly.codelean.project2.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);
    Page<Product> findAllByOrderByPriceAsc(Pageable pageable); // Giá từ thấp đến cao
    Page<Product> findAllByOrderByPriceDesc(Pageable pageable); // Giá từ cao đến thấp

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
