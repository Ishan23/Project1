package com.example.ishankhandelwal.project1;

import java.util.ArrayList;

/**
 * Created by Ishan Khandelwal on 12/28/2016.
 */

public class ChatObject
{
    private String chatID;
    private String userID1;
    private String userID2;
    private String userName1;
    private String userName2;

    private ArrayList<Message> messages;

    public ChatObject() {
        messages =new ArrayList<Message>();
    }

    public ChatObject(String chatID, String userID1, String userID2) {
        this.chatID = chatID;
        this.userID1 = userID1;
        this.userID2 = userID2;
        messages =new ArrayList<Message>();
    }

    public ChatObject(String chatID, String userID1, String userID2, ArrayList<Message> messages) {
        this.chatID = chatID;
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.messages =new ArrayList<Message>();
        this.messages = messages;
    }

    public ChatObject(String chatID, String userID1, String userID2, String userName1, String userName2) {
        this.chatID = chatID;
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.userName1 = userName1;
        this.userName2 = userName2;
        messages =new ArrayList<Message>();
    }

    public ChatObject(String chatID, String userID1, String userID2, String userName1, String userName2, ArrayList<Message> messages) {
        this.chatID = chatID;
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.userName1 = userName1;
        this.userName2 = userName2;
        this.messages =new ArrayList<Message>();
        this.messages = messages;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getUserID1() {
        return userID1;
    }

    public void setUserID1(String userID1) {
        this.userID1 = userID1;
    }

    public String getUserID2() {
        return userID2;
    }

    public void setUserID2(String userID2) {
        this.userID2 = userID2;
    }

    public String getUserName1() {
        return userName1;
    }

    public void setUserName1(String userName1) {
        this.userName1 = userName1;
    }

    public String getUserName2() {
        return userName2;
    }

    public void setUserName2(String userName2) {
        this.userName2 = userName2;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message)
    {
        messages.add(message);
    }
}
