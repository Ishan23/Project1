package com.example.ishankhandelwal.project1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chat extends AppCompatActivity {

    private MessageAdapter adapter;
    private List<Message> messageList;

    //chat variables
    private String chatID;
    private String senderID;
    private String receiverID;
    private String senderName;
    private String receiverName;
    private ChatObject chatObject;

    private DatabaseReference mDatabase;

    private Button sendButton;
    private EditText messageEditText;
    private String textMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sendButton = (Button)findViewById(R.id.sendButton);
        messageEditText = (EditText)findViewById(R.id.messageEditText);

        setupChat();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textMessage = messageEditText.getText().toString();
                messageEditText.setText("");

                Message message = new Message();
                message.setMessage(textMessage);
                message.setSenderId(senderID);
                message.setReceiverId(receiverID);
                message.setSenderName(senderName);
                message.setReceiverName(receiverName);

                chatObject.addMessage(message);

                mDatabase.child("chats").child(chatID).child("messages").push().setValue(message);
            }
        });



        setupRecyclerView();


    }



    protected void setupRecyclerView()
    {
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        messageList = new ArrayList<Message>();
        adapter = new MessageAdapter(messageList);

        recList.setAdapter(adapter);

        getMessages();

    }

    protected void setupChat()
    {
        Intent intent = getIntent();
        senderID = intent.getStringExtra("senderID");
        receiverID = intent.getStringExtra("receiverID");
        senderName = intent.getStringExtra("senderName");
        receiverName = intent.getStringExtra("receiverName");

        Log.d("Sender name",senderName);
        Log.d("Receiver name",receiverName);



        String IDs[] = new String[2];
        IDs[0] = senderID;
        IDs[1] = receiverID;
        Arrays.sort(IDs);

        chatID = IDs[0] + IDs[1];   //unique chat ID

        chatObject = new ChatObject(chatID, IDs[0], IDs[1]);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("chats").push().setValue(chatID);

    }

    protected void getMessages()
    {


        // Use Firebase to populate the list.
        mDatabase.child("chats").child(chatID).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                /*Contact contact = new Contact();
                contact.setUserID(dataSnapshot.getValue(Contact.class).getUserID());
                contact.setName(dataSnapshot.getValue(Contact.class).getName());
                contact.setContactNumber(dataSnapshot.getValue(Contact.class).getContactNumber());
                Log.d("User Name: ", contact.getName());
                allContacts.add(contact);
                Log.d("Total contacts", "" + allContacts.size());

                if(dataSnapshot.getValue(Contact.class).getUserID().equals(mUserId)) //get current username
                {
                    mUserName = dataSnapshot.getValue(Contact.class).getName();
                }*/
                Message message = new Message();
                message.setMessage(dataSnapshot.getValue(Message.class).getMessage());
                message.setSenderId(dataSnapshot.getValue(Message.class).getSenderId());
                message.setSenderName(dataSnapshot.getValue(Message.class).getSenderName());
                message.setReceiverId(dataSnapshot.getValue(Message.class).getReceiverId());
                message.setReceiverName(dataSnapshot.getValue(Message.class).getReceiverName());
                messageList.add(message);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
