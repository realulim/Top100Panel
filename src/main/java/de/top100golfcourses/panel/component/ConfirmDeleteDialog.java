package de.top100golfcourses.panel.component;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.top100golfcourses.panel.da.Persistence;
import de.top100golfcourses.panel.entity.Rankings;

public final class ConfirmDeleteDialog extends Window {

    private boolean confirmed = false;

    public ConfirmDeleteDialog(Persistence storage, Rankings rankingToDelete) {
        super();
        center();

        setSizeUndefined();
        setResizable(false);
        setClosable(false);
        setModal(true);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);

        Label label = new Label("Are you sure you want to delete this Ranking?");
        HorizontalLayout buttons = new HorizontalLayout();

        Button yesButton = new Button("Yes");
        yesButton.addClickListener((ClickEvent event) -> {
            storage.delete(rankingToDelete);
            this.confirmed = true;
            close();
        });
        yesButton.addStyleName(ValoTheme.BUTTON_DANGER);

        Button noButton = new Button("No");
        noButton.addClickListener((ClickEvent event) -> {
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
