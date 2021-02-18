package io.azuremicroservices.qme.qme.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsServiceImpl) {
        userDetailsService = userDetailsServiceImpl;
    }    
    
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(APP_ADMIN_URLS).hasAuthority("APP_ADMIN")
                .antMatchers(VENDOR_ADMIN_URLS).hasAnyAuthority("APP_ADMIN", "VENDOR_ADMIN")
                .antMatchers(BRANCH_ADMIN_URLS).hasAnyAuthority("APP_ADMIN", "VENDOR_ADMIN", "BRANCH_ADMIN")
                .antMatchers(BRANCH_OPERATOR_URLS).hasAnyAuthority("APP_ADMIN", "VENDOR_ADMIN", "BRANCH_ADMIN", "BRANCH_OPERATOR")
                .antMatchers(CLIENT_URLS).hasAnyAuthority("CLIENT")
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/login/success")
                .failureUrl("/login/error")
            .and()
                .logout()
                .logoutUrl("/logout")
            .and()
                .sessionManagement()
                .maximumSessions(-1)
                .expiredUrl("/login/expired")
                .sessionRegistry(sessionRegistry());
    }

    private final String[] APP_ADMIN_URLS = {
    		"/simulator/**",
            "/manage/vendor/**",
            "/manage/user-account/**",
            "/manage/vendor-admin-account/**",
            "/manage/app-admin-account/**",
            "/manage/support-ticket/**",
    };

    private final String[] VENDOR_ADMIN_URLS = {
            "/manage/branch/**",
    		"/manage/branch-admin-account/**",
    };

    private final String[] BRANCH_ADMIN_URLS = {
    		"/dashboard",
    		"/manage/queue/**",
    		"/manage/branch-operator-account/**",
    		"/manage/counter/**",
    };

    private final String[] BRANCH_OPERATOR_URLS = {
            "/operate-queue/**"
    };

    private final String[] CLIENT_URLS = {
            "/home",
            "/search/**",
            "/branch/**",
            "/join-queue",
            "/leave-queue",
            "/rejoin-queue",
            "/my-queues",
            "/my-tickets",
            "/create-ticket",
    };

    private final String[] PUBLIC_URLS = {
            "/**"
    };
}
