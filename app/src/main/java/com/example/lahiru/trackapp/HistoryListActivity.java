package com.example.lahiru.trackapp;

 import android.app.DatePickerDialog;
import android.app.Dialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.icu.util.TimeZone;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryListActivity extends AppCompatActivity {
    private Button mDate;
    private String vehinum,vehiNum;
    private String pDate;
    static final int DIALOG_ID=0;
    int year_x,month_x,day_x;
    DatabaseReference mUser= FirebaseDatabase.getInstance().getReference("Vehicle");
    private String URL="https://192.168.137.1/TrackApp/getDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        mDate=(Button)findViewById(R.id.dateBtn);
        vehinum=getIntent().getExtras().getString("vehi_num");
        mUser.child(vehinum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vehiNum=dataSnapshot.child("vehi_num").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadHistory();
        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);

        showDialogOnButtonClick();


    }
    public void showDialogOnButtonClick(){
        mDate.setOnClickListener(new View.OnClickListener() {
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
            year_x=year;
            month_x=month+1;
            day_x=dayOfMonth;
            pDate=year_x+""+month_x+""+day_x;

            Toast.makeText(HistoryListActivity.this,pDate,Toast.LENGTH_SHORT).show();


        }

    };
    private void loadHistory(){

        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray vehicle=new JSONArray(response);
                            for(int i=0;i<vehicle.length();i++){
                                JSONObject vehicleObject=vehicle.getJSONObject(i);
                                String vehi_num=vehicleObject.getString("vehi_num");
                                String vehi_model=vehicleObject.getString("vehi_model");
                                String lat=vehicleObject.getString("lat");
                                String lang=vehicleObject.getString("lang");
                                String distance=vehicleObject.getString("distance");
                                String date=vehicleObject.getString("date");
                                VehicleList vehiList=new VehicleList(vehi_num,vehi_model,lat,lang,distance,date);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("vehi_num",vehiNum);
                params.put("date",pDate);
                return super.getParams();
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
