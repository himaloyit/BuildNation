package com.himaloyit.buildnation.ui.view.login;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | BuildNation")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginOverlay login = new LoginOverlay();

    public LoginView() {
        login.setTitle("BuildNation");
        //login.setDescription("Political Party Management System");
        login.setDescription("Constituency Development Management System");
        login.setAction("login");
        login.setOpened(true);
        login.setForgotPasswordButtonVisible(false);
        add(login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        login.setError(event.getLocation().getQueryParameters()
                .getParameters().containsKey("error"));
    }
}
