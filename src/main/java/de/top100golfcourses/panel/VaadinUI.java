package de.top100golfcourses.panel;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("mytheme")
@PreserveOnRefresh
public final class VaadinUI extends UI {
    Navigator navigator;

    @WebServlet(value = "/*", name="Top100Panel", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = true, ui = VaadinUI.class)
    public static class Servlet extends VaadinServlet {
    }

    public static Authentication AUTH = new Authentication();

    @Override
    protected void init(VaadinRequest request) {
        Page.getCurrent().setTitle("Top 100 Golf Courses - German Panel");
        navigator = new Navigator(this, this);

        navigator.addView(LoginPage.NAME, new LoginPage());
        navigator.setErrorView(LoginPage.class);

        Page.getCurrent().addUriFragmentChangedListener((UriFragmentChangedEvent event) -> {
            router(event.getUriFragment());
        });

        router("");
    }

    private void router(String route) {
        if (getSession().getAttribute("user") != null) {
            getNavigator().addView(MainPage.NAME, MainPage.class);
            getNavigator().navigateTo(MainPage.NAME);
        }
        else {
            Page.getCurrent().setUriFragment(LoginPage.NAME);
            getNavigator().navigateTo(LoginPage.NAME);
        }
    }

    @Override
    protected void refresh(VaadinRequest request) {
        Notification.show("UI was refreshed @ " + System.currentTimeMillis());
    }

}
