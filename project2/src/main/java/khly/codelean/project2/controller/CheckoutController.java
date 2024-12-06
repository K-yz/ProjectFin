package khly.codelean.project2.controller;


import jakarta.servlet.http.HttpSession;
import khly.codelean.project2.cart.ShoppingCart;
import khly.codelean.project2.dao.*;
import khly.codelean.project2.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private final ProductRepository productRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;


    public CheckoutController(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @RequestMapping("/payment")
    public String showCheckoutPage(HttpSession session, Model model, Principal principal) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            return "redirect:/cart/view";
        }

        if (principal == null) {
            return "redirect:/login";
        }

        String userEmail = principal.getName();
        Customer customer = customerRepository.findByUser_Email(userEmail);

        if (customer == null) {
            return "redirect:/login";
        }

        BigDecimal total = cart.getTotal();

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        List<ShippingMethod> shippingMethods = shippingMethodRepository.findAll();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("customer", customer);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("shippingMethods", shippingMethods);

        return "checkout";
    }



    @PostMapping("/cart-checkout")
    public String submitOrder(HttpSession session, Principal principal,
                              @RequestParam("address") String address,
                              @RequestParam("phone") String phone,
                              @RequestParam("paymentMethodId") Long paymentMethodId,
                              @RequestParam("shippingMethod") Long shippingMethodId,
                              Model model) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng trống.");
            return "checkout";  // Quay lại trang checkout nếu giỏ hàng trống
        }

        // Lấy thông tin người dùng hiện tại từ Principal
        if (principal == null) {
            return "redirect:/login";
        }
        String userEmail = principal.getName();
        Customer customer = customerRepository.findByUser_Email(userEmail);
        if (customer == null) {
            return "redirect:/login";
        }

        // Cập nhật địa chỉ và số điện thoại
        customer.setAddress(address);
        customer.setPhone(phone);
        customerRepository.save(customer);

        // Tạo đơn hàng và lưu vào cơ sở dữ liệu
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDate.now());
        order.setPrice(cart.getTotal());
        order.setStatus("Pending");

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Invalid Payment Method"));
        order.setPaymentMethod(paymentMethod);


        ShippingMethod shippingMethod = shippingMethodRepository.findById(shippingMethodId)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));
        order.setShippingMethod(shippingMethod);

        // Gộp sản phẩm vào đơn hàng
        for (CartItem item : cart.getItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setSize(item.getSize());
            order.setProduct(item.getProduct());
            orderDetail.setOrder(order);

            order.getOrderDetails().add(orderDetail);
        }

        orderRepository.save(order);

        session.removeAttribute("cart");

        return "redirect:/checkout/order-confirmation" ;
    }



    @GetMapping("/order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";
    }



    /*@RequestMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, Model model) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra và thay đổi trạng thái
        if ("Pending".equals(order.getStatus())) {
            order.setStatus("Confirm Order");
        } else if ("Confirm Order".equals(order.getStatus())) {
            order.setStatus("Pending");
        }

        // Lưu lại thay đổi vào cơ sở dữ liệu
        orderRepository.save(order);

        // Chuyển hướng lại trang danh sách đơn hàng sau khi cập nhật
        return "redirect:/order/list";  // Trang danh sách đơn hàng của quản trị viên
    }*/

    @RequestMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, Model model) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra và thay đổi trạng thái
        switch (order.getStatus()) {
            case "Pending":
                order.setStatus("Confirm Order");
                break;
            case "Confirm Order":
                order.setStatus("Preparing");  // Thêm trạng thái "Preparing"
                break;
            case "Preparing":
                order.setStatus("Shipping");  // Chuyển từ "Preparing" sang "Shipping"
                break;
            case "Shipping":
                order.setStatus("Canceling");
                break;
            case "Canceling":
                order.setStatus("Pending");
                break;
            default:
                throw new IllegalStateException("Unknown status: " + order.getStatus());
        }

        // Lưu lại thay đổi vào cơ sở dữ liệu
        orderRepository.save(order);

        // Chuyển hướng lại trang danh sách đơn hàng sau khi cập nhật
        return "redirect:/order/list";
    }

    @PostMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam("status") String status) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Cập nhật trạng thái mới
        order.setStatus(status);

        // Lưu lại thay đổi vào cơ sở dữ liệu
        orderRepository.save(order);

        // Chuyển hướng lại trang danh sách đơn hàng
        return "redirect:/order/list";
    }
}
