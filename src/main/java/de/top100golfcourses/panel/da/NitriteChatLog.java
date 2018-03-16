package de.top100golfcourses.panel.da;

import java.util.List;
import java.util.logging.Logger;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.ObjectRepository;

import de.top100golfcourses.panel.entity.ChatEntry;

public class NitriteChatLog implements ChatLog {

    @Override
    public void append(ChatEntry chatEntry) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(ChatLog.DB).openOrCreate()) {
            ObjectRepository<ChatEntry> repo = db.getRepository(ChatEntry.class);
            WriteResult result = repo.insert(chatEntry);
            Logger.getAnonymousLogger().info("Inserted: " + result.getAffectedCount() + " (" + chatEntry.getId() + ")");
        }
    }

    @Override
    public List<ChatEntry> readLog() {
        try (Nitrite db = Nitrite.builder().compressed().filePath(ChatLog.DB).openOrCreate()) {
            ObjectRepository<ChatEntry> repo = db.getRepository(ChatEntry.class);
            List<ChatEntry> result = repo.find(FindOptions.sort("id", SortOrder.Ascending)).toList();
            Logger.getAnonymousLogger().info("Read: " + result.size());
            return result;
        }
    }

}
