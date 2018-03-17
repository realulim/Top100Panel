package de.top100golfcourses.panel.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomComponent;

import org.vaadin.chatbox.ChatBox;
import org.vaadin.chatbox.ChatBox.ClickListener;
import org.vaadin.chatbox.SharedChat;
import org.vaadin.chatbox.client.ChatLine;
import org.vaadin.chatbox.client.ChatUser;

import de.top100golfcourses.panel.da.ChatLog;
import de.top100golfcourses.panel.da.NitriteChatLog;
import de.top100golfcourses.panel.entity.ChatEntry;

public final class PersistentChatBox extends CustomComponent implements ClickListener {

    private final ChatLog chatLog = new NitriteChatLog();
    private final SharedChat sharedChat;
    private final String user;
    private boolean small = true; // Chatbox starts small and then toggles between big and small with every click

    public interface ChangeSizeListener {
        public void sizeChanged();
    }
    private final List<ChangeSizeListener> changeSizeListeners = new ArrayList<>();

    public void addChangeSizeListener(ChangeSizeListener listener) {
        changeSizeListeners.add(listener);
    }

    public void removeChangeSizeListener(ChangeSizeListener listener) {
        changeSizeListeners.remove(listener);
    }

    public PersistentChatBox(SharedChat sharedChat, String user) {

        this.sharedChat = sharedChat;
        this.user = user;

        createChatBox();

        if (sharedChat.getLinesStartingFrom(0).isEmpty()) {
            // read persistent chatlog from disk
            for (ChatEntry entry : chatLog.readLog()) {
                sharedChat.addLine(new ChatLine(entry.getTimestamp(), ChatUser.newUser(entry.getUser(), "blueuser", "blackuser"), entry.getText()));
            }
            sharedChat.addListener((ChatLine line) -> {
                chatLog.append(new ChatEntry(line));
            });
        }

    }

    public boolean isSmall() {
        return small;
    }

    private void createChatBox() {
        ChatBox chatBox = new ChatBox(sharedChat);
        ChatUser chatUser = ChatUser.newUser(user, "blueuser", "blackuser");
        chatBox.setUser(chatUser);
        chatBox.setShowSendButton(false);
        chatBox.addClickListener(this);

        if (small) {
            chatBox.setHeight(13, Sizeable.Unit.EX);
            chatBox.setWidth("95%");
            chatBox.removeStyleName("ChatBoxLarge");
            chatBox.addStyleName("ChatBoxSmall");        
        }
        else {
            chatBox.setHeight(65, Sizeable.Unit.EX);
            chatBox.setWidth("95%");
            chatBox.removeStyleName("ChatBoxSmall");
            chatBox.addStyleName("ChatBoxLarge");        
        }

        // The composition root MUST be set
        setCompositionRoot(chatBox);
    }

    @Override
    public void chatBoxClicked() {
        small = !small;
        createChatBox();
        for (ChangeSizeListener listener : changeSizeListeners) {
            listener.sizeChanged();
        }
    }

}
