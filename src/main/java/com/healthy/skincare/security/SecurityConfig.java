package com.healthy.skincare.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import  org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

//@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
   // private UserDetailsService userDetailsService;
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        //auth.userDetailsService(userDetailsService);
        auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(
                "select username, password, enabled from user where username = ?")
                .authoritiesByUsernameQuery(
                        "select username, autoryzacja from user_autoryzacja where username = ?")
        .passwordEncoder(
                new BCryptPasswordEncoder(4)); // between 4- 31

    }

    @Override
    protected void configure( HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers("/design", "/add", "/", "/user", "/productList/filtred")
                .access("hasRole('ROLE_USER')")
                .antMatchers( "/**").access("permitAll")

                .and()
                .formLogin()
                .loginPage("/login")
                //.failureUrl("/login?error=true")


                .and()
                .logout()
                .logoutSuccessUrl("/")


        ;
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(4);
    }
}
