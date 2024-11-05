package khly.codelean.project2.controller;

import jakarta.servlet.http.HttpSession;
import khly.codelean.project2.cart.ShoppingCart;
import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.dao.ProductRepository;
import khly.codelean.project2.entity.*;
import khly.codelean.project2.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public CartController(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }


    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable("productId") Long productId, HttpSession session, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }


        // Lấy thông tin Authentication để kiểm tra quyền hạn
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + authentication.getAuthorities());


        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            return "redirect:/error/404";
        }

        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        cart.addItem(new CartItem(product, 1));
        return "redirect:/cart/view";
    }


    @RequestMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            // Tính tổng giá của giỏ hàng dựa trên từng sản phẩm và số lượng
            BigDecimal total = cart.getItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("total", total);
        } else {
            model.addAttribute("cart", new ShoppingCart());
            model.addAttribute("total", BigDecimal.ZERO); // Khi giỏ hàng trống
        }
        return "cart"; // Trả về view "cart"
    }


    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId, @RequestBody Map<String, Integer> payload, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            int newQuantity = payload.get("quantity");
            cart.updateQuantity(productId, newQuantity);
            return "redirect:/cart/view"; // Chuyển hướng trở lại trang giỏ hàng
        }
        return "error/error";
    }



    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(productId); // Xóa sản phẩm khỏi giỏ hàng
        }
        return "redirect:/cart/view"; // Chuyển hướng về trang giỏ hàng
    }
}

