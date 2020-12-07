package com.rash.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rash.chatnwork.Adapters.MessegeAdapter;
import com.rash.chatnwork.Model.Chat;
import com.rash.chatnwork.Model.UserAccount;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessegeActivity extends AppCompatActivity {

    String receiverkey, receiveremail, receiverusername, receiverimage, senderemail;
    TextView goback, rtitle;
    CircleImageView rimage;
    private Toolbar toolbar;
    private EditText messegetext;
    private ImageButton sendbtn;
    private DatabaseReference databaseReference;

    private MessegeAdapter adapter;
    private RecyclerView recyclerView;
    private List<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege);


        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.messrecylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        Bundle b = getIntent().getExtras();
        receiverkey = b.getString("userkey");
        receiveremail = b.getString("useremail");
        receiverusername = b.getString("username");
        receiverimage = b.getString("userimage");
        senderemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        toolbar = findViewById(R.id.messegeToolBar);
        setSupportActionBar(toolbar);
//        try{
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }catch (NullPointerException e){
//            Log.d("oppai",e.getMessage());        }


        rtitle = toolbar.findViewById(R.id.messname);
        rimage = toolbar.findViewById(R.id.messimage);
        goback = toolbar.findViewById(R.id.messgoback);

        rtitle.setText(receiverusername);
        Picasso.with(getApplicationContext()).load(receiverimage).placeholder(R.drawable.profiledefault).into(rimage);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        messegetext = findViewById(R.id.messedittext);
        sendbtn = findViewById(R.id.messsend);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messegetext.getText().toString();
                if (!msg.equals("")) {
                    sendMessege(senderemail, receiveremail, msg);
                    messegetext.setText("");
                }
            }
        });
        readMessege(senderemail, receiveremail, receiverimage);


    }

    private void readMessege(String senderemail, String receiveremail, String receiverimage) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot current : snapshot.getChildren()) {
                    Chat chat = current.getValue(Chat.class);
                    if ((chat.getSender().equals(senderemail) && chat.getReceiver().equals(receiveremail)) ||
                            chat.getReceiver().equals(senderemail) && chat.getSender().equals(receiveremail)) {
                        chatList.add(chat);
                    }
                }


                adapter = new MessegeAdapter(getApplicationContext(), chatList, receiverimage);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessege(String senderemail, String receiveremail, String msg) {
        final Calendar cldr= Calendar.getInstance();
        Date times= cldr.getTime();
        Chat c = new Chat(senderemail, receiveremail, msg,times);
        databaseReference.push().setValue(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messege_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.chatInfo) {

        } else if (item.getItemId() == R.id.messFiles) {

        } else if (item.getItemId() == R.id.messTask) {

        }
        return true;
    }


}