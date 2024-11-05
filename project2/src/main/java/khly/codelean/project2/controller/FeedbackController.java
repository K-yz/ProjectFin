package khly.codelean.project2.controller;


import khly.codelean.project2.dao.FeedbackRepository;
import khly.codelean.project2.entity.Feedback;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/list")
    public String listFeedback(Model model) {
        model.addAttribute("feedbacks", feedbackRepository.findAll());
        return "admin/Feedback/list";
    }

    @GetMapping("/add")
    public String newFeedback(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "admin/Feedback/form";
    }

    /*@GetMapping("/edit/{id}")
    public String editFeedback(@PathVariable("id") Long id, Model model) {
        model.addAttribute("feedback", feedbackRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid feedback Id:" + id)));
        return "admin/Feedback/form";
    }*/

    @PostMapping("/save")
    public String saveFeedback(@ModelAttribute Feedback feedback) {
        feedbackRepository.save(feedback);
        return "redirect:/feedback/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable("id") Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/feedback/list";
    }
}
