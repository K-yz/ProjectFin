package khly.codelean.project2.controller;

import jakarta.servlet.http.HttpSession;
import khly.codelean.project2.cart.ShoppingCart;
import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.dao.ProductRepository;
import khly.codelean.project2.dao.SizeRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepository;

    private final SizeRepository sizeRepository;

    private final CustomerRepository customerRepository;


    @Autowired
    private OrderRepository orderRepository;

    public CartController(ProductRepository productRepository, SizeRepository sizeRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
        this.customerRepository = customerRepository;
    }


    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable("productId") Long productId,
                            @RequestParam Long size,
                            HttpSession session,
                            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + authentication.getAuthorities());


        Product product = productRepository.findById(productId).orElse(null);
        Size selectedSize  = sizeRepository.findById(size).orElse(null);

        if (product == null || selectedSize == null) {
            return "redirect:/error/404";
        }


        BigDecimal finalPrice = product.getPrice().add(
                selectedSize.getAdditionalPrice() != null ? selectedSize.getAdditionalPrice() : BigDecimal.ZERO
        );
        System.out.println("Product Price: " + product.getPrice());
        System.out.println("Size Additional Price: " + selectedSize.getAdditionalPrice());
        System.out.println("Final Price for Cart Item: " + finalPrice);


        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        cart.addItem(new CartItem(product, selectedSize, 1));

        return "redirect:/cart/view";
    }


    /*@RequestMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            BigDecimal total = cart.getItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("total", total);
        } else {
            model.addAttribute("cart", new ShoppingCart());
            model.addAttribute("total", BigDecimal.ZERO); // Khi giỏ hàng trống
        }
        return "cart";
    }*/

    @RequestMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        if (cart != null) {
            BigDecimal total = cart.getItems().stream()
                    .map(item -> {
                        BigDecimal productPrice = item.getProduct().getPrice();
                        BigDecimal sizePrice = item.getSize() != null ? item.getSize().getAdditionalPrice() : BigDecimal.ZERO;
                        return productPrice.add(sizePrice).multiply(BigDecimal.valueOf(item.getQuantity()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
        } else {
            model.addAttribute("cart", new ShoppingCart());
            model.addAttribute("total", BigDecimal.ZERO);
        }

        return "cart";
    }



    @PostMapping("/update/{productId}/{sizeId}")
    public String updateQuantity(
            @PathVariable Long productId,
            @PathVariable Long sizeId,
            @RequestBody Map<String, Integer> payload,
            HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            int newQuantity = payload.get("quantity");
            cart.updateQuantity(productId, sizeId, newQuantity);
            return "redirect:/cart/view";
        }
        return "error/error";
    }


    @PostMapping("/remove/{productId}/{sizeId}")
    public String removeFromCart(@PathVariable Long productId, @PathVariable Long sizeId, HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(productId, sizeId);
        }
        return "redirect:/cart/view";
    }


    @ModelAttribute("totalItems")
    public int getTotalItems(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        return cart != null ? cart.getTotalItems() : 0;
    }

    // Lấy tổng giá trị giỏ hàng
    @ModelAttribute("totalPrice")
    public BigDecimal getTotalPrice(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        return cart != null ? cart.getTotal() : BigDecimal.ZERO;
    }

}

