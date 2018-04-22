package com.example.lahiru.trackapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    Button mSetDate;
    private MapFragment map;
    private GoogleMap mMap;
    Marker marker;
    private String vehinum,vehiNum;
    private Point newPoint;
    private String pDate;
    static final int DIALOG_ID=0;
    private List<LatLng> polylineList=new ArrayList<>();
    int year_x,month_x,day_x;
    private ProgressDialog mProgress;
    DatabaseReference mUser= FirebaseDatabase.getInstance().getReference("Vehicle");
    List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(10f));




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        mSetDate=(Button)findViewById(R.id.setDate);

        map=(MapFragment)getFragmentManager().findFragmentById(R.id.mapid);
        map.getMapAsync(this);
        vehinum=getIntent().getExtras().getString("vehi_num");
        mProgress=new ProgressDialog(this);
        mProgress.setCancelable(false);
        mUser.child(vehinum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vehiNum=dataSnapshot.child("vehi_num").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            year_x=year;
            month_x=month+1;
            day_x=dayOfMonth;
            pDate=year_x+"0"+month_x+"0"+day_x;
            loadHistory();





        }

    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



    }
    private void loadHistory(){
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
                                double lat=vehicleObject.getDouble("lat");
                                double lang=vehicleObject.getDouble("lang");
                                newPoint=new Point(lat,lang);
                                LatLng loc=new LatLng(newPoint.getLatitude(),newPoint.getLongitude());
                                polylineList.add(i,loc);



                            }
                            Log.d("LOC",Integer.toString(polylineList.size()));

                          if(polylineList.isEmpty())
                            {
                                mProgress.dismiss();
                                final Snackbar mySnackbar=Snackbar.make(findViewById(R.id.mapview),"No Records for this date!!",Snackbar.LENGTH_INDEFINITE);
                                mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mySnackbar.dismiss();

                                    }
                                });
                                mySnackbar.show();
                            }else
                            {
                                PolylineOptions polylineOptions=new PolylineOptions();
                                polylineOptions.addAll(polylineList);
                                polylineOptions.width(50f);
                                polylineOptions.color(Color.rgb(0, 178, 255));
                                Polyline polyline=mMap.addPolyline(polylineOptions);

                                List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(10f));
                                polyline.setPattern(pattern);
                                for(int i=0;i<polylineList.size();i++){
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(polylineList.get(i))
                                            .zoom(12)
                                            .build();
                                    Log.d("LOC",polylineList.get(i).toString());
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                                mProgress.dismiss();
                            }


                          /* if(polylineList.isEmpty())
                            {
                                mProgress.dismiss();
                                //Toast.makeText(getApplicationContext(),"No Records for this date",Toast.LENGTH_SHORT).show();
                                final Snackbar mySnackbar=Snackbar.make(findViewById(R.id.mapview),"No Records for this date!!",Snackbar.LENGTH_INDEFINITE);
                                mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mySnackbar.dismiss();

                                    }
                                });
                                mySnackbar.show();

                            }else
                            {
                                for(int j=0;j<polylineList.size()-1;j++){
                                    mProgress.setMessage("Searching History");
                                    mProgress.show();
                                    LatLng loc=polylineList.get(j);
                                    String address=getCompleteAddressString(loc.latitude,loc.longitude);
                                    marker=mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    marker.setTitle("Adress");
                                    marker.setSnippet(address );
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(loc)
                                            .zoom(15.5f)
                                            .bearing(90)
                                            .tilt(45)
                                            .build()));

                                }
                                mProgress.dismiss();
                            }*/

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
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {
               //
            }
        } catch (Exception e) {
            e.printStackTrace();
            //
        }
        return strAdd;
    }
    public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {

        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {



                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                String add=addresses+","+city+","+state+","+knownName;
                return add;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
