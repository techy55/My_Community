package com.chaitanya.my_community.common;

import androidx.annotation.NonNull;
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

public class ServicesDetailActivity extends AppCompatActivity {

    private Intent i;
    private  TextView noService;
    private String service="";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private Button addBtn;
    private String user="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_detail);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        i=getIntent();

        noService=(TextView) findViewById(R.id.noEvents);

        service=i.getStringExtra("service");
        user=i.getStringExtra("user");

        /*if(user.equals("assoc"))
        {
            Button add=(Button) findViewById(R.id.addBtn);
            add.setVisibility(View.VISIBLE);
        }else
        {*/
            FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Services").child(service)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                data.clear();
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    Map singleUser = (Map) entry.getValue();
                                    data.add(new DataModel(
                                            singleUser.get("Provider Name").toString().trim(),
                                            singleUser.get("Available").toString().trim(),
                                            singleUser.get("Contact").toString().trim(),
                                            singleUser.get("Work Hours").toString().trim(),
                                            ""
                                    ));
                                }
                                adapter = new CustomAdapter(data,user,"Services");
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                noService.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        //}
    }
}
