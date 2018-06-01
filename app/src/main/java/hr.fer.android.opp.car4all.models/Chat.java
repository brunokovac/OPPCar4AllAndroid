package hr.fer.android.opp.car4all.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class Chat implements Serializable {

    private int chatID;

    private boolean publicChat = true;

    private List<Integer> lastSeenByUsersIDs = new ArrayList<>();

    private List<Message> messages;

    public Chat() {
    }

    public void addMessage(Message message) {
        DAOProvider.getDao().addMessageToChat(message, this);
        addSeenByUser(message.getSender().getPersonID(), true);

        if (messages != null){
            messages.add(0, message);
        }
    }

    public void switchToPrivate() throws DAOException{
        this.publicChat = false;
        DAOProvider.getDao().switchChatToPrivate(this);
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public void setPublicChat(boolean publicChat) {
        this.publicChat = publicChat;
    }

    public boolean isPublicChat() {
        return publicChat;
    }

    public List<Integer> getLastSeenByUsersIDs() {
        return lastSeenByUsersIDs;
    }

    public void addSeenByUser(int userID, boolean newMessage) throws DAOException{
        if (newMessage) {
            lastSeenByUsersIDs = new ArrayList<>();
        }

        if (!lastSeenByUsersIDs.contains(userID)) {
            lastSeenByUsersIDs.add(userID);
            DAOProvider.getDao().addChatSeenByUsers(this, lastSeenByUsersIDs);
        }
    }

    public List<Message> getMessages() throws DAOException {
        return loadMessages();
    }

    public List<Message> loadMessages() throws DAOException {
        return DAOProvider.getDao().getMessagesForChat(this);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
