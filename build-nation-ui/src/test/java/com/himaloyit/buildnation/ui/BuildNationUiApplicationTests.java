package com.himaloyit.buildnation.ui;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "eureka.client.enabled=false")
class BuildNationUiApplicationTests {

    @Test
    void contextLoads() {
    }
}
