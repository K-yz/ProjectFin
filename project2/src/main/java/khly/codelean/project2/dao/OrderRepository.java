package khly.codelean.project2.dao;


import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'Confirm Order'")
    long countCompletedOrders();

    @Query("SELECT SUM(o.Price) FROM Order o WHERE o.status = 'Confirm Order'")
    Double calculateTotalRevenue();



    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'Pending'")
    long countPendingOrders();

    @Query("SELECT p, SUM(od.quantity) FROM OrderDetail od JOIN od.product p GROUP BY p ORDER BY SUM(od.quantity) DESC")
    List<Object[]> getTopSellingProducts();

    List<Order> findByStatus(String status);



}
