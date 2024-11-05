package khly.codelean.project2.service;

import khly.codelean.project2.dao.UserRepository;
import khly.codelean.project2.entity.UserDTO;
import khly.codelean.project2.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

    @Service
    @Transactional // Thêm @Transactional nếu cần thiết
    public class UserService {

        @Autowired
        private UserRepository userRepository;

        // Tìm người dùng theo email
        public User findByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        // Lưu thông tin người dùng

        public void save(User user) {
            userRepository.save(user);  // Đảm bảo repository đang lưu đúng thông tin

        }

        // Kiểm tra mật khẩu hiện tại (nếu cần)
        public boolean checkCurrentPassword(UserDTO user, String currentPassword, PasswordEncoder passwordEncoder) {
            return passwordEncoder.matches(currentPassword, user.getPassword());
        }


    }

