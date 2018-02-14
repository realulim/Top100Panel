package de.top100golfcourses.panel.da;

import java.util.Arrays;

import com.vaadin.ui.Grid;

import org.vaadin.addons.excelexporter.ExportToExcel;
import org.vaadin.addons.excelexporter.configuration.ExportExcelComponentConfiguration;
import org.vaadin.addons.excelexporter.configuration.ExportExcelConfiguration;
import org.vaadin.addons.excelexporter.configuration.ExportExcelSheetConfiguration;
import org.vaadin.addons.excelexporter.configuration.builder.ComponentHeaderConfigurationBuilder;
import org.vaadin.addons.excelexporter.configuration.builder.ExportExcelComponentConfigurationBuilder;
import org.vaadin.addons.excelexporter.configuration.builder.ExportExcelConfigurationBuilder;
import org.vaadin.addons.excelexporter.configuration.builder.ExportExcelSheetConfigurationBuilder;
import org.vaadin.addons.excelexporter.model.ExportType;

import de.top100golfcourses.panel.entity.RankedCourse;

public class ExcelExporter {

    private static final transient String[] visibleColumns = new String[]{"pos", "bucket", "name", "lastPlayed", "comments"};
    private static final transient String[] columnHeaders = new String[]{"Pos", "Color", "Course", "Played", "Comments"};

    public static ExportToExcel<RankedCourse> export(Grid<RankedCourse> grid) {
        ExportExcelComponentConfiguration<RankedCourse> componentConfig = new ExportExcelComponentConfigurationBuilder<RankedCourse>()
                .withGrid(grid)
                .withVisibleProperties(visibleColumns)
                .withHeaderConfigs(
                        Arrays.asList(
                                new ComponentHeaderConfigurationBuilder()
                                        .withAutoFilter(true)
                                        .withColumnKeys(columnHeaders)
                                        .build()))
                .withIntegerFormattingProperties(Arrays.asList("pos", "bucket"))
                .build();
        ExportExcelSheetConfiguration<RankedCourse> sheetConfig = new ExportExcelSheetConfigurationBuilder<RankedCourse>()
                .withReportTitle("Rankings Title")
                .withSheetName("Sheet Name")
                .withComponentConfigs(Arrays.asList(componentConfig))
                .withIsHeaderSectionRequired(true)
                .withDateFormat("yyyy-MM-dd")
                .build();
        ExportExcelConfiguration<RankedCourse> excelConfig = new ExportExcelConfigurationBuilder<RankedCourse>()
                .withSheetConfigs(Arrays.asList(sheetConfig))
                .withExportFileName("rankings.xls")
                .build();
        return new ExportToExcel<>(ExportType.XLS, excelConfig);
    }

}
