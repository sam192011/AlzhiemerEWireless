package com.example.fall_dectection;

/**********************************************************
 *author: Haojue Wang
 *student number:  S1936286
 *
 *Description:In this activity, it mainly has the following functions
 *              1. Using the Accelerate , Gravity and  Magnetic sensor
 *              2. Use low-pass filter to improve sensor accuracy
 *              3. Call the system time, sum the data within 30 seconds to improve the test accuracy
 *              4. Use Intent to switch between different activities
 *              5.  CountDownTimer to trigger send mail function
 * Layoutfile:activity_main.xml
 * */

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.DialogInterface;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.example.fall_dectection.User;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //get access to sensors
    private SensorManager mSensorManager;
    //represent a sensor
    private Sensor aSensor;     //the acceleration sensor
    private Sensor mSensor;     //the magnitude sensor
    private Sensor gSensor;     //the gyproscope sensor

    private TextView mag_x;
    private TextView mag_y;
    private TextView mag_z;
    private TextView mag_h;
    private TextView mag_SMV_acc;
    private TextView mag_SMV_gyro;
    private TextView mag_Force;
    private TextView mag_Movement;
    private TextView mag_rotate;
    private TextView mag_pitch;
    private TextView mag_roll;
    private TextView steps;
    private double MagnitudePrevious = 0;
    private float prevZaxis = 0;
    private Integer stepCount = 0;

    //call SensorManager to get access the device's
    private float[] accelerometerValues = new float[3]; // create and store the data from the accelerate sensor
    private float[] magneticFieldValues = new float[3]; // create and store the data from the magnetic sensor
    private float[] gyproscopeValues = new float[3];    // create and store the data from the gyproscope sensor
    private float   rotateDegree = 0f;
    private float   pitchDegree = 0f;
    private float   rollDegree = 0f;
    private double  ACC = 0;
    private double  Average_ACC = 0;
    private double  GYRO = 0;
    private double  Average_GYRO = 0;
    private double  FORCE = 0;
    private double  Average_FORCE = 0;
    private float   last_x = 0;
    private float   last_y = 0;
    private float   last_z = 0;
    public  static  int     weight = 40 ;
    private double  threshold_force = 0;
    private static  final int UPDATE_INTERVAL = 100;     //1 second per unit
    private static  final int KEEP_TIME_SECOND = 30;
    private long    mLastUpdateTime;
    private int     time_trigger = 0;

    private float   acc_x = 0 ;
    private float   acc_y = 0 ;
    private float   acc_z = 0 ;
    private float   delta_x = 0 ;
    private float   delta_y = 0 ;
    private float   delta_z = 0 ;
    private double  sum_acc = 0 ;

    private float   gypro_x = 0 ;
    private float   gypro_y = 0 ;
    private float   gypro_z = 0 ;
    private float   gypro_delta_x = 0 ;
    private float   gypro_delta_y = 0 ;
    private float   gypro_delta_z = 0 ;
    private float   gypro_last_x = 0;
    private float   gypro_last_y = 0;
    private float   gypro_last_z = 0;
    private double   mag_delta = 0;
    private float   mag_avg_z_axis = 0;

    private Button Map_Button;
    private Button Email_Button;

    private Button Data_Button;

    static boolean Send_Email_flag = true;
    static boolean NeedHelp = true;
    public double tlat;
    public double tlong;
    private static String Address_name;
    public String email = "";
    public String phone = "";
    public String address = "";


    //location:
    TextView textLat;
    TextView textLong;
    LocationManager locationManager;
    LocationListener locationListener;
    String bestprovider;
    Criteria criteria;
    TextView textLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
            }
        }


        SharedPreferenceActivity sharedPreferenceActivity = new SharedPreferenceActivity();
        String Data_Weight = sharedPreferenceActivity.return_User_weight();
        email = sharedPreferenceActivity.return_Carer_email();
        phone = sharedPreferenceActivity.return_Carer_phone();
        address = sharedPreferenceActivity.return_Home_address();

        if(email == ""){
            Intent intent = new Intent(MainActivity.this, SharedPreferenceActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            weight = Integer.valueOf(Data_Weight).intValue();
            initializeFirebase(email, phone, address, weight);
        }


        //file to initial all the sensor and sensormanager data
        initialization();
        //file the calculate the degree
        calculateOrientation();
        Map_Button = (Button) findViewById(R.id.Map_Button);
        //as the photo button pushed,used Intent to start the Map activity
        Map_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent Map_intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(Map_intent);
            }
        });

        Email_Button = (Button) findViewById(R.id.Email_Button);
        //as the photo button pushed,used Intent to start the Map activity
        Email_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent Email_intent = new Intent(MainActivity.this,EmailActivity.class);
                startActivity(Email_intent);
            }
        });

        Data_Button = (Button) findViewById(R.id.Data_Button);
        //as the photo button pushed,used Intent to start the Map activity
        Data_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent Data_intent = new Intent(MainActivity.this,SharedPreferenceActivity.class);
                startActivity(Data_intent);
            }
        });

        //location:
        locatino_oncreat();
    }

    public void initializeFirebase(String email, String phone, String address, final int weight){
        final String initEmail = email.split("@")[0];
        final String storeEmail = email;
        final String initAddress = address;
        final String initPhone = phone;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(initEmail)) {
                    User user = new User(storeEmail, 0, 0, initAddress, weight, initPhone);
                    ref.child("users").child(initEmail).setValue(user);

                }
                else {
                    ref.child(initEmail).child("email").setValue(storeEmail);
                    ref.child(initEmail).child("phone").setValue(initPhone);
                    ref.child(initEmail).child("address").setValue(initAddress);
                    ref.child(initEmail).child("weight").setValue(weight);
                }
            }
            public void  onCancelled(DatabaseError dbError){

            }
        });
    }


    private void locatino_oncreat() {
        textLocation = (TextView) findViewById(R.id.text_location);
        textLat = (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);
        //locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationListener
        locationListener = new mylocationlistener();
        bestprovider = locationManager.getBestProvider(getcriteria(), true);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Open GPS", Toast.LENGTH_LONG).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }



    private Criteria getcriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("LOCATION", location.toString());
            if (location != null) {
                tlat = location.getLatitude();
                tlong = location.getLongitude();
                textLat.setText(Double.toString(tlat));
                textLong.setText(Double.toString(tlong));
                LatLng User_location = new LatLng(tlat,tlong);
                Address_name =  gecoding_latlng_to_location(User_location);
                textLocation.setText(Address_name);
            }
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
    }

    private String gecoding_latlng_to_location(LatLng user_location) {
        String address_name = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(user_location.latitude,user_location.longitude,1);
            String address = addresses.get(0).getAddressLine(0);
            address_name = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("ADDRESS", address_name);
        return  address_name;
    }


    // to do auto-generated meathod stub
    private void initialization() {
        mag_Movement = (TextView)findViewById(R.id.Movement);
        steps = findViewById(R.id.stepCount);
        //To enable sensors, data must be obtained through the SensorManager system service.
        //Get an instance of SensorManagar for accessing sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Determine a default sensor type, in this case is TYPE_MAGNETIC_FIELD
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Determine a default sensor type, in this case is TYPE_ACCELEROMETER
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Determine a default sensor type, in this case is TYPE_GYROSCOPE
        gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //add a low pass filter
        //Output = Parameter * Output + (1-Parameter) * Input;
        final float lowpasParameter = (float)0.95;

        //if the sensor detected the magnetic field
        if ((event.sensor.getType()) == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues[0] = lowpasParameter*magneticFieldValues[0] +(1-lowpasParameter)*event.values[0];
            magneticFieldValues[1] = lowpasParameter*magneticFieldValues[1] +(1-lowpasParameter)*event.values[1];
            magneticFieldValues[2] = lowpasParameter*magneticFieldValues[2] +(1-lowpasParameter)*event.values[2];
        }
        //if the sensor detected the accelerometer
        if ((event.sensor.getType()) == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues[0] = lowpasParameter*accelerometerValues[0] +(1-lowpasParameter)*event.values[0];
            accelerometerValues[1] = lowpasParameter*accelerometerValues[1] +(1-lowpasParameter)*event.values[1];
            accelerometerValues[2] = lowpasParameter*accelerometerValues[2] +(1-lowpasParameter)*event.values[2];
        }
        if ((event.sensor.getType()) == Sensor.TYPE_GYROSCOPE) {
            gyproscopeValues[0] = lowpasParameter*gyproscopeValues[0] +(1-lowpasParameter)*event.values[0];
            gyproscopeValues[1] = lowpasParameter*gyproscopeValues[1] +(1-lowpasParameter)*event.values[1];
            gyproscopeValues[2] = lowpasParameter*gyproscopeValues[2] +(1-lowpasParameter)*event.values[2];
        }

        ACC_calculateOrientation(event);
        calculateOrientation();

    }


    private void ACC_calculateOrientation(SensorEvent event) {

        //Calling the acceleration sensor
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Trigger every 30 seconds
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - mLastUpdateTime;
            if (diffTime < UPDATE_INTERVAL) {
                time_trigger++;
                return;
            }
            mLastUpdateTime = currentTime;

            //read the sensor value from the SensorEvent
            acc_x = event.values[0];
            acc_y = event.values[1];
            acc_z = event.values[2];
            delta_x = acc_x - last_x;
            delta_y = acc_y - last_y;
            delta_z = acc_z - last_z;
            sum_acc = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            mag_delta = sum_acc - MagnitudePrevious;
            mag_avg_z_axis = acc_z - prevZaxis;
            prevZaxis = acc_z;
            MagnitudePrevious = sum_acc;
            if (mag_delta > 6 ){
                stepCount++;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final String initEmail;
                if(email != "")
                   initEmail = email.split("@")[0];
                else
                    initEmail = "";
                final DatabaseReference ref = database.getReference("users/"+initEmail);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int prevStep = snapshot.getValue(User.class).steps;
                        steps.setText(prevStep+" Steps Taken");
                        ref.child("steps").setValue(prevStep + 1);

                    }
                    public void  onCancelled(DatabaseError dbError){
                        Log.w("DBERROR", "loadPost:onCancelled", dbError.toException());
                    }
                });

            }

            last_x = acc_x;
            last_y = acc_y;
            last_z = acc_z;

            ACC = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2) + Math.pow(delta_z, 2));
            //GYRO = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_z, 2));
            FORCE = 2 * weight * sum_acc / 9.81;

            //When less than 30 seconds, the data is continuously superimposed
            if((time_trigger >= 0) && (time_trigger< KEEP_TIME_SECOND)){
                Average_ACC = Average_ACC + ACC;
                Average_GYRO = Average_GYRO + GYRO;
                //Average_FORCE = Average_FORCE + FORCE;
            }else{
                time_trigger = 0;
                Average_ACC = Average_ACC / KEEP_TIME_SECOND;
                Average_GYRO = Average_GYRO / KEEP_TIME_SECOND;
                //Average_FORCE = Average_FORCE / KEEP_TIME_SECOND;
            }

        }

        //Calling the gravity sensor
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Toast.makeText(this,"turn on",Toast.LENGTH_LONG).show();
            //read the sensor value from the SensorEvent
            gypro_x = event.values[0];
            gypro_y = event.values[1];
            gypro_z = event.values[2];
            gypro_delta_x = gypro_x - gypro_last_x ;
            gypro_delta_y = gypro_y - gypro_last_y ;
            gypro_delta_z = gypro_z - gypro_last_z ;

            gypro_last_x = gypro_x;
            gypro_last_y = gypro_y;
            gypro_last_z = gypro_z;

            GYRO = Math.sqrt(Math.pow(gypro_delta_x,2) + Math.pow(gypro_delta_z,2));
        }
        Display_Movement();
    }

    //This module is used to check the movement  state
    private void Display_Movement() {

        //mag_Movement.setText("Movement:"+ACC);
        //mag_SMV_acc.setText("SMV_acc:"+(float)(Math.round(delta_x*1000))/1000);
        //mag_SMV_gyro.setText("SMV_gyro:"+(float)(Math.round(delta_y*1000))/1000);
        //mag_Force.setText("Force:"+(float)(Math.round(delta_z*1000))/1000);

        if(weight >= 40 && weight <= 49){
            threshold_force = 100.8;
        }else if (weight >= 50 && weight <= 59){
            threshold_force = 122.28;
        }else if (weight >= 60) {
            threshold_force = 143.25;
        }

        if ((Average_ACC >=0 && Average_ACC< 0.3) && (Average_GYRO >=0 && Average_GYRO < 0.01)){
            mag_Movement.setText("Not Moving");
        }
        else if ((Average_ACC >= 0.35 && Average_ACC < 0.66) && (Average_GYRO >=0.01 && Average_GYRO < 0.07)){
            mag_Movement.setText("Movement Detected");
        }
        else if ((Average_ACC >=0.66 && Average_ACC < 4.26) && (Average_GYRO >=0.07 && Average_GYRO < 0.26)){
            mag_Movement.setText("Constantly Moving");
        }
        else if ((Average_ACC >=4.26 && Average_ACC < 5) && (Average_GYRO >=0.26 && Average_GYRO < 0.3)){
            mag_Movement.setText("Over Threshold !!");
        }
        else if ((mag_avg_z_axis > 8 || mag_avg_z_axis < -8) && (Average_GYRO >=2) && (FORCE >= threshold_force)){
            mag_Movement.setText("Falling Down!!");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Fall Detected! Do you need help?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    NeedHelp = true;
                    if (Send_Email_flag) {
                        Send_Email_flag = false;
                        FAll_Down_Email_Send_Timer.start();
                    }
                    dialog.dismiss();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    NeedHelp = false;
                    dialog.dismiss();
                    break;
            }
        }
    };

    //This module is used to count down 30 seconds and trigger the function of sending emails.
    private CountDownTimer FAll_Down_Email_Send_Timer = new CountDownTimer(20000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            final Toast toast = Toast.makeText(getApplicationContext(),"Email wil be sent in 20 seconds!",Toast.LENGTH_SHORT);
            toast.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            toast.cancel();
                        }
                    },
                    3000);
        }
        @Override
        public void onFinish() {
            //Set the Serializable
                Intent_data_share intent_data_share = new Intent_data_share(tlat, tlong, Address_name);
                //Switch to the Email Activity module
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                intent.putExtra("Fall_Down_Trigger", 30);
                //send the data from email activity by using Serializable
                intent.putExtra("intent_share", intent_data_share);
                startActivity(intent);
                Send_Email_flag = true;
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //file to calculate the orientation
    private void calculateOrientation() {
        //float the matrix to store the value frm the SensorManager.getOrientation
        float[] values = new float[3];
        //float the matrix to store the Radian value
        float[] R = new float[9];

        //The rotation matrix can be computed by that the tilt matrix R obtained from the acceleration sensor,
        //and the rotation matrix I obtained from the magnetic field sensor.
        //values [0]: azimuth, which represents the angle between the Y axis of the device and the north pole.
        //North: azimuth = 0 	South: azimuth = π	West: azimuth = -π/2	East: azimuth =  π/2
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        //Converting radians to angles
        //values [1] and values [2] respectively represent angle of Pitch and Roll,
        //which are not used in the compass.
        rotateDegree = (float) Math.toDegrees(values[0]);
        pitchDegree = (float) Math.toDegrees(values[1]);
        rollDegree = (float) Math.toDegrees(values[2]);

    }


    @Override
    protected void onPause() {
        super.onPause();
        //register the sensor when user returns to activity
        //When the system's data delay is set to SENSOR_DELAY_FASTEST at this time,
        //a lot of resources are consumed. So the sensor needs to be turned off in “onPause ()”
        mSensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //disable the sensor
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (aSensor != null) {
            mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (gSensor != null) {
            mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_UI);
        }

        //disable the location sensor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(bestprovider, 0, 0, locationListener);
    }
}


