package de.top100golfcourses.panel.da;

import com.vaadin.addon.tableexport.DefaultGridHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableHolder;
import com.vaadin.ui.Grid;

public class RankingsExporter {

    public static ExcelExport create(Grid grid) {
        final TableHolder tableHolder = new DefaultGridHolder(grid);
        ExcelExport export = new ExcelExport(tableHolder);
        return export;
    }

}
