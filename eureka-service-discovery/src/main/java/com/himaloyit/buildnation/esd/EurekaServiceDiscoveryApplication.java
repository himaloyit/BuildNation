package com.himaloyit.buildnation.esd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Rajib Kumer Ghosh
 *
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceDiscoveryApplication.class, args);
    }
}
