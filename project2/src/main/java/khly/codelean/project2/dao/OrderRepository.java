package khly.codelean.project2.dao;


import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);
}
