package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CategoryRepository;
import khly.codelean.project2.entity.Category;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/list")
    public String listCategory(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/Category/list";
    }

    @GetMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/Category/form";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + id)));
        // send over to our form
        return "admin/Category/form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryRepository.save(category);

        // use a redirect to prevent duplicate submissions
        return "redirect:/category/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/category/list";

    }
}
