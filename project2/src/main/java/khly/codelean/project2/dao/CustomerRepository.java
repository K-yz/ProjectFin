package khly.codelean.project2.dao;

import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.login.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Tìm khách hàng theo tên đăng nhập của User
    Customer findByUser_Email(String email);
    Customer findByUser(User user);
}