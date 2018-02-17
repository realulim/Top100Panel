package de.top100golfcourses.panel;

import java.util.List;
import java.util.logging.Logger;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

import de.top100golfcourses.panel.component.ConfirmDeleteDialog;
import de.top100golfcourses.panel.component.CreateRankingDialog;
import de.top100golfcourses.panel.component.RankingGrid;
import de.top100golfcourses.panel.component.UserMenu;
import de.top100golfcourses.panel.da.NitritePersistence;
import de.top100golfcourses.panel.da.Persistence;
import de.top100golfcourses.panel.da.RankingsExporter;
import de.top100golfcourses.panel.entity.Rankings;
import de.top100golfcourses.panel.entity.Role;

public final class MainPage extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    private final Persistence storage = new NitritePersistence();

    private final HorizontalLayout header = new HorizontalLayout();
    private final VerticalLayout body = new VerticalLayout();
    private final HorizontalLayout footer = new HorizontalLayout();

    private final RankingGrid rankingGrid = new RankingGrid();
    private final ComboBox<Rankings> comboBox = new ComboBox<>("Select a Ranking");
    private List<Rankings> selectableRankings = null;
    private Button saveRankingButton = null;
    private Button createRankingButton = null;
    private Button deleteRankingButton = null;
    private Button exportRankingButton = null;
    private final String user;

    public static final String NAME = "Main";

    public MainPage() {
        user = VaadinSession.getCurrent().getAttribute("user").toString();
        Role role = VaadinSession.getCurrent().getAttribute(Role.class);
        selectableRankings = storage.findAllSortByUser(user);

        header.setWidth("100%");
        Label title = new Label("Top 100 Golf Courses - German Panel");
        title.addStyleName(ValoTheme.LABEL_H1);
        header.addComponents(title);
        header.setExpandRatio(title, 1.0f); // Expand

        // Header
        UserMenu logoutComponent = new UserMenu(this);
        header.addComponent(logoutComponent);

        // Body
        body.setSizeFull();
        installComboBox();
        body.addComponent(rankingGrid);

        // Footer
        if (role == Role.Correspondent || role == Role.Panelist) {
            installCreateRankingButton();
        }
        if (role == Role.Correspondent) {
            installDeleteRankingButton();
        }

        addComponent(header);
        addComponent(body);
        addComponent(footer);
    }

    private void installComboBox() {
        comboBox.setWidth("350px");
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setItems(selectableRankings);
        comboBox.setItemCaptionGenerator(Rankings::getId);
        comboBox.addValueChangeListener((ValueChangeEvent<Rankings> event) -> {
            rankingGrid.setRankings(event.getValue());
            if (rankingGrid.isEditable()) {
                createSaveRankingButton();
                footer.addComponent(saveRankingButton, 0);
            }
            else if (saveRankingButton != null) {
                footer.removeComponent(saveRankingButton);
            }
            if (deleteRankingButton != null && !deleteRankingButton.isVisible()) deleteRankingButton.setVisible(true);
            installExportRankingButton();
        });
        body.addComponent(comboBox);
    }

    private void createSaveRankingButton() {
        if (saveRankingButton == null) {
            saveRankingButton = new Button("Save Ranking");
            saveRankingButton.addClickListener((ClickEvent event) -> {
                storage.save(rankingGrid.getRankings());
                Logger.getAnonymousLogger().info(rankingGrid.getRankings().getRankedCourses().toString());
            });
            saveRankingButton.setIcon(VaadinIcons.CHECK_CIRCLE_O);
            saveRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        }
    }

    private void installCreateRankingButton() {
        createRankingButton = new Button("Create new Ranking");
        createRankingButton.addClickListener((ClickEvent event) -> {
            CreateRankingDialog dialog = new CreateRankingDialog(selectableRankings, comboBox);
            UI.getCurrent().addWindow(dialog);
        });
        createRankingButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        createRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        footer.addComponent(createRankingButton);
    }

    private void installDeleteRankingButton() {
        deleteRankingButton = new Button("Delete Ranking");
        deleteRankingButton.addClickListener((ClickEvent) -> {
            Rankings rankingsToDelete = rankingGrid.getRankings();
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(storage, rankingsToDelete);
            dialog.addCloseListener((CloseEvent event) -> {
                if (((ConfirmDeleteDialog)event.getWindow()).isConfirmed()) {
                    selectableRankings.remove(rankingsToDelete);
                    comboBox.setItems(selectableRankings);
                    if (selectableRankings.size() > 0) {
                        comboBox.setValue(selectableRankings.get(0));
                    }
                }
            });
            UI.getCurrent().addWindow(dialog);
        });
        deleteRankingButton.setIcon(VaadinIcons.MINUS_CIRCLE_O);
        deleteRankingButton.addStyleName(ValoTheme.BUTTON_DANGER);
        deleteRankingButton.setVisible(false);
        footer.addComponent(deleteRankingButton);
    }

    private void installExportRankingButton() {
        if (exportRankingButton == null) {
            exportRankingButton = new Button("Export Ranking");
            exportRankingButton.addClickListener((ClickEvent event) -> {
                new RankingsExporter(rankingGrid.getGrid(), rankingGrid.getRankings().getId()).export();
            });
            exportRankingButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            exportRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
            footer.addComponent(exportRankingButton);
        }
    }

}
