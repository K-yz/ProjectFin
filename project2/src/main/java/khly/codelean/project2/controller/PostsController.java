package khly.codelean.project2.controller;


import khly.codelean.project2.dao.PostsRepository;
import khly.codelean.project2.entity.Posts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/post")
public class PostsController {

    private final PostsRepository postsRepository;

    public PostsController(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @GetMapping("/list")
    public String listPosts(Model model) {
        model.addAttribute("posts", postsRepository.findAll());
        return "admin/Posts/list";
    }

    @GetMapping("/add")
    public String newPost(Model model) {
        model.addAttribute("post", new Posts());
        return "admin/Posts/form";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute Posts post,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        if (!imageFile.isEmpty()) {
            try {
                // Đường dẫn để lưu trữ file
                String uploadDir = "src/main/resources/static/images/";
                String fileName = imageFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);

                // Lưu file ảnh vào server
                Files.write(path, imageFile.getBytes());

                // Lưu đường dẫn của file ảnh vào product
                post.setImage("/images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error saving image: " + e.getMessage());
                return "redirect:/post/add"; // Trở về trang thêm sản phẩm với thông báo lỗi
            }
        }


        postsRepository.save(post);
        return "redirect:/post/list";
    }

    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Model model) {
        model.addAttribute("post", postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Post Id:" + id)));
        return "admin/Posts/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postsRepository.deleteById(id);
        return "redirect:/post/list";
    }

    @GetMapping("/details/{id}")
    public String showPostDetail(@PathVariable("id") Long id, Model model) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Post Id:" + id));
        model.addAttribute("post", post);
        return "blog-detail";
    }
}