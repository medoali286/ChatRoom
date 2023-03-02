package com.cst2335.chatroom.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import kotlin.jvm.JvmOverloads;

@Entity
public class ChatMessage {


    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate=true)
    public int id;

    @ColumnInfo(name="message")
       protected String message;
    @ColumnInfo(name="timeSent")
    protected String timeSent;
    @ColumnInfo(name="SendOrReceive")
        boolean isSentButton;


    public  ChatMessage(String message, String timeSent, boolean isSentButton)
        {
            this.message = message;
           this.timeSent = timeSent;
          this.isSentButton = isSentButton;
        }


    public int getId() {
        return id;
    }

    public String getMessage() {
            return message;
        }

        public String getTimeSent() {
            return timeSent;
        }

        public boolean isSentButton() {
            return isSentButton;
        }

}
