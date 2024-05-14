/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.security;

import deu.cse.spring_webmail.model.Pop3Agent;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

/**
 *
 * @author JunHyeok
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SpringSecurityConfig {

    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;

    @Bean
    public MyUserDetailsService myUserDetailsService(DataSource dataSource) {
        UserDetails user1
                = User.withUsername("admin")
                        .password(passwordEncoder().encode("admin"))
                        .roles("ADMIN")
                        .build();

        log.info("user1 = {}", user1);
        InMemoryUserDetailsManager inMemoryManager = new InMemoryUserDetailsManager(user1);

        JdbcUserDetailsManager jdbcManager = new JdbcUserDetailsManager(dataSource);
        if (!jdbcManager.userExists("admin")) {
            UserDetails user2 = User.builder()
                    .username("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("ADMIN")
                    .build();
            
            jdbcManager.createUser(user2);
        }
        return new MyUserDetailsService(inMemoryManager, jdbcManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/js/**", "/css/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(
                request -> request
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/admin**").hasRole("ADMIN")
                        .requestMatchers("/add**").hasRole("ADMIN")
                        .requestMatchers("/delete_user").hasRole("ADMIN")
                        .requestMatchers("/Registar").permitAll()
                        .requestMatchers("/insert").permitAll()
                        .anyRequest().authenticated()
        );

        http.formLogin(formLogin -> formLogin
                .loginPage("/login").permitAll()
                .successHandler(new MySimpleUrlAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .loginProcessingUrl("/login.do")
        );

        http.httpBasic(Customizer.withDefaults());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        return http.build();
    }

    private class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {

            String host = (String) request.getSession().getAttribute("host");
            String userid = request.getParameter("username");
            String password = request.getParameter("password");
            //log.debug("id = {}", userid);
            //log.debug("passwd = {}", password);

            Pop3Agent pop3Agent = new Pop3Agent(host, userid, password);
            boolean isLoginSuccess = pop3Agent.validate();

            if (isLoginSuccess) {
                if ("admin".equals(userid)) {
                    session.setAttribute("userid", userid);
                } else {
                    session.setAttribute("userid", userid);
                    session.setAttribute("password", password);
                }
                log.info("\n\n\nMySimpleUrlAuthenticationSuccessHandler:onAuthenticationSuccess()...\n\n");

                final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                log.info("authories = {}", authorities);

                String loginURL = "/webmail/main_menu";
                for (final GrantedAuthority grantedAuthority : authorities) {
                    String authorityName = grantedAuthority.getAuthority();
                    log.debug("auth name = {}", authorityName);
                    if ("ROLE_ADMIN".equals(authorityName)) {
                        loginURL = "/webmail/admin_menu";
                        break;
                    }
                }
                response.sendRedirect(loginURL);
            }
        }

    }
}

@Service
class MyUserDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager inMemoryManager;
    private final JdbcUserDetailsManager jdbcManager;

    public MyUserDetailsService(InMemoryUserDetailsManager inMemoryManager, JdbcUserDetailsManager jdbcManager) {
        this.inMemoryManager = inMemoryManager;
        this.jdbcManager = jdbcManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.startsWith("admin")) {
            return inMemoryManager.loadUserByUsername(username);
        } else {
            return jdbcManager.loadUserByUsername(username);
        }
    }
}