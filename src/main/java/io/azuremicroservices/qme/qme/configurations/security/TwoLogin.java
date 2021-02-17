package io.azuremicroservices.qme.qme.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class TwoLogin {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public static class Admin extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;

        public Admin(UserDetailsService userDetailsServiceImpl) {
            userDetailsService = userDetailsServiceImpl;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/OperateQueue/**")
                    .authorizeRequests()
                    .antMatchers("/OperateQueue/**").hasAuthority("BRANCH_OPERATOR")
                    .antMatchers("/login-admin").permitAll()
                    .and()
                    .formLogin();
//                    .loginPage("/login-admin")
//                    .defaultSuccessUrl("/login/success")
//                    .failureUrl("/login/error")
//                    .and()
//                    .logout()
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/login-admin");
        }
    }

    @Configuration
    public static class User extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;

        public User(UserDetailsService userDetailsServiceImpl) {
            userDetailsService = userDetailsServiceImpl;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/home/**")
                    .authorizeRequests()
                    .antMatchers("/home/**").hasAuthority("CLIENT")
                    .antMatchers("/login").permitAll()
                    .and()
                    .formLogin();
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/login/success")
//                    .failureUrl("/login/error")
//                    .and()
//                    .logout()
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/login");
        }
    }
}
