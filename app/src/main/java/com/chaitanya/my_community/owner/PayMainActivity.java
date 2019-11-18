package com.chaitanya.my_community.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.chaitanya.my_community.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PayMainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] Month = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    String selectedMonth="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_main);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.monthSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Month list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Month);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedMonth=adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void payMain(View view)
    {
        LayoutInflater payment_view = LayoutInflater.from(this);
        View paymentMode = payment_view.inflate(R.layout.paymentmodes, null);

        AlertDialog.Builder builder=new AlertDialog.Builder(PayMainActivity.this, R.style.CustomAlertDialog)
                .setView(paymentMode)
                .setCancelable(false)
                .setTitle("Select Payment Method")
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Main").child("Users")
                                .child("Maintenance").child(selectedMonth).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(PayMainActivity.this,"Payment Made Succesfully",Toast.LENGTH_LONG).show();
                                        }else
                                        {
                                            String error=task.getException().getMessage();

                                            if(error.equals("already exists"))
                                                Toast.makeText(PayMainActivity.this,"Payment Failed because you have already Paid or because of Network Error",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(PayMainActivity.this,"Payment Cancelled",Toast.LENGTH_LONG).show();
                    }
                });

        builder.create().show();
    }
}
