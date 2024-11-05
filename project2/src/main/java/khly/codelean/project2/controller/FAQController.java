package khly.codelean.project2.controller;


import khly.codelean.project2.dao.FAQRepository;
import khly.codelean.project2.entity.FAQ;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/faq")
public class FAQController {

    private final FAQRepository faqRepository;

    public FAQController(FAQRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @GetMapping("/list")
    public String listFAQ(Model model) {
        model.addAttribute("faqs", faqRepository.findAll());
        return "admin/Faq/list";
    }

    @GetMapping("/add")
    public String newFAQ(Model model) {
        model.addAttribute("faq", new FAQ());
        return "admin/Faq/form";
    }

    @GetMapping("/edit/{id}")
    public String editFAQ(@PathVariable("id") Long id, Model model) {
        model.addAttribute("faq", faqRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid FAQ Id:" + id)));
        return "admin/Faq/form";
    }

    @PostMapping("/save")
    public String saveFAQ(@ModelAttribute FAQ faq) {
        faqRepository.save(faq);
        return "redirect:/faq/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFAQ(@PathVariable("id") Long id) {
        faqRepository.deleteById(id);
        return "redirect:/faq/list";
    }
}
