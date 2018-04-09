package de.top100golfcourses.panel.component;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.contextmenu.MenuItem;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.renderers.LocalDateRenderer;

import org.vaadin.addons.autocomplete.AutocompleteExtension;

import de.top100golfcourses.panel.da.Suggestions;
import de.top100golfcourses.panel.entity.BucketColor;
import de.top100golfcourses.panel.entity.RankedCourse;
import de.top100golfcourses.panel.entity.Rankings;
import de.top100golfcourses.panel.entity.Role;

public final class RankingGrid extends CustomComponent {

    public static final String BUCKET_COL_ID = "bucket";
    public static final String PLAYED_COL_ID = "played";

    private Rankings rankings;
    private boolean editable = false;
    private Grid<RankedCourse> grid;
    private Suggestions suggestions = null;

    public RankingGrid() { }

    private void init() {
        List<RankedCourse> courses = rankings.getRankedCourses();
        TextField nameField = new TextField();
        String suggestionsFile = "courses.txt";
        URL resource = getClass().getClassLoader().getResource(suggestionsFile);

        try {
            if (resource == null) throw new URISyntaxException("", "Resource is null");
            suggestions = new Suggestions(Paths.get(resource.toURI()));
            AutocompleteExtension<String> nameExtension = new AutocompleteExtension<>(nameField);
            nameExtension.setSuggestionListSize(10);
            nameExtension.setSuggestionGenerator(suggestions::getCourseNameSuggestions);
        }
        catch (URISyntaxException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, suggestionsFile, ex);
        }

        TextField commentsField = new TextField();
        DateField dateField = new DateField();
        grid = new Grid<>();
        Binder<RankedCourse> binder = grid.getEditor().getBinder();
        Binding<RankedCourse, LocalDate> lastPlayed = 
                binder.bind(dateField,
                            (RankedCourse course) -> course.getLastPlayed() == null ? null : course.getLastPlayed(),
                            (RankedCourse course, LocalDate date) -> course.setLastPlayed(date));
        StyleGenerator<RankedCourse> bucketStyleGenerator = (RankedCourse course) -> {
            return "color" + course.getBucketColor();
        };
        grid.setSizeFull();

        grid.addColumn(RankedCourse::getPos).setId("pos").setCaption("").setExpandRatio(0);
        grid.addColumn(course -> "").setId(BUCKET_COL_ID).setCaption("" + rankings.getRankedCourses().size()).setStyleGenerator(bucketStyleGenerator).setExpandRatio(0);
        grid.addColumn(RankedCourse::getName).setId("course").setEditorComponent(nameField, createCourseNameSetter()).setCaption("Course").setExpandRatio(1);
        grid.addColumn(RankedCourse::getLastPlayed, new LocalDateRenderer("yyyy-MM-dd")).setId(PLAYED_COL_ID).setEditorBinding(lastPlayed).setCaption("Played").setExpandRatio(0);
        grid.addColumn(RankedCourse::getComments).setId("comments").setEditorComponent(commentsField, RankedCourse::setComments).setCaption("Comments").setExpandRatio(3);

        if (this.editable) {
            grid.setSelectionMode(SelectionMode.SINGLE);
            grid.getEditor().setEnabled(true);
            grid.getEditor().setSaveCaption("Ok");
            grid.getEditor().addSaveListener((EditorSaveEvent<RankedCourse> event) -> {
                grid.setItems(courses);
            });
            installContextMenu();
            DnDGridRowEnabler.START(this.grid, this.rankings);
        }
        else {
            grid.getEditor().setEnabled(false);
            grid.setSelectionMode(SelectionMode.NONE);
        }

        grid.setHeightByRows(15);
        grid.setItems(courses);

