package com.actvn.cinema.config;

import com.actvn.cinema.service.impl.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;

    private static final String[] PERMITTED_URLS = {
            "/","/verify*",
            "/user/login*", "/user/register*",
            "/movie/details*", "/movie/list*", "/move/search-advantage*", "/movie/filter-movie*",
            "/branch/get-by-city*",
            "/schedule/loadData*",
    };

    private static final String[] AUTHENTICATION_URLS = {
            "/ticket/plan*",
            "/payment/**",
            "/seat/**",
            "/ticket/**"
    };

    private static final String[] ROLE_ADMIN_MANAGER_URLS = {
            "/user/search**", "/user/lock**", "/user/unlock**",
            "/movie**", "/branch/**", "/schedule/**", "/dashboard/**",
            "/manager/search**", "/room**", "/seat/**", "/ticket/**"
    };

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().disable();
        http.authorizeRequests()
                .antMatchers(PERMITTED_URLS).permitAll()
                .antMatchers(AUTHENTICATION_URLS).authenticated()
                .antMatchers(ROLE_ADMIN_MANAGER_URLS).hasAnyRole("ADMIN","MANAGER")
                .antMatchers("/manager/**").hasAnyRole("ADMIN")
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout").permitAll();
    }
}
