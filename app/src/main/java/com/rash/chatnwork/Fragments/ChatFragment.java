package com.rash.chatnwork.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rash.chatnwork.Adapters.ChatlistAdapter;
import com.rash.chatnwork.Adapters.UserAdapter;
import com.rash.chatnwork.Model.Chat;
import com.rash.chatnwork.Model.UserAccount;
import com.rash.chatnwork.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatlistAdapter adapter;
    private List<UserAccount> users;

    private FirebaseUser fuser;
    private DatabaseReference dref;
    private List<String> userlist;
    private List<Chat> lastmsglist;
    private String curUsername;
    public ChatFragment() {
    }
    public ChatFragment(String curUsername){
        this.curUsername= curUsername;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView= v.findViewById(R.id.chatrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        userlist= new ArrayList<>();
        lastmsglist=new ArrayList<>();
        dref= FirebaseDatabase.getInstance().getReference("Chats");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for(DataSnapshot cur: snapshot.getChildren()){
                    Chat chat= cur.getValue(Chat.class);
                    if(chat.getSender().equals(fuser.getEmail())){
                        userlist.add(chat.getReceiver());

                    }
                    if(chat.getReceiver().equals(fuser.getEmail())){
                        userlist.add(chat.getSender());

                    }
                    if(lastmsglist.size()!=0){
                        boolean found= false;
                        for(Chat ch: lastmsglist){
                            if((ch.getSender().equals(chat.getSender()) && ch.getReceiver().equals(chat.getReceiver())) ||
                                    (ch.getSender().equals(chat.getReceiver()) && ch.getReceiver().equals(chat.getSender()))){
                                lastmsglist.remove(ch);
                                lastmsglist.add(chat);
                                found=true;
                                break;
                            }
                        }
                        if(!found){
                            lastmsglist.add(chat);
                        }
                    }
                    else{
                        lastmsglist.add(chat);
                    }
                }
                Collections.reverse(lastmsglist);
                readChats();
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }

    private void readChats() {

        users= new ArrayList<>();
        dref= FirebaseDatabase.getInstance().getReference("Users");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot cur: snapshot.getChildren()){
                    UserAccount user= cur.getValue(UserAccount.class);
                    for(String id: userlist){
                        if(user.getEmail().equals(id)){
                            if(users.size()!=0){
                                boolean flag=false;
                                for(UserAccount u: users){
                                    if(u.getEmail().equals(id)){
                                        flag=true;
                                        break;
                                    }
                                }
                                if(!flag){
                                    users.add(user);
                                }
                            }
                            else{
                                users.add(user);
                            }
                        }

                    }
                }
                adapter= new ChatlistAdapter(getContext(),users,lastmsglist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}