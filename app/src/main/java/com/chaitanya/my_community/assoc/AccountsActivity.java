package com.chaitanya.my_community.assoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaitanya.my_community.R;
import com.chaitanya.my_community.other.DataModel;
import com.chaitanya.my_community.owner.PayMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AccountsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] Month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    String selectedMonth = "";
    TextView amount,dues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.monthSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Month list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Month);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedMonth = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void viewAcc(View view) {
        FirebaseDatabase.getInstance().getReference().child("Main").child("Users")
                .child("Maintenance").child(selectedMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    amount=(TextView) findViewById(R.id.textTotalAmt);
                    dues=(TextView) findViewById(R.id.duesAmt);
                    int total=(int)(dataSnapshot.getChildrenCount())*1200;
                    int duesAmt=(1200*400)-total;
                        amount.setText(total+"");
                        dues.setText(duesAmt+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
