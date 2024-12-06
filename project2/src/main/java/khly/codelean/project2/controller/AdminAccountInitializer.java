package khly.codelean.project2.controller;

import khly.codelean.project2.dao.*;
import khly.codelean.project2.entity.PaymentMethod;
import khly.codelean.project2.entity.Role;
import khly.codelean.project2.entity.ShippingMethod;

import khly.codelean.project2.entity.Size;
import khly.codelean.project2.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public void run(String... args) throws Exception {
        User adminUser = userRepository.findByEmail("admin@gmail.com");

        if (adminUser == null) {
            adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);

            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                adminRole = new Role("ADMIN");
                roleRepository.save(adminRole);
            }

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            System.out.println("Admin account created: admin@gmail.com");
        } else {
            System.out.println("Admin account already exists.");
        }

        if (paymentMethodRepository.count() == 0) {
            paymentMethodRepository.save(new PaymentMethod("Cash on Delivery"));
            /*paymentMethodRepository.save(new PaymentMethod("PayPal"));
            paymentMethodRepository.save(new PaymentMethod("Bank Transfer"));*/
            System.out.println("Payment methods initialized.");
        }

        if (shippingMethodRepository.count() == 0) {
            shippingMethodRepository.save(new ShippingMethod("Order & Pick-Up"));
            shippingMethodRepository.save(new ShippingMethod("Delivers"));
            /*shippingMethodRepository.save(new ShippingMethod("Next Day Shipping"));*/
            System.out.println("Shipping methods initialized.");
        }

        /*if (sizeRepository.count() == 0) {
            sizeRepository.save(new Size("s"));
            sizeRepository.save(new Size("m"));
            sizeRepository.save(new Size("l"));
            sizeRepository.save(new Size("xl"));
            System.out.println("Size initialized.");
        }*/

        /*if (sizeRepository.count() == 0) {
            sizeRepository.save(new Size("s", BigDecimal.valueOf(0)));
            sizeRepository.save(new Size("m", BigDecimal.valueOf(5000)));
            sizeRepository.save(new Size("l", BigDecimal.valueOf(10000)));
            sizeRepository.save(new Size("xl", BigDecimal.valueOf(15000)));
            System.out.println("Sizes initialized with additional prices.");
        }*/
    }

}
