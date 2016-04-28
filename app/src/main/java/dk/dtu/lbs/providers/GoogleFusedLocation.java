package dk.dtu.lbs.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import dk.dtu.lbs.activities.R;

/**
 * Created by Bahram Moradi on 13-11-2015.
 */
public class GoogleFusedLocation  {
    protected static final String TAG ="GoogleFusedLocation";
    private  long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private  float MIN_DISPLACEMENT_INTERVAL =2;
    private long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    private Context context=null;
    private static  GoogleFusedLocation instance=null;
    private LocationListener locationListener=null;
    private  GoogleFusedLocation() {}
    public static  GoogleFusedLocation getInstance(){
        if (instance==null){
            synchronized (GoogleFusedLocation.class){
                if(instance==null){
                    instance=new GoogleFusedLocation();
                }
            }
        }
        return instance;
    }
    public void connectGoogleApiClient(){
        googleApiClient.connect();
    }
    public void disconnectGoogleApiClient(){
        googleApiClient.disconnect();
    }

    public synchronized void  buildGoogleApiClient(Context context,ConnectionCallbacks
            connectionCallbacks,OnConnectionFailedListener failedListener,
                                                   LocationListener locationListener) {
        this.context=context;
        this.locationListener=locationListener;
        Log.i(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    public void createLocationRequest() {
        readSettings();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public Location  getLastLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }
    public void startLocationUpdates() {
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
    }
    public void stopLocationUpdates() {
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }
    private void readSettings(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String key=context.getResources().getString(R.string.settings_location_update_time_interval_key);
        String defaultValue=context.getResources().getString(R.string.settings_location_update_time_interval_default_value);
        String updateInterval=preferences.getString(key, defaultValue);
        try{
            /*to milli seconds*/
            UPDATE_INTERVAL_IN_MILLISECONDS=Long.valueOf(updateInterval)*1000;

        }catch(Exception e){
            Log.d(TAG,"Exception: "+e.getMessage());

        };

        key=context.getResources().getString(R.string.settings_location_update_distance_interval_key);
        defaultValue=context.getResources().getString(R.string.settings_location_update_distance_interval_default_value);
        String dist=preferences.getString(key, defaultValue);
        try{

            MIN_DISPLACEMENT_INTERVAL =Long.valueOf(dist);

        }catch(Exception e){
            /*if exception occur the default value is used.*/
            Log.d(TAG,"Exception: "+e.getMessage());
        };




    }


}
