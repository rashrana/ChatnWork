package com.rash.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rash.chatnwork.Adapters.AddFriendAdapter;
import com.rash.chatnwork.Adapters.UserAdapter;
import com.rash.chatnwork.Model.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText search;
    private RecyclerView recyclerView;
    private AddFriendAdapter adapter;
    private List<UserAccount> users;
    private List<UserAccount> friends, incoming, sent;
    private UserAccount current;
    private DatabaseReference databaseReference;
    private String curUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        curUsername= getIntent().getStringExtra("curUsername");
        toolbar= findViewById(R.id.addfToolBar);
        setSupportActionBar(toolbar);

        users= new ArrayList<>();
        friends= new ArrayList<>();
        incoming= new ArrayList<>();
        sent= new ArrayList<>();

        search= findViewById(R.id.addfsearch);
        recyclerView= findViewById(R.id.addfrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);


        readUsers();
        getFriendsList();
        getIncomingList();
        getSentList();


    }

    private void getSentList() {
        databaseReference= FirebaseDatabase.getInstance().getReference(curUsername).child("Request Sent");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sent.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                        sent.add(user);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai",error.getMessage()+"activity4");
            }
        });
    }

    private void getIncomingList() {
        databaseReference= FirebaseDatabase.getInstance().getReference(curUsername).child("Incoming Request");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incoming.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                        incoming.add(user);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai",error.getMessage()+"activity3");
            }
        });
    }

    private void getFriendsList() {
        databaseReference= FirebaseDatabase.getInstance().getReference(curUsername).child("Friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for(DataSnapshot selected: snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                        friends.add(user);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai",error.getMessage()+"activity2");
            }
        });

    }

    private void readUsers() {
        Log.d("oval","1");
        String currentuser= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot selected: snapshot.getChildren()){
                    UserAccount user = selected.getValue(UserAccount.class);
                    user.setKey(selected.getKey());
                    if(!user.getEmail().equals(currentuser)){
                        users.add(user);
                    }else{
                        current=user;
                    }
                }
                adapter= new AddFriendAdapter(getApplicationContext(),users,friends,sent,incoming,current,AddFriendActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai",error.getMessage()+"activity1");
            }
        });
    }
}