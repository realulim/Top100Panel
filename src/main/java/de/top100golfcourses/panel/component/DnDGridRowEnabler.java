package de.top100golfcourses.panel.component;

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDragStartEvent;
import com.vaadin.ui.components.grid.GridDropTarget;

import de.top100golfcourses.panel.entity.RankedCourse;
import de.top100golfcourses.panel.entity.Rankings;

public class DnDGridRowEnabler {

    private final Grid<RankedCourse> grid;
    private final Rankings rankings;

    private RankedCourse draggedItem = null;

    private DnDGridRowEnabler(Grid<RankedCourse> grid, Rankings rankings) {
        this.grid = grid;
        this.rankings = rankings;
    }

    public static void START(Grid<RankedCourse> grid, Rankings rankings) {
        DnDGridRowEnabler enabler = new DnDGridRowEnabler(grid, rankings);
        enabler.init();
    }

    private void init() {
        GridDragSource<RankedCourse> dragSource = new GridDragSource<>(grid);
        dragSource.setEffectAllowed(EffectAllowed.MOVE);
        dragSource.addGridDragStartListener((GridDragStartEvent<RankedCourse> event) -> {
            draggedItem = event.getDraggedItems().get(0);
        });
        GridDropTarget<RankedCourse> dropTarget = new GridDropTarget<>(grid, DropMode.ON_TOP);
        dropTarget.setDropEffect(DropEffect.MOVE);
        dropTarget.addGridDropListener(event -> {
            if (draggedItem != null) {
                int startPos = draggedItem.getPos();
                int endPos = event.getDropTargetRow().get().getPos();
                Notification.show("Dragged from " + startPos + " to " + endPos);
                rankings.deleteRankedCourseAt(startPos - 1);
                rankings.insertRankedCourseAt(endPos - 1, draggedItem);
                draggedItem.setPos(endPos);
                grid.setItems(rankings.getRankedCourses());
                draggedItem = null;
            }
        });
        
    }

}
