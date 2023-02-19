package com.cst2335.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cst2335.chatroom.Data.ChatMessage;
import com.cst2335.chatroom.Data.ChatViewModel;
import com.cst2335.chatroom.databinding.ActivityChatRoomBinding;
import com.cst2335.chatroom.databinding.ReceiveMessagesBinding;
import com.cst2335.chatroom.databinding.SentMessageBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoom extends AppCompatActivity {


    private RecyclerView.Adapter myAdapter;
    private   ArrayList<ChatMessage> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityChatRoomBinding binding=ActivityChatRoomBinding.inflate(getLayoutInflater());

        ChatViewModel chatModel = new ViewModelProvider(this).get(ChatViewModel.class);

            messages= chatModel.messages.getValue();

            if (messages==null){
                chatModel.messages.postValue(messages=new  ArrayList<ChatMessage>());
            }


            setContentView(binding.getRoot());







            binding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
                @NonNull
                @Override
                public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    if(messages.get(viewType).isSentButton()){
                        SentMessageBinding binding = SentMessageBinding.inflate(getLayoutInflater(),
                                parent, false);

                        View root = binding.getRoot();
                        return new MyRowHolder(root);

                    }else {
                        ReceiveMessagesBinding binding1=ReceiveMessagesBinding.inflate(getLayoutInflater(),
                                parent,false);
                        View root = binding1.getRoot();
                        return new MyRowHolder(root);

                    }




                }

                @Override
                public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                    String obj = messages.get(position).getMessage();
                    holder.messageText.setText(obj);


holder.timeText.setText(messages.get(position).getTimeSent());



                }

                @Override
                public int getItemCount() {
                    return messages.size();
                }

                @Override
                public int getItemViewType(int position) {
                    return position % 2;
                }
            });















binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        binding.sendButton.setOnClickListener(click->{


            String text=binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDatEndTime = sdf.format(new Date());

            messages.add(new ChatMessage(text,currentDatEndTime,true));
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });






        binding.receiveButton.setOnClickListener(click->{
ChatMessage chatMessage=messages.get(messages.size()-1);
            String text=chatMessage.getMessage();

            String currentDatEndTime = chatMessage.getTimeSent();

            messages.add(new ChatMessage(text,currentDatEndTime,false));
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });










    }





 class MyRowHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public  TextView timeText;

        public MyRowHolder(@NonNull View itemView) {

            super(itemView);
            messageText= itemView.findViewById(R.id.message);
            timeText= itemView.findViewById(R.id.time);

        }
    }






}