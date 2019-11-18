package com.chaitanya.my_community.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chaitanya.my_community.R;

public class ServicesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button ispBtn, plumberBtn, pestBtn, shopBtn, elecBtn;
    private SharedPreferences sharedPreferences;
    private String user="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        sharedPreferences=getSharedPreferences("userCheck",0);

        ispBtn = (Button) findViewById(R.id.isp);
        plumberBtn = (Button) findViewById(R.id.plumbing);
        shopBtn = (Button) findViewById(R.id.shop);
        elecBtn = (Button) findViewById(R.id.electrician);
        pestBtn = (Button) findViewById(R.id.pest_ctrl);

        if (sharedPreferences.getBoolean("assoc",false))
        {
            user="assoc";
        }else
            user="owner";

        ispBtn.setOnClickListener(this);
        plumberBtn.setOnClickListener(this);
        shopBtn.setOnClickListener(this);
        elecBtn.setOnClickListener(this);
        pestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.isp:
                start("isp");
                break;
            case R.id.plumbing:
                start("plumbing");
                break;
            case R.id.shop:
                start("shop");
                break;
            case R.id.electrician:
                start("elec");
                break;
            case R.id.pest_ctrl:
                start("pest");
                break;
        }
    }

    private void start(String service)
    {
        Intent i =new Intent(this, ServicesDetailActivity.class);
        i.putExtra("user",user);
        i.putExtra("service",service);
        startActivity(i);
    }
}
