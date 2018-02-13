package de.top100golfcourses.panel.component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.contextmenu.MenuItem;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.icons.VaadinIcons;
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
import de.top100golfcourses.panel.entity.BucketColor;

import de.top100golfcourses.panel.entity.RankedCourse;
import de.top100golfcourses.panel.entity.Rankings;

public final class RankingGrid extends CustomComponent {

    private Rankings rankings;
    private boolean editable = false;
    private Grid<RankedCourse> grid;

    public RankingGrid() { }

    private void init() {
        List<RankedCourse> courses = rankings.getRankedCourses();
        TextField nameField = new TextField();
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

        grid.addColumn(RankedCourse::getPos).setCaption("").setExpandRatio(0);
        grid.addColumn(course -> "").setCaption("").setStyleGenerator(bucketStyleGenerator).setExpandRatio(0);
        grid.addColumn(RankedCourse::getName).setEditorComponent(nameField, RankedCourse::setName).setCaption("Name").setExpandRatio(1);
        grid.addColumn(RankedCourse::getLastPlayed, new LocalDateRenderer("yyyy-MM-dd")).setEditorBinding(lastPlayed).setCaption("Played").setExpandRatio(0);
        grid.addColumn(RankedCourse::getComments).setEditorComponent(commentsField, RankedCourse::setComments).setCaption("Comments").setExpandRatio(3);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.getEditor().setEnabled(this.editable);
        grid.getEditor().addSaveListener((EditorSaveEvent<RankedCourse> event) -> {
            grid.setItems(courses);
        });
        grid.setHeightByRows(15);
        grid.setItems(courses);

        DnDGridRowEnabler.START(this.grid, this.rankings);

        final ContextMenu contextMenu = new ContextMenu(grid, this.editable);
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

        // The composition root MUST be set
        setCompositionRoot(grid);
    }

    public Rankings getRankings() {
        return this.rankings;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setRankings(Rankings newRankings) {
        this.rankings = newRankings;
        String user = VaadinSession.getCurrent().getAttribute("user").toString();
        this.editable = user.equals(newRankings.getUser());
        init();
    }

    private void addRow() {
        Set<RankedCourse> selectedRows = grid.getSelectedItems();
        int pos = selectedRows.size() == 1 ? selectedRows.iterator().next().getPos() + 1 : rankings.getRankedCourses().size() + 1;
        RankedCourse course = new RankedCourse();
        course.setPos(pos);
        this.rankings.insertRankedCourseAt(pos - 1, course);
        grid.setItems(this.rankings.getRankedCourses());
    }

    private void deleteRow() {
        Set<RankedCourse> selectedRows = grid.getSelectedItems();
        if (selectedRows.size() == 1) {
            int pos = selectedRows.iterator().next().getPos();
            this.rankings.deleteRankedCourseAt(pos - 1);
            grid.setItems(this.rankings.getRankedCourses());
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
