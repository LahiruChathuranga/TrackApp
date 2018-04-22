package com.example.lahiru.trackapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Lahiru on 3/15/2018.
 */

public class History extends AppCompatActivity {
    private Button track;
    private String deviceId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        deviceId=getIntent().getExtras().getString("deviceID");



        track=(Button)findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(History.this,MapsActivity.class);
                intent.putExtra("deviceId",deviceId);
                startActivity(intent);

            }
        });
    }
}
