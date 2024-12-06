package khly.codelean.project2.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import khly.codelean.project2.dao.CustomerRepository;
import khly.codelean.project2.dao.RoleRepository;
import khly.codelean.project2.entity.Category;
import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Role;
import khly.codelean.project2.login.User;
import khly.codelean.project2.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import net.bytebuddy.utility.RandomString;


import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@Controller
@RequestMapping("/user")
public class UserController {

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomerRepository customerRepo;


    @Autowired
    private RoleRepository roleRepo;


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }


    @PostMapping("/process_register")
    public String processRegister(User user, HttpServletRequest request, @RequestParam("phone") String phone, @RequestParam("address") String address) throws MessagingException, UnsupportedEncodingException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        // Tạo mã xác thực ngẫu nhiên
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);  // Người dùng chưa được kích hoạt



        // Kiểm tra và lấy vai trò USER
        Role userRole = roleRepo.findByName("USER");
        if (userRole == null) {
            userRole = new Role("USER");
            roleRepo.save(userRole);
        }

        // Gán vai trò USER cho người dùng mới
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Lưu người dùng vào cơ sở dữ liệu
        userRepo.save(user);

        // Gửi email xác thực
        sendVerificationEmail(user, getSiteURL(request));

        // Tạo và lưu thông tin khách hàng
        Customer customer = new Customer();
        customer.setName(user.getFirstName() + " " + user.getLastName());
        customer.setEmail(user.getEmail());
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setUser(user);

        customerRepo.save(customer);

        return "register_success"; // Trả về trang xác nhận đăng ký thành công
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "your-email@example.com";
        String senderName = "Project SEM4";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Project SEM4";


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/user/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        if (verify(code)) {
            return "verify_success";  // Hiển thị trang xác thực thành công
        } else {
            return "verify_fail";  // Hiển thị trang xác thực thất bại
        }
    }


    public boolean verify(String verificationCode) {
        User user = userRepo.findByVerificationCode(verificationCode);  // Tìm người dùng dựa trên mã xác minh

        if (user == null || user.isEnabled()) {
            return false;  // Không tìm thấy người dùng hoặc tài khoản đã được kích hoạt trước đó
        } else {
            user.setVerificationCode(null);  // Xóa mã xác minh
            user.setEnabled(true);  // Kích hoạt tài khoản
            userRepo.save(user);  // Lưu lại thay đổi vào cơ sở dữ liệu

            return true;
        }
    }



    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("users", listUsers);
        return "admin/User/list";
    }









    @GetMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("user", new Category());
        return "admin/User/form";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid User Id:" + id)));
        // send over to our form
        return "admin/User/form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userRepo.save(user);

        // use a redirect to prevent duplicate submissions
        return "redirect:/user/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepo.deleteById(id);
        return "redirect:/user/list";

    }
}
