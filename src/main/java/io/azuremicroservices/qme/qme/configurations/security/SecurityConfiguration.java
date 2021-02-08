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
                .antMatchers(PUBLIC_URLS).permitAll()
                .antMatchers("/**").permitAll() // To be removed. This line allows everyone to access any page.
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/login/success")
                .failureUrl("/login/error")
            .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/success")
            .and()
                .sessionManagement()
                .maximumSessions(-1)
                .expiredUrl("/login/expired")
                .sessionRegistry(sessionRegistry());
    }

    private final String[] APP_ADMIN_URLS = {

    };

    private final String[] VENDOR_ADMIN_URLS = {

    };

    private final String[] BRANCH_ADMIN_URLS = {

    };

    private final String[] BRANCH_OPERATOR_URLS = {
            "/BranchOperator"

    };

    private final String[] CLIENT_URLS = {

    };

    private final String[] PUBLIC_URLS = {

    };
}
