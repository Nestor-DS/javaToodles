package com.ns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.ns"})
public class LauncherApplication {
    public static void main(String[] args) {
        SpringApplication.run(LauncherApplication.class, args);
    }
}
