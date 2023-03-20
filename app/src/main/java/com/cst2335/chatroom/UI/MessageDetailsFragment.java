package com.cst2335.chatroom.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cst2335.chatroom.Data.ChatMessage;
import com.cst2335.chatroom.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {

    ChatMessage selected;

    public  MessageDetailsFragment(ChatMessage m){

        selected=m;


    }






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        DetailsLayoutBinding binding=DetailsLayoutBinding.inflate(inflater);

        binding.getRoot().setBackgroundColor(Color.WHITE);
        binding.detailsMessage.setText("Message : "+selected.getMessage());
        binding.detailsTime.setText("Time : "+selected.getTimeSent());
        binding.detailsSendReceive.setText("is send : " +String.valueOf(selected.isSentButton()));
        binding.detailsDatabaseId.setText("Id = "+selected.id);






       return binding.getRoot();



    }
}
