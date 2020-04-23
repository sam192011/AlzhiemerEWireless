package com.example.fall_dectection;

/**********************************************************
 *author:Haotian Liu   Haojue Wang
 *student number:s1917779   S1936286
 *
 *Description:In this activity, it mainly has the following functions
 *
 *            1.Use the googlemap instance to complete the initialization of the map user interface.
 *            2.The GETDIRECTION button is defined, and when it is detected that the user clicks on
 *              it, the AsyncTask FetchURL will be executed.
 *            3.Define the fetchLastLocation method and use the instance of FusedLocationProviderCl-
 *              ient to get the patient's current location.
 *            4.Define the getUrl method, which is used to generate the target network address.
 *
 * Added value by Haojue WANG:
 *            Set the destination address manually
 *            Enter the address postal code to realize the navigation function
 *
 * Layoutfile:activity_map.xml
 * */

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import com.example.fall_dectection.mapsdirection.FetchURL;
import com.example.fall_dectection.mapsdirection.TaskLoadedCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.LocationServices;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    Location currentLoction;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;////////
    private GoogleMap mMap;
    public static Location returnLocation;
    private double Curr_location_lat;
    private double Curr_location_long;

    public double home_location_lat;
    public double home_location_long;

    private Button Button_Get_dirction;
    private MarkerOptions det_marker,acc_Marker;
    private Polyline current_polyline;

    private EditText EditText_Home_Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        EditText_Home_Location = (EditText) findViewById(R.id.EditText_Home_Location);
        String location = EditText_Home_Location.getText().toString();
        Log.d("Location", location);


        SharedPreferenceActivity sharedPreferenceActivity = new SharedPreferenceActivity();
        String Data_address = sharedPreferenceActivity.return_Home_address();
        EditText_Home_Location.setText(Data_address);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Button_Get_dirction = (Button) findViewById(R.id.Button_Get_Direction);
        Button_Get_dirction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                GeoLocate_onCLick(View);
                String url = getUrl(acc_Marker.getPosition(), det_marker.getPosition(), "driving");
                new FetchURL(MapActivity.this).execute(getUrl(acc_Marker.getPosition(), det_marker.getPosition(),"driving"), "driving");
                //new FetchURL(MapActivity.this).execute(url,"driving");
            }
        });

        fetchLastLocation();
        //det_marker = new MarkerOptions().position(new LatLng(home_location_lat, home_location_long)).title("Old College");
        //acc_Marker = new MarkerOptions().position(new LatLng(55.9239, -3.1664)).title("Current Location");

    }

    public void GeoLocate_onCLick(View v) {
        mMap.clear();

        hideSoftKeyboard(v);

        String location = EditText_Home_Location.getText().toString();
        Log.d("Location", location);
        if(location == null){

            Toast.makeText(this, "Please Enter a Location", Toast.LENGTH_LONG).show();
        }
        Geocoder geocoder = new Geocoder(this);
        Log.d("Location", location);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(location, 1);
            Address address = list.get(0);
            home_location_lat = address.getLatitude();
            home_location_long = address.getLongitude();
            String locality = address.getFeatureName();
            Toast.makeText(this, "Go to " + locality, Toast.LENGTH_LONG).show();

            //set the finial address
            LatLng Destination_latLng = new LatLng(home_location_lat, home_location_long);
            mMap.addMarker(new MarkerOptions().position(Destination_latLng).title("Destination"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(Destination_latLng));

        } catch (IOException e) {
            e.printStackTrace();
        }
            det_marker = new MarkerOptions().position(new LatLng(home_location_lat, home_location_long)).title("Destination");
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }


    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    currentLoction = location;
                    returnLocation = location;

                    Curr_location_lat = currentLoction.getLatitude();
                    Curr_location_long =currentLoction.getLongitude();

                    acc_Marker = new MarkerOptions().position(new LatLng(Curr_location_lat, Curr_location_long)).title("Current Location");

                    Toast.makeText(getApplicationContext(), Curr_location_lat
                            + " " + Curr_location_long, Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().
                            findFragmentById(R.id.Fragment_Google_Map);
                    supportMapFragment.getMapAsync(MapActivity.this);

                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap GoogleMap) {
        mMap = GoogleMap;
        LatLng Curr_latLng = new LatLng(currentLoction.getLatitude(), currentLoction.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(Curr_latLng).title("You Here.");

        mMap.animateCamera(CameraUpdateFactory.newLatLng(Curr_latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Curr_latLng, 5));
        mMap.addMarker(markerOptions);

        //
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    /*
     * function:getUrl method is designed to get the destination network address with input parameter
     *
     * input parameter:
     * @Curr_latLng :current location
     * @Old_College_latLng: destination location
     * @direction :travel mode
     *
     * return parameter:
     * @url : destination network address which makes a requirement from google direction api
     *
     * */
    private String getUrl(LatLng Curr_latLng, LatLng Old_College_latLng, String direction) {
        // Origin of route
        String str_origin = "origin=" + Curr_latLng.latitude + "," + Curr_latLng.longitude;
        // Destination of route
        String str_dest = "destination=" + Old_College_latLng.latitude + "," + Old_College_latLng.longitude;
        // Mode
        String mode = "mode=" + direction;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.Map_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (current_polyline != null)
            current_polyline.remove();
        current_polyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}