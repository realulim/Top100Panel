package de.top100golfcourses.panel.component;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomComponent;

import org.vaadin.chatbox.ChatBox;
import org.vaadin.chatbox.SharedChat;
import org.vaadin.chatbox.client.ChatLine;
import org.vaadin.chatbox.client.ChatUser;

import de.top100golfcourses.panel.da.ChatLog;
import de.top100golfcourses.panel.da.NitriteChatLog;
import de.top100golfcourses.panel.entity.ChatEntry;

public final class PersistentChatBox extends CustomComponent {

    private final ChatLog chatLog = new NitriteChatLog();

    public PersistentChatBox(SharedChat sharedChat, String user) {

        ChatBox chatBox = new ChatBox(sharedChat);
        ChatUser chatUser = ChatUser.newUser(user, "blueuser", "blackuser");
        chatBox.setUser(chatUser);
        chatBox.setShowSendButton(false);
        chatBox.setHeight(13, Sizeable.Unit.EX);
        chatBox.setWidth("95%");

        if (sharedChat.getLinesStartingFrom(0).isEmpty()) {
            // read persistent chatlog from disk
            for (ChatEntry entry : chatLog.readLog()) {
                sharedChat.addLine(new ChatLine(entry.getTimestamp(), ChatUser.newUser(entry.getUser(), "blueuser", "blackuser"), entry.getText()));
            }
            sharedChat.addListener((ChatLine line) -> {
                chatLog.append(new ChatEntry(line));
            });
        }

        // The composition root MUST be set
        setCompositionRoot(chatBox);
    }

}
