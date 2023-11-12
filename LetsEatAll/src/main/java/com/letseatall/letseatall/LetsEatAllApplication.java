package com.letseatall.letseatall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class LetsEatAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetsEatAllApplication.class, args);
    }

}
