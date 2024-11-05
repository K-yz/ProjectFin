package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CategoryRepository;
import khly.codelean.project2.dao.ProductRepository;
import khly.codelean.project2.entity.Category;
import khly.codelean.project2.entity.Product;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @GetMapping("/list")
    public String listProduct(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/Product/list";
    }

    @GetMapping("/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll()); // Thêm danh sách category
        return "admin/Product/form";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Product Id:" + id)));
        return "admin/Product/form";
    }


    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product theProduct,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        if (theProduct.getCategory() == null || theProduct.getCategory().getCategoryid() == null) {
            redirectAttributes.addFlashAttribute("message", "Category must be selected.");
            return "redirect:/product/add";
        }

        Category category = categoryRepository.findById(theProduct.getCategory().getCategoryid())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + theProduct.getCategory().getCategoryid()));
        theProduct.setCategory(category);

        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/images/";
                String fileName = imageFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);

                Files.write(path, imageFile.getBytes());

                theProduct.setImage("/images/" + fileName);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving image", e);
                redirectAttributes.addFlashAttribute("message", "Error saving image: " + e.getMessage());
                return "redirect:/product/add";
            }
        }

        productRepository.save(theProduct);

        return "redirect:/product/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/product/list";

    }

    @GetMapping("/details/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product Id:" + id));
        model.addAttribute("product", product);
        return "product-details-thumbnail-right";
    }

    /*@GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(query, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("searchQuery", query);

        return "shop-list-no-sidebar"; // Đảm bảo rằng tên template là chính xác
    }*/

    @GetMapping("/search")
    public String searchProducts(@RequestParam(name = "query", required = false) String query,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang

        Page<Product> productPage;

        // Kiểm tra xem query có rỗng hay không
        if (query == null || query.trim().isEmpty()) {
            // Nếu rỗng, lấy tất cả sản phẩm
            productPage = productRepository.findAll(pageable);
        } else {
            // Nếu không rỗng, tìm kiếm sản phẩm theo tên
            productPage = productRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        // Lưu trữ thông tin vào model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("searchQuery", query != null ? query : ""); // Đặt giá trị query vào model

        return "shop-list-no-sidebar"; // Đảm bảo rằng tên template là chính xác
    }


}
