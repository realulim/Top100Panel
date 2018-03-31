package de.top100golfcourses.panel.component;

import de.top100golfcourses.panel.da.Persistence;
import de.top100golfcourses.panel.entity.Rankings;

public final class ConfirmDeleteDialog extends AbstractConfirmationDialog {

    private final Persistence storage;
    private final Rankings rankingToDelete;

    public ConfirmDeleteDialog(Persistence storage, Rankings rankingToDelete) {
        super("Are you sure you want to delete this Ranking?");
        this.storage = storage;
        this.rankingToDelete = rankingToDelete;
    }

    @Override
    protected void confirmed() {
        storage.delete(rankingToDelete);
    }

    @Override
    protected void notConfirmed() {
        // nothing
    }

}
