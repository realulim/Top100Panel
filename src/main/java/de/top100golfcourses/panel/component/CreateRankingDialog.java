package de.top100golfcourses.panel.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.top100golfcourses.panel.entity.Rankings;

public final class CreateRankingDialog extends Window {

    public CreateRankingDialog(List<Rankings> existingRankings, ComboBox<Rankings> select) {
        super(); // Set window caption
        center();

        setSizeUndefined();
        setResizable(false);
        setClosable(false);
        setModal(true);

        FormLayout content = new FormLayout();
        content.setMargin(true);

        TextField textField = new TextField("Name of new Ranking:");
        textField.setValue("Germany");
        textField.setWidth("300px");

        Button okButton = new Button("OK");
        okButton.addClickListener((ClickEvent event) -> {
            String user = VaadinSession.getCurrent().getAttribute("user").toString();
            String name = textField.getValue();
            if (name.trim().equals("")) {
                Notification.show("Empty Name - not allowed!", Notification.Type.ERROR_MESSAGE);
            }
            else if (checkForDuplicate(existingRankings, user, name)) {
                Notification.show("Duplicate Name - Ranking already exists!", Notification.Type.ERROR_MESSAGE);
            }
            else {
                Rankings newRankings = new Rankings();
                newRankings.setRankedCourses(new ArrayList<>());
                newRankings.setUser(user);
                newRankings.setName(name);
                existingRankings.add(newRankings);
                select.setItems(existingRankings);
                select.setValue(newRankings);
            }
            close();
        });
        okButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        okButton.setClickShortcut(KeyCode.ENTER);

        content.addComponents(textField, okButton);
        setContent(content);
    }

    private boolean checkForDuplicate(List<Rankings> rankings, String user, String name) {
        for (Rankings ranking : rankings) {
            if (ranking.getUser().equals(user) && ranking.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
