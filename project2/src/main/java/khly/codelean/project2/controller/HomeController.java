package khly.codelean.project2.controller;


import khly.codelean.project2.dao.*;
import khly.codelean.project2.entity.Feedback;
import khly.codelean.project2.entity.Order;
import khly.codelean.project2.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    private final UserRepository userRepo;

    private final FeedbackRepository feedbackRepository;

    private final ProductRepository productRepository;

    private final FAQRepository faqRepository;

    private final PostsRepository postsRepository;

    private final OrderRepository orderRepository;

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

    /*@GetMapping("/products")
    public String showProduct(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "shop-list-no-sidebar";
    }*/
    /*@GetMapping("/products")
    public String listProducts(Model model, @RequestParam(name = "sort", required = false) String sort) {

        List<Product> products;

        if ("asc".equals(sort)) {
            products = productRepository.findAllByOrderByPriceAsc();
        } else if ("desc".equals(sort)) {
            products = productRepository.findAllByOrderByPriceDesc();
        } else {
            products = productRepository.findAll();
        }

        model.addAttribute("products", products);
        return "shop-list-no-sidebar";
    }*/

    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(name = "sort", required = false) String sort,
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
        Page<Product> productPage;

        // Kiểm tra tham số 'sort' để sắp xếp danh sách sản phẩm
        if ("asc".equals(sort)) {
            productPage = productRepository.findAllByOrderByPriceAsc(pageable);
        } else if ("desc".equals(sort)) {
            productPage = productRepository.findAllByOrderByPriceDesc(pageable);
        } else {
            productPage = productRepository.findAll(pageable); // Trả về trang sản phẩm không sắp xếp
        }

        // Lấy danh sách sản phẩm từ Page<Product> bằng getContent()
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("searchQuery", "");


        return "shop-list-no-sidebar"; // Trả về tên template cho view
    }


    @GetMapping("/faq")
    public String viewFAQs(Model model) {
        model.addAttribute("faqs", faqRepository.findAll());
        return "faq";
    }

    @GetMapping("/blog")
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


    @GetMapping("/product-detail")
    public String listProduct(Model model) {
        model.addAttribute("products", productRepository.findAll());
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





}
