package de.top100golfcourses.panel.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.dizitart.no2.objects.Id;

import org.vaadin.chatbox.client.ChatLine;

public class ChatEntry implements Serializable {

    @Id
    private String id;

    private String ts;
    private String user;
    private String text;

    private transient final DateTimeFormatter dtfPersist = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss.SSS");
    private transient final DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss");

    public ChatEntry() { }

    public ChatEntry(ChatLine line) {
        this.ts = dtfPersist.format(LocalDateTime.now());
        this.user = line.getUser().getName();
        this.text = line.getText();
        setId();
    }

    public String getId() {
        return id;
    }

    private void setId() {
        this.id = "[" + ts + "] " + user;
    }

    public String getTs() {
        if (ts == null) return null;
        else {
            LocalDateTime parsed = dtfPersist.parse(ts, LocalDateTime::from);
            return dtfDisplay.format(parsed);
        } 
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.ts);
        hash = 37 * hash + Objects.hashCode(this.user);
        hash = 37 * hash + Objects.hashCode(this.text);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatEntry other = (ChatEntry) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        return Objects.equals(this.ts, other.ts);
    }

}
