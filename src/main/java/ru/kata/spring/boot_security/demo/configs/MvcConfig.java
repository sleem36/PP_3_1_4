package ru.kata.spring.boot_security.demo.configs;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedHashMap;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/admin").setViewName("index-all");
    }

}
