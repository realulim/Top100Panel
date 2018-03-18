package de.top100golfcourses.panel.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;

import de.top100golfcourses.panel.entity.Rankings;

public final class CreateRankingDialog extends AbstractRankingDialog {

    private final ComboBox<Rankings> select;

    public CreateRankingDialog(List<Rankings> existingRankings, ComboBox<Rankings> select) {
        super(existingRankings, "Name of new Ranking:", "Germany"); // Set window caption
        this.select = select;
    }

    @Override
    protected void processRankingName(String user, String selectedName) {
        Rankings newRankings = new Rankings();
        newRankings.setRankedCourses(new ArrayList<>());
        newRankings.setUser(user);
        newRankings.setName(selectedName);
        existingRankings.add(newRankings);
        select.setItems(existingRankings);
        select.setValue(newRankings);
    }

}
