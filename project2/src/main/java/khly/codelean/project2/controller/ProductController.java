package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CategoryRepository;
import khly.codelean.project2.dao.ProductRepository;
import khly.codelean.project2.dao.SizeRepository;
import khly.codelean.project2.entity.Category;
import khly.codelean.project2.entity.Product;
import khly.codelean.project2.entity.Size;
import khly.codelean.project2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;


    private final ProductService productService ;

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository, SizeRepository sizeRepository , ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sizeRepository = sizeRepository;
        this.productService = productService;

    }

    /*@GetMapping("/list")
    public String listProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (keyword.isEmpty()) {
            products = productService.getAllProducts(pageable);
        } else {
            products = productService.searchProducts(keyword, pageable);
        }
        model.addAttribute("products", productRepository.findAll());

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/Product/list";
    }*/

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        // Kiểm tra nếu có categoryId
        if (categoryId != null) {
            products = productRepository.findByCategory_Categoryid(categoryId, pageable);
        } else if (!keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        // Đưa dữ liệu vào model
        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        // Lấy danh sách tất cả các category để hiển thị trong form lọc
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "admin/Product/list";
    }


    /*@GetMapping("/list")
    public String listProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, // 10 sản phẩm mỗi trang
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        // Sử dụng phương thức search có sẵn trong ProductRepository
        if (keyword.isEmpty()) {
            products = productRepository.findAll(pageable); // Không có từ khóa -> lấy tất cả
        } else {
            products = productRepository.findByNameContainingIgnoreCase(keyword, pageable); // Có từ khóa -> tìm kiếm
        }

        // Đưa thông tin vào model để truyền sang view
        model.addAttribute("products", products.getContent()); // Danh sách sản phẩm của trang hiện tại
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", products.getTotalPages()); // Tổng số trang
        model.addAttribute("keyword", keyword); // Từ khóa tìm kiếm (nếu có)

        return "admin/Product/list";
    }*/


    @GetMapping("/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "admin/Product/form";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product Id:" + id)));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "admin/Product/form";
    }


    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product theProduct,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam(value = "selectedSizes", required = false) List<Long> selectedSizes,
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

        /*// Lưu các size đã chọn cho sản phẩm
        if (selectedSizes != null) {
            for (Long sizeId : selectedSizes) {
                Size size = sizeRepository.findById(sizeId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Size Id:" + sizeId));
                theProduct.getSizes().add(size); // Gán size cho sản phẩm
            }
        }*/

        // Lưu kích thước (size)
        theProduct.setSizes(new ArrayList<>(theProduct.getSizes()));

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
