package de.top100golfcourses.panel;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public final class LoginPage extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "";

    public LoginPage() {
        Image logo = new Image(null, new ThemeResource("logo.jpg"));

        Panel panel = new Panel("<b>German Panel</b>");
        panel.setSizeUndefined();
        addComponents(logo, panel);

        FormLayout content = new FormLayout();
        TextField username = new TextField("Username");
        content.addComponent(username);
        PasswordField password = new PasswordField("Password");
        content.addComponent(password);

        Button send = new Button("Enter");
        send.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (VaadinUI.AUTH.authenticate(username.getValue(), password.getValue())) {
                    getUI().getNavigator().addView(MainPage.NAME, MainPage.class);
                    Page.getCurrent().setUriFragment(MainPage.NAME);
                }
                else {
                    Notification.show("Invalid credentials", Notification.Type.ERROR_MESSAGE);
                }
            }

        });
        send.setClickShortcut(KeyCode.ENTER);
        send.addStyleName(ValoTheme.BUTTON_PRIMARY);

        content.addComponent(send);
        content.setSizeUndefined();
        content.setMargin(true);
        panel.setContent(content);
        setComponentAlignment(logo, Alignment.TOP_CENTER);
        setComponentAlignment(panel, Alignment.TOP_CENTER);
    }

}
