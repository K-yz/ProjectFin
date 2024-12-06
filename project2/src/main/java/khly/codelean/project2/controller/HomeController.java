package khly.codelean.project2.controller;


import jakarta.servlet.http.HttpSession;
import khly.codelean.project2.cart.ShoppingCart;
import khly.codelean.project2.dao.*;
import khly.codelean.project2.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    private final UserRepository userRepo;

    private final FeedbackRepository feedbackRepository;

    private final ProductRepository productRepository;

    private final FAQRepository faqRepository;

    private final PostsRepository postsRepository;

    private final OrderRepository orderRepository;

    @Autowired
    private SizeRepository sizeRepository;


    public HomeController(UserRepository userRepo, FeedbackRepository feedbackRepository, ProductRepository productRepository, FAQRepository faqRepository, PostsRepository postsRepository, OrderRepository orderRepository) {
        this.userRepo = userRepo;
        this.feedbackRepository = feedbackRepository;
        this.productRepository = productRepository;
        this.faqRepository = faqRepository;
        this.postsRepository = postsRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/")
    public String showHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            model.addAttribute("username", username);
        }
        return "home";
    }


    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(name = "sort", required = false) String sort,
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Product> productPage;

        if ("asc".equals(sort)) {
            productPage = productRepository.findAllByOrderByPriceAsc(pageable);
        } else if ("desc".equals(sort)) {
            productPage = productRepository.findAllByOrderByPriceDesc(pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }



        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("searchQuery", "");


        List<Size> sizes = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            sizes.addAll(product.getSizes());
        }
        model.addAttribute("sizes", sizes);


        return "shop-list-no-sidebar";
    }


    @GetMapping("/Faq")
    public String viewFAQs(Model model) {
        model.addAttribute("faqs", faqRepository.findAll());
        return "faq";
    }

    @GetMapping("/Blog")
    public String showBlog(Model model) {
        model.addAttribute("posts", postsRepository.findAll());
        return "blog-no-sidebar";
    }

    @GetMapping("/contact")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "contact"; // Trả về trang contact để người dùng nhập feedback
    }

    @PostMapping("/contact/save")
    public String saveFeedback(@ModelAttribute("feedback") Feedback feedback, RedirectAttributes redirectAttributes) {
        // Xử lý lưu feedback vào database hoặc dịch vụ
        feedbackRepository.save(feedback);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Feedback sent successfully!");
        return "feedback-confirm";
    }




    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("sizes", product.getSizes());
        return "product-details-thumbnail-right";
    }





    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
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
