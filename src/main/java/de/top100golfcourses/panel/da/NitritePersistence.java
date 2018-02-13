package de.top100golfcourses.panel.da;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import de.top100golfcourses.panel.entity.Rankings;

public class NitritePersistence implements Persistence {

    @Override
    public void save(Rankings rankings) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(Persistence.DB).openOrCreate()) {
            ObjectRepository<Rankings> repo = db.getRepository(Rankings.class);
            Cursor<Rankings> cursor = repo.find(ObjectFilters.eq("id", rankings.getId()));
            if (cursor.totalCount() == 0) {
                WriteResult result = repo.insert(rankings);
                Logger.getAnonymousLogger().info("Inserted: " + result.getAffectedCount() + " (" + rankings.getId() + ")");
            }
            else {
                WriteResult result = repo.update(rankings);
                Logger.getAnonymousLogger().info("Updated: " + result.getAffectedCount() + " (" + rankings.getId() + ")");
            }
            Notification notif = new Notification("Saved " + rankings.getName(), Type.HUMANIZED_MESSAGE);
            notif.setDelayMsec(2000);
            notif.show(Page.getCurrent());
        }
    }

    @Override
    public List<Rankings> findByUser(String user) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(Persistence.DB).openOrCreate()) {
            ObjectRepository<Rankings> repo = db.getRepository(Rankings.class);
            return repo.find(ObjectFilters.eq("user", user)).toList();
        }
    }

    @Override
    public List<Rankings> findAll() {
        try (Nitrite db = Nitrite.builder().compressed().filePath(Persistence.DB).openOrCreate()) {
            ObjectRepository<Rankings> repo = db.getRepository(Rankings.class);
            return repo.find().toList();
        }
    }

    @Override
    public List<Rankings> findAllSortByUser(String user) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(Persistence.DB).openOrCreate()) {
            ObjectRepository<Rankings> repo = db.getRepository(Rankings.class);
            List<Rankings> preSorted = repo.find(FindOptions.sort("id", SortOrder.Ascending)).toList();
            List<Rankings> sorted = new ArrayList<>();
            int i = 0;
            for (Rankings rankings : preSorted) {
                if (rankings.getUser().equals(user)) {
                    sorted.add(i++, rankings);
                }
                else {
                    sorted.add(rankings);
                }
            }
            return sorted;
        }
    }

    @Override
    public void delete(Rankings rankings) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(Persistence.DB).openOrCreate()) {
            ObjectRepository<Rankings> repo = db.getRepository(Rankings.class);
            repo.remove(rankings);
        }
    }

}
