package com.ns.wsdl.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ns.wsdl")
public class WsdlAutoConfiguration {

    @PostConstruct
    public void init() {
        System.out.println("========================");
        System.out.println("WSDL MODULE LOADED!");
        System.out.println("========================");
    }
}