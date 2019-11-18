package com.chaitanya.my_community.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaitanya.my_community.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ComplaintsActivity extends AppCompatActivity implements android.view.View.OnClickListener {

    private EditText cp_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        cp_box = (EditText) findViewById(R.id.complaint_text_box);
        Button submitBtn = (Button) findViewById(R.id.submit_complaint);

        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Complaints")
                .child("" + new Random().nextInt(1000));
                ref.child("User").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.child("Name").setValue(cp_box.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Custom AlertDialog
                            final AlertDialog.Builder Dialog = new AlertDialog.Builder(ComplaintsActivity.this,R.style.CustomAlertDialog);
                            Dialog.setTitle("Your Complaint is Posted")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }
}