        // The composition root MUST be set
        setCompositionRoot(grid);
    }

    private Setter<RankedCourse, String> createCourseNameSetter() {
        final Setter<RankedCourse, String> courseNameSetter;
        if (suggestions == null) {
            courseNameSetter = (RankedCourse course, String fieldValue) -> {
                course.setName(fieldValue);
                Logger.getAnonymousLogger().info("Input: " + course.toString());
            };
        }
        else {
            courseNameSetter = (RankedCourse course, String fieldValue) -> {
                if (suggestions.isSuggestion(fieldValue)) {
                    course.setName(fieldValue);
                    Logger.getAnonymousLogger().info("Input: " + course.toString());
                }
            };
        }
        return courseNameSetter;
    }

    private void installContextMenu() {
        final ContextMenu contextMenu = new ContextMenu(grid, true);
        contextMenu.addItem("Add Row", VaadinIcons.PLUS_CIRCLE_O, (MenuItem selectedItem) -> {
            addRow();
        });
        contextMenu.addItem("Delete Row", VaadinIcons.MINUS_CIRCLE_O, (MenuItem selectedItem) -> {
            deleteRow();
        });
        MenuItem bucket = contextMenu.addItem("Color", VaadinIcons.PAINT_ROLL, null);
        bucket.addItem(" Gold", new ThemeResource("gold.png"), (MenuItem selectedItem) -> {
            changeBucket(BucketColor.Gold);
        }).setStyleName("contextmenupadding");
        bucket.addItem(" Silver", new ThemeResource("silver.png"), (MenuItem selectedItem) -> {
            changeBucket(BucketColor.Silver);
        }).setStyleName("contextmenupadding");
        bucket.addItem(" Bronze", new ThemeResource("bronze.png"), (MenuItem selectedItem) -> {
            changeBucket(BucketColor.Bronze);
        }).setStyleName("contextmenupadding");
        bucket.addItem(" Field", new ThemeResource("field.png"), (MenuItem selectedItem) -> {
            changeBucket(BucketColor.Field);
        }).setStyleName("contextmenupadding");
        bucket.addItem(" DQ", new ThemeResource("dq.png"), (MenuItem selectedItem) -> {
            changeBucket(BucketColor.DQ);
        }).setStyleName("contextmenupadding");
    }

    public Rankings getRankings() {
        return this.rankings;
    }

    public Grid<RankedCourse> getGrid() {
        return grid;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setRankings(Rankings newRankings) {
        this.rankings = newRankings;
        String user = VaadinSession.getCurrent().getAttribute("user").toString();
        Role role = VaadinSession.getCurrent().getAttribute(Role.class);
        this.editable = user.equals(newRankings.getUser()) || role == Role.Correspondent;
        init();
    }

    private void addRow() {
        Set<RankedCourse> selectedRows = grid.getSelectedItems();
        int pos = selectedRows.size() == 1 ? selectedRows.iterator().next().getPos() + 1 : rankings.getRankedCourses().size() + 1;
        RankedCourse course = new RankedCourse();
        course.setPos(pos);
        this.rankings.insertRankedCourseAt(pos - 1, course);
        grid.setItems(this.rankings.getRankedCourses());
        grid.getColumn("bucket").setCaption("" + this.rankings.getRankedCourses().size());
        Logger.getAnonymousLogger().info("Added Row: " + pos);
    }

    private void deleteRow() {
        Set<RankedCourse> selectedRows = grid.getSelectedItems();
        if (selectedRows.size() == 1) {
            int pos = selectedRows.iterator().next().getPos();
            this.rankings.deleteRankedCourseAt(pos - 1);
            grid.setItems(this.rankings.getRankedCourses());
            grid.getColumn("bucket").setCaption("" + this.rankings.getRankedCourses().size());
            Logger.getAnonymousLogger().info("Deleted Row: " + pos);
        }
    }

    private void changeBucket(BucketColor color) {
        int newBucket = color.getIndex();
        Set<RankedCourse> selectedRows = grid.getSelectedItems();
        if (selectedRows.size() == 1) {
            RankedCourse changedCourse = selectedRows.iterator().next();
            if (changedCourse.getBucket() != newBucket) {
                this.rankings.deleteRankedCourseAt(changedCourse.getPos() - 1);
                RankedCourse bucketLeader = this.rankings.getFirstCourseIn(BucketColor.byIndex(newBucket));
                if (bucketLeader == null) {
                    int pos = this.rankings.getFirstPosForBucket(newBucket);
                    changedCourse.setPos(pos);
                    this.rankings.insertRankedCourseAt(pos - 1, changedCourse);
                    changedCourse.setBucket(newBucket);
                }
                else {
                    int pos = bucketLeader.getPos();
                    changedCourse.setPos(pos + 1);
                    this.rankings.insertRankedCourseAt(pos, changedCourse);
                }
            }
            grid.setItems(this.rankings.getRankedCourses());
        }
    }

}
