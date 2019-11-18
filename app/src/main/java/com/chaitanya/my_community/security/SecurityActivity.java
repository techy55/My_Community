package com.chaitanya.my_community.security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chaitanya.my_community.MainActivity;
import com.chaitanya.my_community.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SecurityActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secuity);

        Button go = (Button) findViewById(R.id.go_btn);
        final EditText Vname=(EditText) findViewById(R.id.visitor_name);
        final EditText flatNo=(EditText) findViewById(R.id.flatno);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Vname.getText().toString().trim().isEmpty())
                    Toast.makeText(getApplicationContext(),"Please Enter Visitor Name",Toast.LENGTH_LONG).show();
                else if(flatNo.getText().toString().trim().isEmpty())
                    Toast.makeText(getApplicationContext(),"Please Enter Flat No",Toast.LENGTH_LONG).show();
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Owner").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                boolean flag = false;

                                //while (!flag) {
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        //Get user map
                                        Map singleUser = (Map) entry.getValue();
                                        if(singleUser.get("Flat No").toString().trim()!=null)
                                        if (singleUser.get("Flat No").toString().trim().equals(flatNo.getText().toString().trim())) {
                                            flag = true;
                                            Intent i=new Intent(getApplicationContext(),securityDetailActivity.class);
                                            i.putExtra("vname",Vname.getText().toString().trim());
                                            i.putExtra("oname",singleUser.get("Name").toString().trim());
                                            i.putExtra("flatno",flatNo.getText().toString().trim());
                                            i.putExtra("phoneno",singleUser.get("Phone").toString().trim());
                                            i.putExtra("uid", singleUser.get("Uid").toString().trim());
                                            startActivity(i);
                                            finish();
                                            break;
                                        } else {
                                            flag=false;
                                        }
                                    }

                                    if(!flag)
                                        Toast.makeText(getApplicationContext(),"Please Enter Valid Flat No",Toast.LENGTH_LONG).show();
                               // }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back again3 to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
}
