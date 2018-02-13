package de.top100golfcourses.panel.component;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import de.top100golfcourses.panel.MainPage;
import de.top100golfcourses.panel.entity.Role;

public final class UserMenu extends CustomComponent {

    public UserMenu(ClientConnector clientConnector) {
        VerticalLayout layout = new VerticalLayout();
        MenuBar userMenu = new MenuBar();
        userMenu.setAutoOpen(true);
        userMenu.setDescription(VaadinSession.getCurrent().getAttribute(Role.class).toString());
        MenuItem user = userMenu.addItem(VaadinSession.getCurrent().getAttribute("user").toString(), null, null);
        user.addItem("Logout", null, (MenuItem selectedItem) -> {
            clientConnector.getUI().getNavigator().removeView(MainPage.NAME);
            VaadinSession.getCurrent().setAttribute("user", null);
            Page.getCurrent().setUriFragment("");
        });

        // The composition root MUST be set
        layout.addComponents(userMenu);
        setCompositionRoot(layout);

        // this is not needed for a Composite
        setSizeUndefined();
    }

}
