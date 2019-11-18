package com.chaitanya.my_community.security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.VisitorDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

public class securityDetailActivity extends AppCompatActivity {

    private Button call,allow,dnt_allow;
    private Intent i;
    private String uid,flatno,vname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_securitydetail);

        i = getIntent();

        final String phone=i.getStringExtra("phoneno");
        uid=i.getStringExtra("uid");
        flatno=i.getStringExtra("flatno");
        vname=i.getStringExtra("vname");

        final TextView Toname = (TextView) findViewById(R.id.dummy);
        final TextView Tflatno = (TextView) findViewById(R.id.dummyf);
        TextView Tvname = (TextView) findViewById(R.id.dummyV);
        Tvname.setText(vname);
        Toname.setText(i.getStringExtra("oname"));
        Tflatno.setText(flatno);

        call = (Button) findViewById(R.id.call_btn);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == 0) {
                allow = (Button) findViewById(R.id.allow_btn);
                dnt_allow = (Button) findViewById(R.id.dont_allow_btn);
                call.setVisibility(View.INVISIBLE);
                allow.setVisibility(View.VISIBLE);
                dnt_allow.setVisibility(View.VISIBLE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult

    public void close(View v)
    {
        final DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Main").child("Users");
        switch (v.getId())
        {
            case R.id.allow_btn:
                VisitorDetails vd=new VisitorDetails(vname,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),"Allowed");
                        db.child("Visitors").child(uid).child(""+new Random().nextInt(1000))
                        .setValue(vd).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(), SecurityActivity.class));
                            finish();
                        }
                    }
                });
                break;
            case R.id.dont_allow_btn:
                VisitorDetails vd1=new VisitorDetails(vname,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),"Not Allowed");
                db.child("Visitors").child(uid).child(""+new Random().nextInt(1000))
                        .setValue(vd1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(), SecurityActivity.class));
                            finish();
                        }
                    }
                });
                break;
        }
    }
}
