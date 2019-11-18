package com.chaitanya.my_community.assoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.Announce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class PostAnnounceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText announceBox,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announce);

        announceBox = (EditText) findViewById(R.id.announce_text);
        title = (EditText) findViewById(R.id.announce_title);
        Button submitBtn = (Button) findViewById(R.id.post_announce);

        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child("Announcements")
                .child("" + new Random().nextInt(1000)).setValue(new Announce(title.getText().toString().trim(),announceBox.getText().toString().trim()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Custom AlertDialog
                            final AlertDialog.Builder Dialog = new AlertDialog.Builder(PostAnnounceActivity.this,R.style.CustomAlertDialog);
                            Dialog.setTitle("Announcement Posted")
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
