package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderController(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/list")
    public String listOrder(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/Order/list";
    }


    @GetMapping("/details/{id}")
    public String getOrderDetails(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "admin/test/from"; // Trả về tên template cho trang chi tiết đơn hàng
    }

}
