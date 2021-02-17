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
            http.requestMatchers().antMatchers(ADMIN_URLS)
                    .and()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .formLogin().permitAll();
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
            http.requestMatchers().antMatchers(CLIENT_URLS)
                    .and()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .formLogin().permitAll();
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/login/success")
//                    .failureUrl("/login/error")
//                    .and()
//                    .logout()
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/login");
        }
    }

    private static final String[] ADMIN_URLS = {
            "/manage/vendor/**",
            "/manage/vendor-admin-account/**",
            "/manage/app-admin-account/**",
            "/manage/branch/**",
            "/manage/branch-admin-account/**",
            "/dashboard",
            "/manage/queue/**",
            "/manage/branch-operator-account/**",
            "/manage/counter/**",
            "/OperateQueue/**"
    };

    private static final String[] APP_ADMIN_URLS = {
            "/manage/vendor/**",
            "/manage/vendor-admin-account/**",
            "/manage/app-admin-account/**",
    };

    private static final String[] VENDOR_ADMIN_URLS = {
            "/manage/branch/**",
    		"/manage/branch-admin-account/**",
    };

    private static final String[] BRANCH_ADMIN_URLS = {
    		"/dashboard",
    		"/manage/queue/**",
    		"/manage/branch-operator-account/**",
    		"/manage/counter/**",
    };

    private static final String[] BRANCH_OPERATOR_URLS = {
    		"/OperateQueue/**",
    };

    private static final String[] CLIENT_URLS = {
            "/home",
            "/search/**",
            "/branch/**",
            "/join-queue",
            "/leave-queue",
            "/rejoin-queue",
            "/my-queues",
            "/support-ticket/**",
    };
}
