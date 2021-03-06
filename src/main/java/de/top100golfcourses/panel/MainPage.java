package de.top100golfcourses.panel;

import java.util.List;
import java.util.Optional;
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
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

import org.vaadin.chatbox.SharedChat;

import de.top100golfcourses.panel.component.ConfirmDeleteDialog;
import de.top100golfcourses.panel.component.CreateRankingDialog;
import de.top100golfcourses.panel.component.MultiCommand;
import de.top100golfcourses.panel.component.PersistentChatBox;
import de.top100golfcourses.panel.component.RankingGrid;
import de.top100golfcourses.panel.component.RenameRankingDialog;
import de.top100golfcourses.panel.component.UserMenu;
import de.top100golfcourses.panel.da.NitritePersistence;
import de.top100golfcourses.panel.da.Persistence;
import de.top100golfcourses.panel.da.RankingsExporter;
import de.top100golfcourses.panel.entity.Rankings;
import de.top100golfcourses.panel.entity.Role;
import de.top100golfcourses.panel.entity.TopList;

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
    private Button renameRankingButton = null;
    private Button deleteRankingButton = null;
    private Button exportRankingButton = null;
    private UserMenu userMenu = null;

    private final String user;
    private final Role role;

    public static final String NAME = "Main";

    // A static variable so that everybody gets the same instance.
    private static final SharedChat sharedChat = new SharedChat();
    private final PersistentChatBox chatBox;

    public MainPage() {
        user = VaadinSession.getCurrent().getAttribute("user").toString();
        role = VaadinSession.getCurrent().getAttribute(Role.class);
        selectableRankings = storage.findAllSortByUser(user);
        chatBox = new PersistentChatBox(sharedChat, user);
        chatBox.addChangeSizeListener(() -> {
            Optional<Rankings> selectedItem = comboBox.getSelectedItem();
            Rankings selectedRankings = null;
            if (selectedItem.isPresent()) {
                selectedRankings = selectedItem.get();
            }
            initComponents();
            if (selectedRankings != null) comboBox.setSelectedItem(selectedRankings);
        });

        initComponents();
    }

    private void initComponents() {
        header.removeAllComponents();
        body.removeAllComponents();
        footer.removeAllComponents();

        // Header
        header.setWidth("100%");
        Label title = new Label("Top 100 Golf Courses - German Panel");
        title.addStyleName(ValoTheme.LABEL_H1);
        header.addComponents(title);
        if (chatBox.isSmall()) {
            header.addComponent(chatBox);
            header.setExpandRatio(chatBox, 1.0f); // Expand
        }
        this.userMenu = new UserMenu(selectableRankings);
        attachAggregationCommandListeners(userMenu.getAggregationMenuItems());
        header.addComponent(userMenu);

        // Body
        body.setSizeFull();
        if (!chatBox.isSmall()) {
            body.addComponent(chatBox);
            header.setExpandRatio(title, 1.0f); // Expand
        }
        installComboBox();

        body.addComponent(rankingGrid);

        // Footer
        if ((role == Role.Correspondent || role == Role.Panelist) && !user.equals(TopList.USERNAME)) {
            installCreateRankingButton(!chatBox.isSmall());
        }
        installExportRankingButton();
        if (role == Role.Correspondent && !user.equals(TopList.USERNAME)) {
            installRenameRankingButton();
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
        comboBox.setItemCaptionGenerator(Rankings::getId); // the function to produce the strings shown in the combo box for each item
        comboBox.addValueChangeListener((ValueChangeEvent<Rankings> event) -> {
            if (event.getValue() != null) {
                rankingGrid.setRankings(event.getValue());
                if (rankingGrid.isEditable()) {
                    createSaveRankingButton();
                    footer.addComponent(saveRankingButton, 0);
                }
                else if (saveRankingButton != null) {
                    footer.removeComponent(saveRankingButton);
                }
                if (renameRankingButton != null && !renameRankingButton.isVisible()) renameRankingButton.setVisible(true);
                if (deleteRankingButton != null && !deleteRankingButton.isVisible()) deleteRankingButton.setVisible(true);
                if (exportRankingButton != null && !exportRankingButton.isVisible()) exportRankingButton.setVisible(true);
            }
            // else: combobox entry was deselected, nothing more to do
        });
        body.addComponent(comboBox);
    }

    private void createSaveRankingButton() {
        if (saveRankingButton == null) {
            saveRankingButton = new Button("Save Ranking");
            saveRankingButton.addClickListener((ClickEvent event) -> {
                storage.save(rankingGrid.getRankings());
                Logger.getAnonymousLogger().info("Saved Ranking with " + rankingGrid.getRankings().getRankedCourses().size() + " Courses.");
            });
            saveRankingButton.setIcon(VaadinIcons.CHECK_CIRCLE_O);
            saveRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        }
    }

    private void installCreateRankingButton(boolean spaceBelowNeeded) {
        createRankingButton = new Button("Create new Ranking");
        createRankingButton.addClickListener((ClickEvent createRankingButtonClicked) -> {
            CreateRankingDialog dialog = new CreateRankingDialog(selectableRankings);
            dialog.getOkButton().addClickListener((ClickEvent okButtonClicked) -> {
                this.comboBox.setItems(selectableRankings);
                this.comboBox.setSelectedItem(dialog.getNewRankings());
            });
            UI.getCurrent().addWindow(dialog);
        });
        createRankingButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        createRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        if (spaceBelowNeeded) createRankingButton.addStyleName("SpaceBelowButtons");
        footer.addComponent(createRankingButton);
    }

    private void installRenameRankingButton() {
        renameRankingButton = new Button("Rename Ranking");
        renameRankingButton.addClickListener((ClickEvent) -> {
            Rankings rankingsToRename = rankingGrid.getRankings();
            RenameRankingDialog dialog = new RenameRankingDialog(storage, selectableRankings, rankingsToRename);
            dialog.addCloseListener((CloseEvent event) -> {
                comboBox.setItems(selectableRankings);
                comboBox.setValue(rankingsToRename);
            });
            UI.getCurrent().addWindow(dialog);
        });
        renameRankingButton.setIcon(VaadinIcons.PENCIL);
        renameRankingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        renameRankingButton.setVisible(false);
        footer.addComponent(renameRankingButton);
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
            exportRankingButton.setVisible(false);
            footer.addComponent(exportRankingButton);
        }
    }

    private void attachAggregationCommandListeners(List<MenuItem> aggregationMenuItems) {
        aggregationMenuItems.forEach((menuItem) -> {
            Command updateRankingGridCommand = (MenuItem selectedItem) -> {
                rankingGrid.setRankings(userMenu.getTopList().toRankings());
                rankingGrid.getGrid().removeColumn(RankingGrid.BUCKET_COL_ID);
                rankingGrid.getGrid().removeColumn(RankingGrid.PLAYED_COL_ID);
                rankingGrid.getGrid().getColumns().stream().forEach(item -> item.setSortable(false));
            };
            Command updateComboBoxCommand = (MenuItem selectedItem) -> {
                comboBox.setSelectedItem(null); // deselect combobox item
            };
            Command multiCommand = new MultiCommand(menuItem.getCommand(), updateRankingGridCommand, updateComboBoxCommand);
            menuItem.setCommand(multiCommand);
        });
    }

}
