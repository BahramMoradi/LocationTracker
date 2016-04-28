package dk.dtu.lbs.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import android.location.Location;

/**
 * Created by Bahram on 07-11-2015.
 */
public class LocationUtils {

    public static final String LOCATION_CHANGED_KEY="dk.dtu.locationtracker_LOCATION_CHANGED";
    public static final String PROVIDER_ON_OFF_KEY="dk.dtu.locationtracker_PROVIDER_CHANGED";
    public static final String PROVIDER_STATUS_CHANGED_KEY ="dk.dtu.locationtracker_STATUS_CHANGED";
    public static final String LATITUDE="LATITUDE";
    public static final String LONGITUDE="LONGITUDE";
    public static final String PROVIDER="PROVIDER";
    public static final String TIME="TIME";
    public static final String SPEED="SPEED";
    public static final String ACCURACY="ACCURACY";

    public static final String PROVIDER_STATUS="PROVIDER_STATUS";
    public static final String PROVIDER_NAME="PROVIDER_NAME";
    public static final String PROVIDER_STATUS_CODE="PROVIDER_STATUS_CODE";



    public static void showToast(Context context,String message,int duration){
        Toast.makeText(context,message,duration).show();
    }
    /* Broadcasting location information */
    public static void broadcastLocation(Context context, Location location){

        Intent broadcastIntent = new Intent(LocationUtils.LOCATION_CHANGED_KEY);
        broadcastIntent.putExtra(PROVIDER_NAME,location.getProvider());
        broadcastIntent.putExtra(TIME,location.getTime());
        broadcastIntent.putExtra(SPEED,location.getSpeed());
        broadcastIntent.putExtra(ACCURACY,location.getAccuracy());
        broadcastIntent.putExtra(LONGITUDE, location.getLongitude());
        broadcastIntent.putExtra(LATITUDE, location.getLatitude());
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);


    }
}
