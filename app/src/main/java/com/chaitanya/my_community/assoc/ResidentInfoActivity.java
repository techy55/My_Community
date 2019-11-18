package com.chaitanya.my_community.assoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.CustomAdapter;
import com.chaitanya.my_community.other.DataModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ResidentInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private SharedPreferences sharedPreferences;
    private String user="assoc";
    private Button view;

    String[] Month = {"241", "242", "243", "244", "245", "246"};
    String selectedMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_info);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.monthSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Month list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Month);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        view=(Button) findViewById(R.id.btnView);
        view.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedMonth = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    public void onClick(View view) {
        Toast.makeText(this,"Hello",Toast.LENGTH_LONG).show();
        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Owner")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            data.clear();
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                Map singleUser = (Map) entry.getValue();

                                if(singleUser.get("Flat No").toString().trim()!=null)
                                    if (singleUser.get("Flat No").toString().trim().equals(selectedMonth)) {
                                        data.add(new DataModel(
                                                singleUser.get("Name").toString().trim(),
                                                singleUser.get("Flat No").toString().trim(),
                                                singleUser.get("Phone").toString().trim()

                                        ));
                                    }
                            }
                            adapter = new CustomAdapter(data,user,"residentInfo");
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        adapter = new CustomAdapter(data,user,"residentInfo");
        recyclerView.setAdapter(adapter);
    }
}
