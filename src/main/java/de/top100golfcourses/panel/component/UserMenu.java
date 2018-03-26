package de.top100golfcourses.panel.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import de.top100golfcourses.panel.MainPage;
import de.top100golfcourses.panel.aggregation.Algorithm;
import de.top100golfcourses.panel.aggregation.Linear;
import de.top100golfcourses.panel.aggregation.Quadratic;
import de.top100golfcourses.panel.entity.Rankings;
import de.top100golfcourses.panel.entity.Role;
import de.top100golfcourses.panel.entity.TopList;

public final class UserMenu extends CustomComponent {

    private final RankingGrid rankingGrid;
    private final List<Rankings> allRankings;
    private final List<MenuItem> aggregationMenuItems = new ArrayList<>();

    public UserMenu(RankingGrid rankingGrid, List<Rankings> allRankings) {
        this.rankingGrid = rankingGrid;
        this.allRankings = allRankings;

        VerticalLayout layout = new VerticalLayout();
        MenuBar userMenu = new MenuBar();
        userMenu.setAutoOpen(true);
        userMenu.setDescription(VaadinSession.getCurrent().getAttribute(Role.class).toString());

        MenuItem user = userMenu.addItem(VaadinSession.getCurrent().getAttribute("user").toString(), null, null);

        if (allRankings.size() > 0) {
            MenuItem aggregations = user.addItem("Aggregations", null, null);
            aggregationMenuItems.add(aggregations.addItem("Linear", (MenuItem selectedItem) -> {
                createTopList(new Linear());
            }));
            aggregationMenuItems.add(aggregations.addItem("Quadratic", (MenuItem selectedItem) -> {
                createTopList(new Quadratic());
            }));
        }

        user.addItem("Logout", null, (MenuItem selectedItem) -> {
            getUI().getNavigator().removeView(MainPage.NAME);
            VaadinSession.getCurrent().setAttribute("user", null);
            Page.getCurrent().setUriFragment("");
        });

        // The composition root MUST be set
        layout.addComponents(userMenu);
        setCompositionRoot(layout);

        // this is not needed for a Composite
        setSizeUndefined();
    }

    public List<MenuItem> getAggregationMenuItems() {
        return aggregationMenuItems;
    }

    private void createTopList(Algorithm algorithm) {
        String name = allRankings.get(0).getName();
        TopList topList = new TopList(name, allRankings, algorithm);
        rankingGrid.setRankings(topList.toRankings());
        rankingGrid.getGrid().removeColumn(RankingGrid.BUCKET_COL_ID);
        rankingGrid.getGrid().removeColumn(RankingGrid.PLAYED_COL_ID);
        rankingGrid.getGrid().getColumns().stream().forEach(item -> item.setSortable(false));
    }

}
