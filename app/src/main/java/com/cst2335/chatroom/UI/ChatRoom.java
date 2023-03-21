package com.cst2335.chatroom.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cst2335.chatroom.Data.ChatMessage;
import com.cst2335.chatroom.Data.ChatMessageDAO;
import com.cst2335.chatroom.Data.ChatViewModel;
import com.cst2335.chatroom.Data.MessageDatabase;
import com.cst2335.chatroom.R;
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

    private  ChatViewModel chatModel;
   private ActivityChatRoomBinding chatRoomBinding;
    int position;

    View itemView1;


TextView tv_message;

boolean IsSelected;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);



        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);



            switch( item.getItemId() ) {
                case R.id.Item_1:


                    Log.i("tag1", "onOptionsItemSelected: chat room");


                    if (messages.size() != 0 && IsSelected) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
                        builder.setMessage("Do you want to Delete this message : " + tv_message.getText().toString()).
                                setTitle("Question").
                                setNegativeButton("no", (dialog, cl) -> {
                                })
                                .setPositiveButton("yes", (dialog, cl) -> {

                                    ChatMessage removedMessage = messages.get(position);
                                    thread.execute(() ->
                                    {

                                        mDAO.deleteMessage(removedMessage);

                                    });
                                    runOnUiThread(() -> {
                                        messages.remove(position);
                                        myAdapter.notifyItemRemoved(position);
                                    });


                                    Snackbar.make(chatRoomBinding.getRoot(), "You deleted message # " + tv_message.getText(), Snackbar.LENGTH_SHORT)
                                            .setAction("Undo", c -> {
                                                messages.add(position, removedMessage);
                                                myAdapter.notifyItemInserted(position);
                                            }).show();


                                    onBackPressed();


                                    IsSelected = false;


                                }).create().show();


                    }


                    break;


                case R.id.Item_2:
                    if (tv_message != null) {
                        Snackbar.make(chatRoomBinding.getRoot(), "You deleted message # " + tv_message.getText(), Snackbar.LENGTH_SHORT)
                                .show();

                    }
            }
            return true;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);








         chatRoomBinding=ActivityChatRoomBinding.inflate(getLayoutInflater());


        setSupportActionBar(chatRoomBinding.toolbar);













         chatModel = new ViewModelProvider(this).get(ChatViewModel.class);


         messages= chatModel.messages.getValue();

            if (messages==null ){


                chatModel.messages.setValue(messages=new  ArrayList<ChatMessage>());



                thread = Executors.newSingleThreadExecutor();

                thread.execute(() ->
                {

                    MessageDatabase db= Room.databaseBuilder(getApplicationContext(),MessageDatabase.class,"database-name").build();
                    mDAO = db.cmDAO();






                    messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database




                    runOnUiThread( () -> {

                                chatRoomBinding.recycleView.setAdapter( myAdapter );

                                setContentView(chatRoomBinding.getRoot());


                      if(messages.size()-1>0) {
                          chatRoomBinding.recycleView.smoothScrollToPosition(messages.size() - 1);
                      }

                    }); //You can then load the RecyclerView
                });

            }



        chatModel.selectedMessage.observe(this, (newMessageValue) -> {

            Log.i("tag", "onCreate: "+newMessageValue.getMessage());



            MessageDetailsFragment chatFragment = new MessageDetailsFragment(newMessageValue);


           getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation ,chatFragment).addToBackStack("").commit();




        });







            chatRoomBinding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
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






            chatRoomBinding.recycleView.setLayoutManager(new LinearLayoutManager(this));

            //the button was clicked, and a boolean true to specify that it was the Sent button that was clicked
            //As for a String representing the time sent


        chatRoomBinding.sendButton.setOnClickListener(click->{


            String text=chatRoomBinding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDatEndTime = sdf.format(new Date());

            messages.add(new ChatMessage(text,currentDatEndTime,true));


            thread.execute(() ->
            {

                mDAO.insertMessage(new ChatMessage(text,currentDatEndTime,true));


            });


            //redraw the whole list
            myAdapter.notifyItemInserted(messages.size()-1);

            chatRoomBinding.textInput.setText("");//now will be empty
            chatRoomBinding.recycleView.smoothScrollToPosition(messages.size()-1);

        });


            //the button was clicked, and a boolean false to specify that it was the receive button that was clicked
            //As for a String representing the time receive


        chatRoomBinding.receiveButton.setOnClickListener(click->{


            String text=chatRoomBinding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDatEndTime = sdf.format(new Date());

            messages.add(new ChatMessage(text,currentDatEndTime,false));

            thread.execute(() ->
            {

                mDAO.insertMessage(new ChatMessage(text,currentDatEndTime,false));


            });




                //redraw the whole list
            myAdapter.notifyItemInserted(messages.size()-1);

            chatRoomBinding.textInput.setText("");//now will be empty
            chatRoomBinding.recycleView.smoothScrollToPosition(messages.size()-1);



        });




    }



        //itemView will the the root of the layout, LinearLayout

 class MyRowHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public  TextView timeText;


        public MyRowHolder(@NonNull View itemView) {

            super(itemView);


            itemView1 = itemView;






            itemView.setOnClickListener(clk->{








                position = getAbsoluteAdapterPosition();
                ChatMessage selected = messages.get(position);
                chatModel.selectedMessage.postValue(selected);
                tv_message=messageText;

                IsSelected=true;









/*

*/



            });





            messageText= itemView.findViewById(R.id.message);
            timeText= itemView.findViewById(R.id.time);


        }
    }






}