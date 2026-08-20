package com.himaloyit.buildnation.ui.security;

import com.himaloyit.buildnation.ui.view.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final BackendLogoutHandler backendLogoutHandler;

    public SecurityConfig(BackendLogoutHandler backendLogoutHandler) {
        this.backendLogoutHandler = backendLogoutHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Actuator health/info must stay reachable without a session — Docker healthchecks
        // and Prometheus scraping hit these directly, matching every other service in this repo.
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
        http.logout(logout -> logout.addLogoutHandler(backendLogoutHandler));
    }
}
