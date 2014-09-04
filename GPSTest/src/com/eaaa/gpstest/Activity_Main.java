package com.eaaa.gpstest;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class Activity_Main extends Activity {

    private static final int MINTIME = 500; // milliseconds
    private static final float DISTANCE = 2; // meters

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Criteria criteria;

    //SharedPreference statics
    private static final String MY_PREF = "myLocation";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ACCURACY = "accuracy";
    private static final String TIME = "time";

    private SharedPreferences sharedPreferences;

    private GestureDetector mGestureDetector;

    private TextView txtLatitude, txtLongitude;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Ensure that the screen stays on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //initial sharedPreferences
        sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_MULTI_PROCESS);
        
        setContentView(R.layout.activity_main);

        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);

        
        Log.d("Activity_GPS", "SETUP_START");
        mGestureDetector = createGestureDetector(this);
        setupLocation();
        Log.d("Activity_GPS", "SETUP_DONE");
    }

    private void testPrefference(){
        if(sharedPreferences.contains(LATITUDE)){
            Log.d("Activity_GPS", "LATITUDE: " + sharedPreferences.getString(LATITUDE, "NULL"));
        }
        
        if(sharedPreferences.contains(LONGITUDE)){
            Log.d("Activity_GPS", "LONGITUDE: " + sharedPreferences.getString(LONGITUDE, "NULL"));
        }

        if(sharedPreferences.contains(ACCURACY)){
            Log.d("Activity_GPS", "ACCURACY: " + sharedPreferences.getFloat(ACCURACY, -1));
        }
        
        if(sharedPreferences.contains(TIME)){
            Log.d("Activity_GPS", "TIME: " + sharedPreferences.getLong(TIME,-1));
        }
    }

    private void setupLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //TODO: estimate usefulness
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d("Activity_GPS", "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Activity_GPS", "onLocationChanged");
                //Log.d("Activity_GPS", "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
                
                updatePreferences(location); // for testing purpose
                
                Log.d("Activity_GPS", "" + System.currentTimeMillis());
                Log.d("Activity_GPS", "preferenceupdated");
                txtLatitude.setText("" + location.getLatitude());
                txtLongitude.setText("" + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("Activity_GPS", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("Activity_GPS", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Activity_GPS", "onProviderDisabled");
            }
        };

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(true);

        List<String> providers = mLocationManager.getProviders(criteria, true);

        for (String provider : providers) {
            mLocationManager.requestLocationUpdates(provider, MINTIME, DISTANCE, mLocationListener);
        }
    }
    
    private void updatePreferences(Location location){
    	SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LONGITUDE, "" + location.getLongitude());
        editor.putString(LATITUDE, "" + location.getLatitude());
        editor.putFloat(ACCURACY, location.getAccuracy());
        editor.putLong(TIME, System.currentTimeMillis());
        editor.commit();
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    updateLocation();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private void updateLocation() {
    	testPrefference();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

}