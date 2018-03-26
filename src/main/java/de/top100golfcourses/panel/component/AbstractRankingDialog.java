package de.top100golfcourses.panel.component;

import java.util.List;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.top100golfcourses.panel.entity.Rankings;

public abstract class AbstractRankingDialog extends Window {

    protected final List<Rankings> existingRankings;
    private final String caption;
    private final String defaultName;
    private final Button okButton;

    protected abstract void processRankingName(String user, String selectedName);

    public AbstractRankingDialog(List<Rankings> existingRankings, String caption, String defaultName) {
        super(); // Set window caption
        this.existingRankings = existingRankings;
        this.caption = caption;
        this.defaultName = defaultName;
        this.okButton = new Button("OK");
        init();
    }
    
    private void init() {
        center();

        setSizeUndefined();
        setResizable(false);
        setClosable(false);
        setModal(true);

        FormLayout content = new FormLayout();
        content.setMargin(true);

        TextField textField = new TextField(caption);
        textField.setValue(defaultName);
        textField.setWidth("300px");

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
                processRankingName(user, name);
            }
            close();
        });
        okButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        okButton.setClickShortcut(KeyCode.ENTER);

        content.addComponents(textField, okButton);
        setContent(content);
    }

    public Button getOkButton() {
        return okButton;
    }

    protected boolean checkForDuplicate(List<Rankings> rankings, String user, String name) {
        for (Rankings ranking : rankings) {
            if (ranking.getUser().equals(user) && ranking.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
