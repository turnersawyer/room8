package com.example.room8.ui.dashboard.chat;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String messageText;
    private Timestamp messageTime;
    private String user;
    private String UID;

    public ChatMessage(String users, String messageText) {
        this.user = users;
        this.messageText = messageText;
        // Initialize to current time
  //      this.messageTime = time;
    }

    public ChatMessage(){

    }

    public String getName() {
        return user;
    }

    public void setName(String user) {
        this.user = user;
    }

    public String getMessage() {
        return messageText;
    }

    public void setMessage(String messageText) {
        this.messageText = messageText;
    }

    public String getUID(){
        return UID;
    }

    public void setUID(String uid) {
        this.UID = uid;
    }

    public Timestamp getTime() {
        return messageTime;
    }

    public void setTime(Timestamp messageTime) {
        this.messageTime = messageTime;
    }


}
