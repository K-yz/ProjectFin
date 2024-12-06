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
    public String listOrder(@RequestParam(value = "status", required = false) String status, Model model) {
        if (status != null && !status.isEmpty()) {
            // Lọc đơn hàng theo trạng thái
            model.addAttribute("orders", orderRepository.findByStatus(status));
        } else {
            // Hiển thị tất cả đơn hàng
            model.addAttribute("orders", orderRepository.findAll());
        }
        model.addAttribute("currentStatus", status); // Để giữ trạng thái đã chọn trên giao diện
        return "admin/Order/list";
    }


    @GetMapping("/details/{id}")
    public String getOrderDetails(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "admin/test/from";
    }


    @GetMapping("/detail/{id}")
    public String getOrderDetail(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "user-cart";
    }

}
