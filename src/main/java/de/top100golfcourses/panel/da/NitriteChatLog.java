package de.top100golfcourses.panel.da;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.exceptions.NitriteException;
import org.dizitart.no2.objects.ObjectRepository;

import de.top100golfcourses.panel.entity.ChatEntry;

public class NitriteChatLog implements ChatLog {

    private final int MAX_SCROLLBACK_BUFFER = 1000;

    @Override
    public void append(ChatEntry chatEntry) {
        try (Nitrite db = Nitrite.builder().compressed().filePath(ChatLog.DB).openOrCreate()) {
            ObjectRepository<ChatEntry> repo = db.getRepository(ChatEntry.class);
            WriteResult result = repo.insert(chatEntry);
            Logger.getAnonymousLogger().info("Inserted: " + result.getAffectedCount() + " (" + chatEntry.getId() + ")");
        }
        catch (NitriteException ex) {
            Logger.getAnonymousLogger().severe(chatEntry.toString());
            throw ex;
        }
    }

    @Override
    public List<ChatEntry> readLog() {
        try (Nitrite db = Nitrite.builder().compressed().filePath(ChatLog.DB).openOrCreate()) {
            ObjectRepository<ChatEntry> repo = db.getRepository(ChatEntry.class);
            List<ChatEntry> result = repo.find(FindOptions.sort("id", SortOrder.Descending).thenLimit(0, MAX_SCROLLBACK_BUFFER)).toList();
            Collections.reverse(result);
            Logger.getAnonymousLogger().info("Read: " + result.size());
            return result;
        }
        catch (NitriteException ex) {
            Logger.getAnonymousLogger().severe("Cannot read " + ChatLog.DB);
            throw ex;
        }
    }

}
