package com.example.lahiru.trackapp;

import android.app.TabActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TabHost;

public class DisplayActivity extends TabActivity {
    private String deviceId=null;
    private String hashEmail=null;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        sharedPreferences=getSharedPreferences("myPref",0);

        deviceId=sharedPreferences.getString("deviceId","");
        hashEmail=getIntent().getExtras().getString("hashEmail");


        TabHost tabHost=(TabHost)findViewById(android.R.id.tabhost);
        TabHost.TabSpec spec;
        Intent intent;


        spec=tabHost.newTabSpec("History");
        spec.setIndicator("HISTORY");
        intent=new Intent(this,LiveTrack.class);
        intent.putExtra("deviceId",deviceId);
        intent.putExtra("hashEmail",hashEmail);
        spec.setContent(intent);
        tabHost.addTab(spec);


        spec=tabHost.newTabSpec("Track");
        spec.setIndicator("TRACK");
        intent=new Intent(this,History.class);
        intent.putExtra("deviceId",deviceId);
        intent.putExtra("hashEmail",hashEmail);
        spec.setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);



    }
}
