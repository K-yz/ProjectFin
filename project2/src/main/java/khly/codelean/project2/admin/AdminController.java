package khly.codelean.project2.admin;

import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.dao.ProductRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller

public class AdminController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public AdminController(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /*@GetMapping("/admin")
    public String viewDashboard(Model model) {
        long totalOrders = orderRepository.countCompletedOrders();
        model.addAttribute("totalOrders", totalOrders);


        Double totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        model.addAttribute("totalRevenue", totalRevenue);

        return "/admin/index";
    }*/

    @GetMapping("/admin")
    public String viewDashboard(Model model) {
        long totalOrders = orderRepository.countCompletedOrders();
        long totalCustomers = customerRepository.countCustomers();
        long totalProducts = productRepository.countProducts();
        long pendingOrders = orderRepository.countPendingOrders();

        Double totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }


        // Thêm dữ liệu vào model
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "/admin/index";
    }



    /*@GetMapping("/admin")
    public String admin() {
        return "/admin/index";
    }*/

    @GetMapping("/category")
    public String category() {
        return "/admin/category";
    }
}



