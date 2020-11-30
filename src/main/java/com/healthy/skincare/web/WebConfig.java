package com.healthy.skincare.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // tag::customLoginViewController[]
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("main");
        registry.addViewController("/abc").setViewName("main"); // to vhyba nie powinno tu być
        registry.addViewController("/login");
    }
    // end::customLoginViewController[]

}
