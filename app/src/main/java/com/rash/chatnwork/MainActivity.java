package com.rash.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rash.chatnwork.Adapters.UserAdapter;
import com.rash.chatnwork.Fragments.AccountFragment;
import com.rash.chatnwork.Fragments.ChatFragment;
import com.rash.chatnwork.Fragments.ContactsFragment;
import com.rash.chatnwork.Fragments.TasksFragment;
import com.rash.chatnwork.Model.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    //android:background="?android:attr/windowBackground"
    private Toolbar chatToolbar, taskToolbar, contactToolbar, profileToolbar;
    private BottomNavigationView bottomNavigationView;
    private ImageView cimage, timage, coimage;

    private String email, curprofileurl, curUsername;
    private UserAccount current;
    private Button searchnew,logout,addchat,addtask;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            email = firebaseAuth.getCurrentUser().getEmail();

        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference(email);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot selected : snapshot.getChildren()) {

                    UserAccount user = selected.getValue(UserAccount.class);
                    user.setKey(selected.getKey());
                    if (user.getEmail().equals(email)) {
                        current=user;
                        curUsername = user.getUsername();
                        curprofileurl = user.getImageurl();
                        Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(cimage);
                        Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(timage);
                        Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(coimage);
                        break;
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatToolbar = findViewById(R.id.chatToolBar);
        taskToolbar = findViewById(R.id.taskToolBar);
        contactToolbar = findViewById(R.id.contactsToolBar);
        profileToolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(chatToolbar);

        taskToolbar.setVisibility(View.INVISIBLE);
        contactToolbar.setVisibility(View.INVISIBLE);
        profileToolbar.setVisibility(View.INVISIBLE);


        cimage = chatToolbar.findViewById(R.id.cimage);
        timage = taskToolbar.findViewById(R.id.timage);
        coimage = contactToolbar.findViewById(R.id.coimage);

        searchnew = contactToolbar.findViewById(R.id.search_newpeople);
        logout= profileToolbar.findViewById(R.id.logout);
        addtask= taskToolbar.findViewById(R.id.addtask);
        addchat= chatToolbar.findViewById(R.id.addchat);
        searchnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                i.putExtra("curUsername", curUsername);
                startActivity(i);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status("false");
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });



        bottomNavigationView = findViewById(R.id.bottomnavbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment(curUsername)).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected_frag = null;
                switch (item.getItemId()) {
                    case R.id.chat:
                        chatToolbar.setVisibility(View.VISIBLE);
                        taskToolbar.setVisibility(View.INVISIBLE);
                        contactToolbar.setVisibility(View.INVISIBLE);
                        profileToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(chatToolbar);
                        selected_frag = new ChatFragment(curUsername);
                        break;
                    case R.id.task:
                        taskToolbar.setVisibility(View.VISIBLE);
                        chatToolbar.setVisibility(View.INVISIBLE);
                        contactToolbar.setVisibility(View.INVISIBLE);
                        profileToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(taskToolbar);
                        selected_frag = new TasksFragment(curUsername);
                        break;
                    case R.id.contact:
                        contactToolbar.setVisibility(View.VISIBLE);
                        taskToolbar.setVisibility(View.INVISIBLE);
                        chatToolbar.setVisibility(View.INVISIBLE);
                        profileToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(contactToolbar);
                        selected_frag = new ContactsFragment(curUsername);
                        break;
                    case R.id.account:
                        profileToolbar.setVisibility(View.VISIBLE);
                        chatToolbar.setVisibility(View.INVISIBLE);
                        taskToolbar.setVisibility(View.INVISIBLE);
                        contactToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(profileToolbar);
                        selected_frag = new AccountFragment(current);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected_frag).commit();
                return true;
            }
        });


    }

    public void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("true");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("false");
        }
    }
}