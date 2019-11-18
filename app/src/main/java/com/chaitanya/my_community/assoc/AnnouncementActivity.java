package com.chaitanya.my_community.assoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.CustomAdapter;
import com.chaitanya.my_community.other.DataModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AnnouncementActivity extends AppCompatActivity implements View.OnClickListener {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private SharedPreferences sharedPreferences;
    private TextView noNoti;
    private String user="";
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        noNoti = (TextView) findViewById(R.id.noNoti);
        addBtn = (Button) findViewById(R.id.addBtn);

        sharedPreferences = getSharedPreferences("userCheck", 0);

        if (sharedPreferences.getBoolean("assoc", false)) {
            user = "assoc";
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(this);
        } else
            user = "owner";

        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Announcements")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            data.clear();
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                Map singleUser = (Map) entry.getValue();
                                data.add(new DataModel(
                                        singleUser.get("Name").toString().trim(),
                                        singleUser.get("Desc").toString().trim()
                                ));
                            }
                            adapter = new CustomAdapter(data, user,"Announce");
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            noNoti.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        adapter = new CustomAdapter(data, user,"Announce");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, PostAnnounceActivity.class));
    }
}
