package com.example.lahiru.trackapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText mVehi_num;
    private EditText mVehi_model;
    private EditText mDate;
    private EditText mDistance;
    private Button mSubmit;
    private ImageButton mEditNumber;
    private ImageButton mEditModel;
    private ImageButton mSetDate;
    private ImageButton mClearDis;
    private List<Double> disList=new ArrayList<>();
    static final int DIALOG_ID=0;
    int year_x,month_x,day_x;
    String pDate;
    String vehiNum;
    double dis;
    Context context;
    private ProgressDialog mProgress;
    DatabaseReference mUser;
    private String deviceId=null;
    private String hashEmail=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDate=(EditText)findViewById(R.id.date);
        mVehi_model=(EditText)findViewById(R.id.vehi_model);
        mVehi_num=(EditText)findViewById(R.id.vehi_num);
        mDistance=(EditText)findViewById(R.id.distance);
        mSubmit=(Button)findViewById(R.id.submit);
        mEditModel=(ImageButton)findViewById(R.id.editModel);
        mEditNumber=(ImageButton)findViewById(R.id.editNumber);
        mSetDate=(ImageButton)findViewById(R.id.setDate);
        mClearDis=(ImageButton)findViewById(R.id.clearDis);
        mProgress=new ProgressDialog(this);
        mProgress.setCancelable(false);



        deviceId=getIntent().getExtras().getString("deviceId");
        hashEmail=getIntent().getExtras().getString("hashEmail");
        Log.d("DeviceId",deviceId);
        Log.d("hashemail",hashEmail);
        mUser=FirebaseDatabase.getInstance().getReference("Vehicle").child(hashEmail);
        mUser.child(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vehiNum=dataSnapshot.child("vehi_num").getValue(String.class);
                mVehi_num.setText(dataSnapshot.child("vehi_num").getValue(String.class));
                mVehi_model.setText(dataSnapshot.child("vehi_model").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEditNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mVehi_num.setEnabled(true);


            }
        });
        mEditModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVehi_model.setEnabled(true);



            }
        });
        mClearDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("This Will Erase Your Ride History at this day.").setCancelable(false);
                builder.setTitle("Warning!!!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHistoy();
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                        .show();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num=mVehi_num.getText().toString();
                String model=mVehi_model.getText().toString();
                Log.d("NUM",num);
                Log.d("MODEL",model);

                if(!TextUtils.isEmpty(num) && !TextUtils.isEmpty(model))
                {
                    mUser.child(deviceId).child("vehi_num").setValue(num);
                    mUser.child(deviceId).child("vehi_model").setValue(model);

                }
            }
        });
        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);

        showDialogOnButtonClick();



    }
    public void showDialogOnButtonClick(){
        mSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==DIALOG_ID)
        {
            return  new DatePickerDialog(this,dpickerListner,year_x,month_x,day_x);
        }
        return null;
    }
    private  DatePickerDialog.OnDateSetListener dpickerListner
            =new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            pDate = year_x + "0" + month_x + "0" + day_x;
            mDate.setText(pDate);
            //loadHistory();
        }
    };



   /* private void loadHistory(){
        mProgress.setMessage("Searching History");
        mProgress.show();
        String url="http://frozentrack.000webhostapp.com/getDetails.php?vehinum="+vehiNum+"&date="+pDate+"";
        Log.d("URL",url);

        StringRequest stringRequest=new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray vehicle=new JSONArray(response);
                            for(int i=0;i<vehicle.length();i++){
                                JSONObject vehicleObject=vehicle.getJSONObject(i);
                                double dis=vehicleObject.getDouble("distance");
                                disList.add(i,dis);

                            }
                            if(disList.isEmpty())
                            {
                                mProgress.dismiss();
                                final Snackbar mySnackbar=Snackbar.make(findViewById(R.id.settings),"No Records..!",Snackbar.LENGTH_SHORT);
                                mySnackbar.getView().setBackgroundColor(R.color.cardview_light_background);
                                        mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mySnackbar.dismiss();

                                            }
                                        }).setActionTextColor(R.color.red);
                                mySnackbar.show();
                            }else
                            {
                                dis=0;
                                for(int j=0;j<disList.size();j++)
                                {

                                    dis=dis+disList.get(j);
                                }
                                mProgress.dismiss();
                                double acc=dis/1000;
                                String dist=String.format("%.2f",acc);

                                mDistance.setText(dist+"Km");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }*/
    public void deleteHistoy(){
        String url="http://frozentrack.000webhostapp.com/delete.php?vehinum="+vehiNum+"&date="+pDate+"";
        Log.d("URL",url);

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),"Successfully Deleted",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }



}
