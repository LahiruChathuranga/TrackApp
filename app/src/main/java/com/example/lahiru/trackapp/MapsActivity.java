package com.example.lahiru.trackapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.lahiru.trackapp.Remote.IGoogleApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.android.volley.*;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener,RoutingListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private DatabaseReference mUsers;
    Marker marker;
    private String vehinum=null;
    double latitude;
    double longitude;
    private Handler handler;
    private LatLng startPosition,endposition;
    private int index,next;
    private PolylineOptions polylineOptions,blackPolylineOptions;
    private Polyline blackPolyline,greyPolyline;
    private List<LatLng> polylineList;

    private float v;
    private double lat,lng;
    private String des="kandy";
    private double distance;
    IGoogleApi mService;
    private double newDis;
    private String vehi_num;
    private String vehi_model;
    LatLng destination;
    LatLng start;
    LatLng loc;


    private String server_url="https://frozentrack.000webhostapp.com/details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);
         mService=Common.getGoogleApi();
        destination=new LatLng(7.2906,80.6337);
        start=new LatLng(latitude,longitude);




        polylineList=new ArrayList<>();

        vehinum=getIntent().getExtras().getString("vehi_num");
        mUsers= FirebaseDatabase.getInstance().getReference().child("Vehicle");


       mUsers.child(vehinum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    vehi_num=dataSnapshot.child("vehi_num").getValue(String.class);
                    vehi_model=dataSnapshot.child("vehi_model").getValue(String.class);
                    Point value=dataSnapshot.child("Location").getValue(Point.class);
                    latitude=value.getLatitude();
                    longitude=value.getLongitude();

                }catch (Exception e)
                {
                    Point point=new Point(9.6615,80.0255);
                    mUsers.child(vehinum).child("Location").setValue(point);
                    Point value=dataSnapshot.child("Location").getValue(Point.class);
                    latitude=value.getLatitude();
                    longitude=value.getLongitude();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s:dataSnapshot.getChildren()) {


                    // Point point=s.getValue(Point.class);
                    loc = new LatLng(33.8688, 151.2093);
                    final LatLng location = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(location)
                            .zoom(17)
                            .bearing(30)
                            .tilt(45)
                            .build()));


                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(MapsActivity.this)
                            .alternativeRoutes(false)
                            .waypoints(start, destination)
                            .build();
                    routing.execute();



                }


                                };







            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






public void moveCar(){
    try {


        //adjusting Bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latlng : polylineList)
            builder.include(latlng);
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(JointType.ROUND);
        polylineOptions.addAll(polylineList);
        greyPolyline = mMap.addPolyline(polylineOptions);


        blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.width(10);
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(JointType.ROUND);
        blackPolylineOptions.addAll(polylineList);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);


        mMap.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size() - 1)));

        //smooth


        //Animator
        final ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);


        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                List<LatLng> points = greyPolyline.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        //add car
        marker = mMap.addMarker(new MarkerOptions().position(loc).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.caradd)));


        //car moving


        handler = new Handler();
        index = -1;
        next = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < polylineList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < polylineList.size() - 1) {
                    startPosition = polylineList.get(index);
                    endposition = polylineList.get(next);

                }
                final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        v = valueAnimator.getAnimatedFraction();
                        // lng=v*endposition.longitude+(1-v) * startPosition.longitude;
                        //lat=v*endposition.latitude+(1-v) * startPosition.latitude;
                        final LatLng newPos = latLngInterpolator.interpolate(v, startPosition, endposition);

                        //new location
                        // final LatLng newPos=new LatLng(lat,lng);;
                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(getBearing(startPosition, newPos));
                        //marker.setTitle("Vehicle-01");
                        //marker.setSnippet("Model:"+vehi_model+"|"+"Number:"+vehi_num);
                        marker.setFlat(true);
                        marker.setVisible(true);
                        marker.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPos)
                                .zoom(15.5f)
                                .bearing(90)
                                .tilt(45)
                                .build()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));


                        //get distance


                        Location l1 = new Location("");
                        l1.setLatitude(latitude);
                        l1.setLongitude(longitude);

                        Location l2 = new Location("");
                        l2.setLatitude(newPos.latitude);
                        l2.setLongitude(newPos.longitude);

                        distance = l1.distanceTo(l2);

                        //get date
                        final String date = getDateTime();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url, new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                final Snackbar mySnackbar = Snackbar.make(findViewById(R.id.map), "Error", Snackbar.LENGTH_SHORT);
                                mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mySnackbar.dismiss();

                                    }
                                });
                                mySnackbar.show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("vehi_num", vehi_num);
                                params.put("vehi_model", vehi_model);
                                params.put("lat", Double.toString(newPos.latitude));
                                params.put("lng", Double.toString(newPos.longitude));
                                params.put("date", date);
                                params.put("dis", Double.toString(distance));
                                return params;
                            }
                        };
                        MySingleton.getInstance(MapsActivity.this).addTorequestque(stringRequest);

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("Vehicle").child(vehinum).child("Location");
                        Point point = new Point(newPos.latitude, newPos.longitude);
                        user.setValue(point);


                    }
                });

                valueAnimator.start();
                handler.postDelayed(this, 3000);
            }
        }, 3000);


    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private float getBearing(LatLng start, LatLng end) {
        double lat=Math.abs(start.latitude - end.latitude);
        double lng=Math.abs(start.longitude - end.longitude);
        if (start.latitude < end.latitude && start.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (start.latitude >= end.latitude && start.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (start.latitude >= end.latitude && start.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (start.latitude < end.latitude && start.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dLat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dLng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);
        double a = sin(dLat / 2) * sin(dLat / 2)
                + cos(toRadians(lat1))
                * cos(toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {

        double l1 = toRadians(lat1);
        double l2 = toRadians(lat2);
        double g1 = toRadians(lng1);
        double g2 = toRadians(lng2);

        double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        return Math.round(dist * 6378100);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(MapsActivity.this)
                .alternativeRoutes(false)
                .waypoints(start, destination)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

        for(LatLng point : arrayList.get(0).getPoints())
            polylineList.add(point);
            moveCar();


    }

    @Override
    public void onRoutingCancelled() {

    }

    //smooth hanmdler
    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    public String getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        String dist = distance + " M";

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + " KM";
        }
        return dist;
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private double newDistance(double dis,double newDis)
    {

        double result=dis+newDis;
        return result;




    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
