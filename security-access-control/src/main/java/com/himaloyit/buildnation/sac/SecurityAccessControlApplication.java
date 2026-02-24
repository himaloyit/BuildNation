package com.himaloyit.buildnation.sac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/*
 * Author: Rajib Kumer Ghosh
 */

@SpringBootApplication
@EnableCaching
public class SecurityAccessControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAccessControlApplication.class, args);
    }
}
