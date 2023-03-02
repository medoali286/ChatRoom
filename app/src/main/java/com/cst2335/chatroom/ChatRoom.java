package com.cst2335.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cst2335.chatroom.Data.ChatMessage;
import com.cst2335.chatroom.Data.ChatMessageDAO;
import com.cst2335.chatroom.Data.ChatViewModel;
import com.cst2335.chatroom.Data.MessageDatabase;
import com.cst2335.chatroom.databinding.ActivityChatRoomBinding;
import com.cst2335.chatroom.databinding.ReceiveMessagesBinding;
import com.cst2335.chatroom.databinding.SentMessageBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatRoom extends AppCompatActivity {


    private RecyclerView.Adapter myAdapter;
    private   ArrayList<ChatMessage> messages;
   private ChatMessageDAO mDAO;
   private Executor thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        ActivityChatRoomBinding binding=ActivityChatRoomBinding.inflate(getLayoutInflater());

        ChatViewModel chatModel = new ViewModelProvider(this).get(ChatViewModel.class);

            messages= chatModel.messages.getValue();

            if (messages==null){


                chatModel.messages.setValue(messages=new  ArrayList<ChatMessage>());



                thread = Executors.newSingleThreadExecutor();

                thread.execute(() ->
                {

                    MessageDatabase db= Room.databaseBuilder(getApplicationContext(),MessageDatabase.class,"database-name").build();
                    mDAO = db.cmDAO();






                    messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database




                    runOnUiThread( () -> {

                                binding.recycleView.setAdapter( myAdapter );

                                setContentView(binding.getRoot());


                      if(messages.size()-1>0) {
                          binding.recycleView.smoothScrollToPosition(messages.size() - 1);
                      }

                    }); //You can then load the RecyclerView
                });

            }




            binding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
                @NonNull
                @Override
                public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    if(viewType==0){
                        //always inflates Sent_message.xml
                        SentMessageBinding sentMessageBinding = SentMessageBinding.inflate(getLayoutInflater(),
                                parent, false);

                        View root = sentMessageBinding.getRoot();
                        return new MyRowHolder(root);

                    }else {

                        //always inflates receive_message.xml
                        ReceiveMessagesBinding receiveMessagesBinding=ReceiveMessagesBinding.inflate(getLayoutInflater(),
                                parent,false);
                        View root = receiveMessagesBinding.getRoot();
                        return new MyRowHolder(root);

                    }

                }


                //what are the textViews set to for row POSITION?
                @Override
                public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {



                        String strMessage = messages.get(position).getMessage();
                        holder.messageText.setText(strMessage);

                        holder.timeText.setText(messages.get(position).getTimeSent());



                }

                @Override
                public int getItemCount() {
                    return messages.size();
                }

                //function to check what kind of ChatMessage object is at row position
                // If the isSend is true, then return 0
                // so that the onCreateViewHolder checks the viewType and inflates a send_message layout.
                // If isSend is false, then getItemViewType returns 1 and onCreateViewHolder checks
                // if the viewType is 1 and inflates a receive_message layout.


                @Override
                public int getItemViewType(int position) {
                    if(messages.get(position).isSentButton()) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
            });






            binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

            //the button was clicked, and a boolean true to specify that it was the Sent button that was clicked
            //As for a String representing the time sent


        binding.sendButton.setOnClickListener(click->{


            String text=binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDatEndTime = sdf.format(new Date());

            messages.add(new ChatMessage(text,currentDatEndTime,true));


            thread.execute(() ->
            {

                mDAO.insertMessage(new ChatMessage(text,currentDatEndTime,true));


            });


            //redraw the whole list
            myAdapter.notifyItemInserted(messages.size()-1);

            binding.textInput.setText("");//now will be empty
            binding.recycleView.smoothScrollToPosition(messages.size()-1);

        });


            //the button was clicked, and a boolean false to specify that it was the receive button that was clicked
            //As for a String representing the time receive


        binding.receiveButton.setOnClickListener(click->{


            String text=binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDatEndTime = sdf.format(new Date());

            messages.add(new ChatMessage(text,currentDatEndTime,false));

            thread.execute(() ->
            {

                mDAO.insertMessage(new ChatMessage(text,currentDatEndTime,false));


            });




                //redraw the whole list
            myAdapter.notifyItemInserted(messages.size()-1);

            binding.textInput.setText("");//now will be empty
            binding.recycleView.smoothScrollToPosition(messages.size()-1);



        });




    }



        //itemView will the the root of the layout, LinearLayout

 class MyRowHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public  TextView timeText;

        public MyRowHolder(@NonNull View itemView) {

            super(itemView);


            itemView.setOnClickListener(clk->{




                AlertDialog.Builder builder=new AlertDialog.Builder(ChatRoom.this);
                builder.setMessage("Do you want to Delete this message : "+messageText.getText()).
                        setTitle("Question").
                        setNegativeButton("no",(dialog,cl)->{})
                        .setPositiveButton("yes",(dialog,cl)->{
                            int Position=getAbsoluteAdapterPosition();
                            ChatMessage removedMessage=messages.get(Position);

                            thread.execute(() ->
                            {

                                if (messages.size()!=Position) {

                                    mDAO.deleteMessage(removedMessage);

                                }


                            });

                                runOnUiThread( () ->  {


                                    messages.remove(Position);
                                    myAdapter.notifyItemRemoved(Position);

                                });
                                Snackbar.make(itemView,"You deleted message # "+messageText.getText() ,Snackbar.LENGTH_SHORT)
                                    .setAction("Undo",c->{

                                        messages.add(Position,removedMessage);
                                        myAdapter.notifyItemInserted(Position);

                                    }).show();


                        }).create().show();





            });




            messageText= itemView.findViewById(R.id.message);
            timeText= itemView.findViewById(R.id.time);


        }
    }






}