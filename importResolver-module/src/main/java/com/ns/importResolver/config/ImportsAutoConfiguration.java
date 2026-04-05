package com.ns.importResolver.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ns.importResolver")
public class ImportsAutoConfiguration {
    @PostConstruct
    public void init() {
        System.out.println("========================");
        System.out.println("IMPORT RESOLVER MODULE LOADED!");
        System.out.println("========================");
    }
}
