package de.top100golfcourses.panel.component;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AbstractConfirmationDialog extends Window {
    
    private boolean confirmed = false;
    private final String question;

    protected abstract void confirmed();
    protected abstract void notConfirmed();

    public AbstractConfirmationDialog(String question) {
        super();
        this.question = question;
        init();
    }
    
    private void init() {
        center();

        setSizeUndefined();
        setResizable(false);
        setClosable(false);
        setModal(true);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);

        Label label = new Label(question);
        HorizontalLayout buttons = new HorizontalLayout();

        Button yesButton = new Button("Yes");
        yesButton.addClickListener((ClickEvent event) -> {
            confirmed();
            this.confirmed = true;
            close();
        });
        yesButton.addStyleName(ValoTheme.BUTTON_DANGER);

        Button noButton = new Button("No");
        noButton.addClickListener((ClickEvent event) -> {
            notConfirmed();
            this.confirmed = false;
            close();
        });
        noButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        noButton.setClickShortcut(KeyCode.ENTER);

        buttons.addComponents(yesButton, noButton);

        content.addComponents(label, buttons);
        setContent(content);
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

}
