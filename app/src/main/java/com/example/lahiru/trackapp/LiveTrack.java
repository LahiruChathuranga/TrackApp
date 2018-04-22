package com.example.lahiru.trackapp;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lahiru.trackapp.NewFile.MainHistoryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lahiru on 3/15/2018.
 */

public class LiveTrack extends AppCompatActivity {

    ImageButton mMapViewBtn;
    ImageButton mSettingsBtn;
    private String hashEmail=null;
    private String deviceId=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livetrack);

        mMapViewBtn=(ImageButton)findViewById(R.id.mapbtn);
        mSettingsBtn=(ImageButton)findViewById(R.id.settingBtn);
        deviceId=getIntent().getExtras().getString("deviceId");
        hashEmail=getIntent().getExtras().getString("hashEmail");


        mMapViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LiveTrack.this,MapViewActivity.class);
                intent.putExtra("deviceId",deviceId);
                intent.putExtra("hashEmail",hashEmail);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LiveTrack.this,MainHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });





    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
