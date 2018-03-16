package de.top100golfcourses.panel.da;

import java.util.List;

import de.top100golfcourses.panel.entity.ChatEntry;

public interface ChatLog {

    static String DB = "chatlog.db";
    static String ID = "top100chat";

    void append(ChatEntry entry);

    List<ChatEntry> readLog();

}
