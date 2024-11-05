package khly.codelean.project2.dao;

import khly.codelean.project2.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
}
