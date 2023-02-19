package com.cst2335.chatroom.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {

   public MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>();




}
