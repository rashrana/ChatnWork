package com.rash.chatnwork.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rash.chatnwork.LoginActivity;
import com.rash.chatnwork.MainActivity;
import com.rash.chatnwork.R;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class TasksFragment extends Fragment {
    public static final String PREF_NAME="WHATEVER";

    private  String curUsername;
    public TasksFragment() {

    }
    public TasksFragment(String curUserName){
        this.curUsername= curUserName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_tasks, container, false);
        Button b= v.findViewById(R.id.back);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", "false");
                databaseReference.updateChildren(map);
                FirebaseAuth.getInstance().signOut();

                Intent i=new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);

            }
        });
        return v;
    }
}