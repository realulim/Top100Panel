package de.top100golfcourses.panel.component;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomComponent;
import org.vaadin.chatbox.ChatBox;
import org.vaadin.chatbox.SharedChat;
import org.vaadin.chatbox.client.ChatUser;

public final class ChatPanel extends CustomComponent {

    public ChatPanel(SharedChat sharedChat, String user) {
        ChatBox chatBox = new ChatBox(sharedChat);
        ChatUser chatUser = ChatUser.newUser(user);
        chatBox.setUser(chatUser);
        chatBox.setShowSendButton(false);
        chatBox.setHeight(13, Sizeable.Unit.EX);
        chatBox.setWidth("90%");
        chatBox.addStyleName("chatbox");

        // The composition root MUST be set
        setCompositionRoot(chatBox);
    }

}
