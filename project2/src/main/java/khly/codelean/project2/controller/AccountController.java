package khly.codelean.project2.controller;

import khly.codelean.project2.entity.UserDTO;
import khly.codelean.project2.login.User;
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

@Controller
    public class AccountController {

        @Autowired
        private UserService userService;

        @Autowired
        private PasswordEncoder passwordEncoder;
        @GetMapping("/my-account")
        public String showAccountPage(Model model) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName(); // Lấy email từ thông tin xác thực hiện tại
            User currentUser = userService.findByEmail(email);

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

                // Cập nhật mật khẩu mới (mã hóa trước khi lưu)
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            }
// Lưu cập nhật thông tin người dùng
            try {
                userService.save(currentUser);
                model.addAttribute("success", "Thông tin tài khoản và mật khẩu đã được cập nhật thành công.");
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin.");
                e.printStackTrace();
            }

            return "redirect:/my-account"; // Redirect về trang thông tin tài khoản
        }

    }

