package khly.codelean.project2.service;

import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

        private final OrderRepository orderRepository;

        public OrderService(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        public List<Order> getOrdersByCustomer(Customer customer) {
            return orderRepository.findByCustomer(customer);
        }
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    }

