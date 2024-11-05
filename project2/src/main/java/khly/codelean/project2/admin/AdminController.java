package khly.codelean.project2.admin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class AdminController {

    @GetMapping("/admin")
    public String admin() {
        return "/admin/index";
    }

    @GetMapping("/category")
    public String category() {
        return "/admin/category";
    }
}



