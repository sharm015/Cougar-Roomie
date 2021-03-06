package com.SE370.Cougar.Roomie.configuration;
import com.SE370.Cougar.Roomie.controller.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userDetailsService; // We overrided default

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // TODO: Figure out how to fix this and enable csrf security without breaking everything
        http.cors().and().csrf().disable(); // If you do not have a csrf token on a form this will disable that security

        http.authorizeRequests()
                .antMatchers("/user/*").hasRole("USER")
                .antMatchers("/chat/*").hasRole("USER")
                .antMatchers("/*").permitAll()
                .and().formLogin().loginPage("/login")
                .defaultSuccessUrl("/user/profile", true)
                .failureUrl("/login?error=true")
                .and().logout().logoutSuccessUrl("/");
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(getPasswordEncoder());
        return authProvider;
    }

    // This method returns an instance of a BCryptPasswordEncoder which contains the tools needed
    // by Spring Sec. authProvider.setPasswordEncoder() method needs to perform the encryption.
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
