package com.himaloyit.buildnation.sac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 * Author: Rajib Kumer Ghosh
 */

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
public class SecurityAccessControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAccessControlApplication.class, args);
    }
}
