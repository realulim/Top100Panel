package de.top100golfcourses.panel.component;

import java.util.List;

import de.top100golfcourses.panel.da.Persistence;
import de.top100golfcourses.panel.entity.Rankings;

public class RenameRankingDialog extends AbstractRankingDialog {

    private final Persistence storage;
    private final Rankings rankingsToRename;

    public RenameRankingDialog(Persistence storage, List<Rankings> existingRankings, Rankings rankingsToRename) {
        super(existingRankings, "New Name of Ranking:", rankingsToRename.getName());
        this.storage = storage;
        this.rankingsToRename = rankingsToRename;
    }

    @Override
    protected void processRankingName(String user, String selectedName) {
        storage.rename(rankingsToRename, selectedName);
    }

    @Override
    protected boolean checkForDuplicate(List<Rankings> rankings, String user, String name) {
        for (Rankings ranking : rankings) {
            if (ranking.getUser().equals(rankingsToRename.getUser()) && ranking.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
