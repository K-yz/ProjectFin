package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.entity.Category;
import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerController(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/list")
    public String listCustomer(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/Customer/list";
    }


    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerRepository.save(customer);
        return "redirect:/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customer/list";

    }


    /*@GetMapping("/my-orders")
    public String viewUserOrders(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        Customer customer = customerRepository.findByUser_Email(userEmail);
        if (customer == null) {
            throw new RuntimeException("Không tìm thấy khách hàng");
        }

        List<Order> orders = orderRepository.findByCustomer(customer);
        model.addAttribute("orders", orders);

        return "admin/Customer/order-list";
    }*/

    @GetMapping("/user/{userId}/orders")
    public String getUserOrders(@PathVariable Long userId, Model model) {
        Customer customer = customerRepository.findById(userId).orElse(null);
        if (customer != null) {
            List<Order> orders = orderRepository.findByCustomer(customer);
            model.addAttribute("customer", customer);
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("error", "Customer not found.");
        }
        return "admin/Customer/order-list";
    }


}
