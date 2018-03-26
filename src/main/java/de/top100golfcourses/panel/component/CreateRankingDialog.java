package de.top100golfcourses.panel.component;

import java.util.ArrayList;
import java.util.List;

import de.top100golfcourses.panel.entity.Rankings;

public final class CreateRankingDialog extends AbstractRankingDialog {

    private final Rankings newRankings = new Rankings();

    public CreateRankingDialog(List<Rankings> existingRankings) {
        super(existingRankings, "Name of new Ranking:", "Germany"); // Set window caption
    }

    @Override
    protected void processRankingName(String user, String selectedName) {
        newRankings.setRankedCourses(new ArrayList<>());
        newRankings.setUser(user);
        newRankings.setName(selectedName);
        existingRankings.add(newRankings);
    }

    public Rankings getNewRankings() {
        return newRankings;
    }

}
