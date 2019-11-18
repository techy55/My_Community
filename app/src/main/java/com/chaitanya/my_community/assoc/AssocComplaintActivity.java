package com.chaitanya.my_community.assoc;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AssocComplaintActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private TextView noComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assoc_complaint);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        noComplaints=(TextView) findViewById(R.id.noComplaints);

        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Complaints")
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
                                        ""
                                ));
                            }
                            adapter = new CustomAdapter(data,"assoc","Announce");
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            noComplaints.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        adapter = new CustomAdapter(data,"assoc","Announce");
        recyclerView.setAdapter(adapter);
    }
}
