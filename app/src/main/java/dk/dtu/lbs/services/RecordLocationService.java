package dk.dtu.lbs.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.dtu.lbs.activities.RecordLocationActivity;
import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.dto.Profile;
import dk.dtu.lbs.dto.RecordHistory;
import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.providers.GoogleFusedLocation;
import dk.dtu.lbs.scheduler.Task;
import dk.dtu.lbs.scheduler.Scheduler;
import dk.dtu.lbs.utils.LocationUtils;
import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.wsclient.RESTClient;
import dk.dtu.lbs.activities.R;

public class RecordLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static String TAG = RecordLocationService.class.getName();
    private GoogleFusedLocation fusedLocation = null;
    private long uid = 0;
    private static int failNr=0;
    private Context context;
    //private boolean isScheduled=false;
    private Intent dataTransferIntent=null;
    private boolean isConnected=false ;
    private boolean isWifi=false;
    private boolean isMobil=false;
    private int delay=5;   // 5 seconder
    private int interval=5;
    private int reset=0;
    private DataTransferTask task=null;
    private OnNetworkConnection connectivity=null;
    private TrackerDataSource database=null;
    private TrackerRestService restClient=null;
    private Scheduler scheduler=null;
    public static List<LatLng> buffer=null;
    private NotificationManager notificationManager=null;
    private int NOTIFICATION_REF=10;
    private boolean isLocationInserted=false;
    private long fromTime=0;
    private long toTime=0;
    public void onCreate() {
        logd("onCreate");
        fromTime=System.currentTimeMillis();
        context=this.getApplicationContext();
        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        connectivity=new OnNetworkConnection();
        buffer=new ArrayList<>();


        /*database */
        database=TrackerDataSource.getInstance(context);
        /*web service client */
        restClient= RESTClient.getClient(this.getApplicationContext());
        /* Location provider*/
        fusedLocation = GoogleFusedLocation.getInstance();
        fusedLocation.buildGoogleApiClient(this, this, this, this);
        fusedLocation.connectGoogleApiClient();
        registerOnNetworkConnection();
        /* scheduler */
        //List<Profile> profiles=LocalDatabase.getInstaonStartnce().readProfile(Realm.getInstance(this));
        List<Profile> profiles=database.readProfile();
        Profile profile=null;
        if(!profiles.isEmpty()){profile=profiles.get(0);}
        scheduler= Scheduler.getInstance();
        if(profile!=null){
            uid=profile.getUid();
            dataTransferIntent=new Intent(this, DataTransferService.class);
            dataTransferIntent.putExtra("uid",uid);

        }
        /*schedule */
        scheduleDataTransfer();




    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        logd("onStartCommand");
        showNotification();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
        logd("onDestroy");
        toTime=System.currentTimeMillis();
        /*if some locations are inserted in database then we need to update record history table*/
        if(isLocationInserted){
            database.insertToRecordHistory(new RecordHistory(0,fromTime,toTime));
        }
        notificationManager.cancel(NOTIFICATION_REF);
        fusedLocation.stopLocationUpdates();
        fusedLocation.disconnectGoogleApiClient();

        unregisterOnNetworkConnection();
        scheduler.cancelTask(task);
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle) {
        logd("onConnected");
        fusedLocation.startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        logd("Connection suspended");
        fusedLocation.connectGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
        logd("onLocationChanged");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        logd("onConnectionFailed");
        logd("onConnectionFailed" + connectionResult.getErrorMessage());

    }

    private void handleNewLocation(Location location) {

        addToBuffer(location);
        LocationUtils.broadcastLocation(this, location);
        GeoCoordinate geo=new GeoCoordinate(location.getTime(),location.getLongitude(),location.getLatitude());
        //LocalDatabase.getInstance().insertLocation(Realm.getInstance(this), geo);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String ldate=sdf.format(new Date(location.getTime()));
        String sdd=sdf.format(new Date());

        Log.d(TAG,"Locations Date :"+ldate);
        Log.d(TAG,"System Date : "+ sdd);
        database.insertLocation(geo);
        isLocationInserted=true;
    }
    private static synchronized void addToBuffer(Location loc){
        buffer.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
    }
    public static List<LatLng> getPointList(){
        return buffer;
    }




    /*The task would be scheduled if and only if the network connection is enabled */
    private void scheduleDataTransfer(){
        /*schedule */
        logd("Scheduling Data transfer Task");
        String unitKey=context.getResources().getString(R.string.settings_time_unit_key);
        String unitDefaultValue= context.getResources().getString(R.string.settings_time_unit_default_value);
        String timeIntervalKey= context.getResources().getString(R.string.settings_time_interval_sync_key);
        String timeIntervalDefaultValue=context.getResources().getString(R.string.settings_time_interval_sync_default_value);
        TimeUnit unit;
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String timeUnit=settings.getString(unitKey, unitDefaultValue);
        String sInterval=settings.getString(timeIntervalKey, timeIntervalDefaultValue);

        unit=timeUnit.equals(unitDefaultValue) ? TimeUnit.SECONDS: TimeUnit.MINUTES;
        try{
            interval=Integer.parseInt(sInterval);
            delay=interval;
        }catch (Exception e){
            logd(e.getMessage());
        }
        // if exception happened just use the default value

        logd("Time Unit: "+unit+" Delay: "+delay+" Interval: "+interval);


        task=new DataTransferTask(DataTransferTask.class.getName(),delay,interval,unit);
        if(AppUtil.isNetworkConnected(this)){
            scheduler.schedule(task);
        }




    }
    public void startDataTransferService(){
        if (dataTransferIntent!=null){
            startService(dataTransferIntent);
        }

    }


    private void registerOnNetworkConnection(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivity, filter);
    }
    private void unregisterOnNetworkConnection(){
        unregisterReceiver(connectivity);
    }

    private class OnNetworkConnection extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
            case ConnectivityManager.CONNECTIVITY_ACTION:
                isConnected=AppUtil.isNetworkConnected(context);
                isWifi=AppUtil.isWifiNet(context);
                isMobil=AppUtil.isMobilNet(context);
                /* when network is deactivated the we stop the scheduler*/
                if(!isConnected ){
                    scheduler.cancelTask(task);
                    logd("OnNetworkConnection/ Stop Schedule data transfer");
                }else{
                    scheduleDataTransfer();
                    logd("OnNetworkConnection/Schedule data transfer");
                }
                break;
            /* modify Intent Filter of receiver and add other listening cases here */
            }
        }
    }


    public void logd(String str) {
        Log.d(TAG, str);
    }


    public class DataTransferTask extends Task {

        private String name=null;
        private int delay=0;
        private int interval=0;
        private TimeUnit unit=TimeUnit.SECONDS;

        public DataTransferTask(String name, int delay, int interval, TimeUnit unit) {
            this.name = name;
            this.delay = delay;
            this.interval = interval;
            this.unit = unit;
        }


        @Override
        public void setName(String name) {
            this.name=name;
        }

        @Override
        public void setDelay(int delay) {
            this.delay=delay;
        }

        @Override
        public void setInterval(int interval) {
            this.interval=interval;
        }

        @Override
        public void setTimeUnit(TimeUnit unit) {
            this.unit=unit;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public int getInterval() {
            return interval;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return unit;
        }

        public void run(){

            startDataTransferService();

        }

    }





private void showNotification(){

    Intent intent=new Intent(this, RecordLocationActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    Notification.Builder builder= new Notification.Builder(this);
    builder.setContentTitle("Tracker");
    builder.setContentText("Tracker service is running");
    builder.setSmallIcon(R.mipmap.ic_notification_location_on);
    builder.setTicker("Tracker");
    builder.setWhen(System.currentTimeMillis());
    builder.setDefaults(Notification.DEFAULT_ALL);
    builder.setOngoing(true);
    builder.setContentIntent(pIntent);
    notificationManager.notify(NOTIFICATION_REF, builder.getNotification());




}


}
