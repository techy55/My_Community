package com.chaitanya.my_community.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
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
import java.util.Map;

public class ViewVisitorsActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private TextView nohistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitors);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        nohistory = (TextView) findViewById(R.id.noHistory);

        data = new ArrayList<DataModel>();

        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Visitors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("Date")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        data.clear();
                        if(dataSnapshot.exists()) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                Map singleUser = (Map) entry.getValue();

                                if(singleUser.get("Date")!=null) {
                                    data.add(new DataModel(
                                            singleUser.get("Name").toString().trim(),
                                            singleUser.get("Status").toString().trim(),
                                            singleUser.get("Date").toString().trim()
                                    ));
                                }
                                else
                                {
                                    nohistory.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                            adapter = new CustomAdapter(data);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else
                        {
                            nohistory.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);*/
    }
}
