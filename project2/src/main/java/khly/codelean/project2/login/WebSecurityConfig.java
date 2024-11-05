package khly.codelean.project2.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

import static org.hibernate.cfg.JdbcSettings.USER;

@Configuration
public class WebSecurityConfig {

	@Autowired
	private DataSource dataSource;


	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				)

				.authorizeHttpRequests(authorizeRequests ->
						authorizeRequests

								.requestMatchers("/admin").hasRole("ADMIN")
								.requestMatchers("/category/**", "/product/list","/product/from", "/faq/**", "/blog/**", "/customer/**", "/feedback/**", "/order/**", "/orderdetail/**", "/post/**" ).hasRole("ADMIN")
								.requestMatchers("/checkout/**").authenticated()
								/*.requestMatchers("/cart/**").hasRole("USER")*/
								.requestMatchers("/add-to-cart", "/cart/**", "/product/**").permitAll()
								.requestMatchers("/account/update").authenticated()

								.anyRequest().permitAll()
				)

				.formLogin(formLogin ->
						formLogin
								.loginPage("/login")
								.usernameParameter("email")
								.passwordParameter("password")
								.defaultSuccessUrl("/", true)
								.failureUrl("/login?error")
								.permitAll()
				)

				.logout(logout ->
						logout
								.logoutUrl("/logout") // URL thực hiện logout
								.logoutSuccessUrl("/") // Trang chuyển hướng sau khi logout thành công
								.permitAll()
				)
				.csrf((csrf) -> csrf.disable());

		return http.build();
	}


	@Bean
	public AuthenticationConfiguration authenticationConfiguration() {
		return new AuthenticationConfiguration();
	}

}
