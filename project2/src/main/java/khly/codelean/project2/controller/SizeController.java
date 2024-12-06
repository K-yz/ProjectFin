package khly.codelean.project2.controller;

import khly.codelean.project2.dao.CategoryRepository;
import khly.codelean.project2.dao.SizeRepository;
import khly.codelean.project2.entity.Category;
import khly.codelean.project2.entity.Size;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@Controller
@RequestMapping("/size")
public class SizeController {
    private final SizeRepository sizeRepository;

    public SizeController(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @GetMapping("/list")
    public String listSize(Model model) {
        model.addAttribute("sizes", sizeRepository.findAll());
        return "admin/Size/list";
    }

    @GetMapping("/add")
    public String addSize(Model model) {
        model.addAttribute("size", new Size());
        return "admin/Size/form";
    }

    @GetMapping("/edit/{id}")
    public String editSize(@PathVariable("id") Long id, Model model) {
        model.addAttribute("size", sizeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Size Id:" + id)));
        // send over to our form
        return "admin/Size/form";
    }

    @PostMapping("/save")
    public String saveSize(@ModelAttribute Size size) {
        sizeRepository.save(size);

        // use a redirect to prevent duplicate submissions
        return "redirect:/size/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteSize(@PathVariable("id") Long id) {
        sizeRepository.deleteById(id);
        return "redirect:/size/list";

    }
}
