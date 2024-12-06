package khly.codelean.project2.controller;

import khly.codelean.project2.dao.OrderRepository;
import khly.codelean.project2.entity.Customer;
import khly.codelean.project2.entity.Order;
import khly.codelean.project2.entity.UserDTO;
import khly.codelean.project2.login.User;
import khly.codelean.project2.service.OrderService;
import khly.codelean.project2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
    public class AccountController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(OrderRepository orderRepository, OrderService orderService, UserService userService, PasswordEncoder passwordEncoder) {
        this.orderService = orderService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
    }

        @GetMapping("/my-account")
        public String showAccountPage(Model model) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName(); // Lấy email từ thông tin xác thực hiện tại
            User currentUser = userService.findByEmail(email);
            model.addAttribute("orders", orderRepository.findAll());

            if (currentUser != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setFirstName(currentUser.getFirstName());
                userDTO.setLastName(currentUser.getLastName());
                userDTO.setEmail(currentUser.getEmail());
                model.addAttribute("userDTO", userDTO);
            }

            return "my-account"; // Trả về tên trang thông tin tài khoản của bạn
        }

        @PostMapping("/account/update")
        public String updateAccountDetails(@ModelAttribute UserDTO userDTO,
                                           @RequestParam("current-pwd") String currentPassword,
                                           @RequestParam("new-pwd") String newPassword,
                                           @RequestParam("confirm-pwd") String confirmPassword,
                                           Model model) {

            // Tìm người dùng theo email
            User currentUser = userService.findByEmail(userDTO.getEmail());
            if (currentUser == null) {
                model.addAttribute("error", "Người dùng không tồn tại.");
                return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
            }

            // Cập nhật thông tin cá nhân
            currentUser.setFirstName(userDTO.getFirstName());
            currentUser.setLastName(userDTO.getLastName());
            currentUser.setEmail(userDTO.getEmail());

            // Kiểm tra và cập nhật mật khẩu nếu mật khẩu hiện tại được nhập
            if (!currentPassword.isEmpty()) {
                if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                    model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
                    return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
                }

                if (newPassword.isEmpty()) {
                    model.addAttribute("error", "Mật khẩu mới không được để trống.");
                    return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
                }

                if (!newPassword.equals(confirmPassword)) {
                    model.addAttribute("error", "Mật khẩu mới và mật khẩu xác nhận không khớp.");
                    return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
                }


                currentUser.setPassword(passwordEncoder.encode(newPassword));
            }

            try {
                userService.save(currentUser);
                model.addAttribute("success", "Thông tin tài khoản và mật khẩu đã được cập nhật thành công.");
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin.");
                e.printStackTrace();
            }

            return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
        }

    @GetMapping("/account/orders")
    public String viewOrders(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer currentUser = (Customer) authentication.getPrincipal();

        // Lấy danh sách đơn hàng của người dùng
        List<Order> orders = orderService.getOrdersByCustomer(currentUser);
        model.addAttribute("orders", orders);

        return "account/orders"; // Đảm bảo tên template này là chính xác
    }

    }

