package com.chaitanya.my_community.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.CustomAdapter;
import com.chaitanya.my_community.other.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DuesActivity extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private TextView noDues;
    private String user="";
    private String amount="Rs.1200/-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dues);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        noDues=(TextView) findViewById(R.id.noDues);
        user= FirebaseAuth.getInstance().getUid()+"=true";

        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Maintenance").orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        data.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds:dataSnapshot.getChildren()) {
                                if(!ds.getValue().toString().contains(user))
                                {
                                    data.add(new DataModel(
                                            ds.getKey(),amount,"",""
                                    ));
                                }
                            }

                            if(!data.isEmpty())
                            {
                                adapter = new CustomAdapter(data,"owner","Dues");
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else
                                noDues.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        /*adapter = new CustomAdapter(data,user);
        recyclerView.setAdapter(adapter);*/
    }
}
