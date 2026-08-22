package com.himaloyit.buildnation.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("buildnation")
public class BuildNationUiApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(BuildNationUiApplication.class, args);
    }
}
