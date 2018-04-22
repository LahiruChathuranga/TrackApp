package com.example.lahiru.trackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.annotation.Dimension.PX;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView mVehi_list;
    private DatabaseReference mDatabase;
    private FloatingActionButton add;
    private String hashEmail=null;
    private String deviceId=null;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences=getSharedPreferences("myPref",0);
        hashEmail=getIntent().getExtras().getString("hashEmail");
        deviceId=sharedPreferences.getString("deviceId","");
        mVehi_list=(RecyclerView)findViewById(R.id.vehi_list);
        mVehi_list.setHasFixedSize(true);
        mVehi_list.setLayoutManager(new LinearLayoutManager(this));
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Vehicle").child(hashEmail);
        add=(FloatingActionButton)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AddActivity.class);
                intent.putExtra("hashEmail",hashEmail);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Vehicle,VehicleViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder>(
                Vehicle.class,
                R.layout.vehicle_row,
                VehicleViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(final VehicleViewHolder viewHolder, final Vehicle model, int position) {




                    viewHolder.setVehiNum(model.getVehi_num());
                    viewHolder.setVehiModel(model.getVehi_model());
                    viewHolder.setImage(getApplicationContext(),model.getVehi_image());



                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent=new Intent(HomeActivity.this,DisplayActivity.class);
                            intent.putExtra("deviceId",deviceId);
                            intent.putExtra("hashEmail",hashEmail);
                            startActivity(intent);
                            overridePendingTransition(R.xml.fadein, R.xml.fadeout);



                        }
                    });

            }

        };
        mVehi_list.setAdapter(firebaseRecyclerAdapter);
    }
   public static class VehicleViewHolder extends RecyclerView.ViewHolder{
        View mView;


        public VehicleViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setVehiNum(String vehinum){
            TextView vehi_num=(TextView)mView.findViewById(R.id.vehi_num);
            vehi_num.setText(vehinum);


        }
        public void setVehiModel(String vehiModel){
            TextView vehi_model=(TextView)mView.findViewById(R.id.vehi_model);
            vehi_model.setText(vehiModel);
        }
        public void setImage(Context ctx,String image){
            ImageView vehi_image=(ImageView)mView.findViewById(R.id.vehi_image);
            if(image!=null)
            {
                Picasso.with(ctx).load(image).fit().noFade().into(vehi_image);
            }else
            {
                Picasso.with(ctx).load(image).fit().noFade().placeholder(R.drawable.placeholder).into(vehi_image);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add){
            startActivity(new Intent(HomeActivity.this,AddActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
