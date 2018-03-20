package com.bonlai.socialdiningapp.detail.gathering;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.ChatMessage;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    public static final String GATHERING_ID="gatheringId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.message);

        final String gatheringId=Integer.toString(getIntent().getIntExtra(GATHERING_ID,0));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(gatheringId)
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                MyUserHolder.getInstance().getUser().getUsername())
                        );

                // Clear the input
                input.setText("");
            }
        });

        displayChatMessages(gatheringId);

    }

    private void displayChatMessages(String gatheringId) {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("chats").child(gatheringId);
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.message)
                .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
