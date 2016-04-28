package dk.dtu.lbs.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationProvider;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.dto.RecordHistory;
import dk.dtu.lbs.fragments.TimePickerFragment;
import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.listeners.DateTimeListener;
import dk.dtu.lbs.services.DataTransferService;
import dk.dtu.lbs.utils.LocationUtils;
import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.wsclient.RESTClient;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class TestActivity extends BaseActivity implements View.OnClickListener{
    private String TAG=this.getClass().getName();
    private TextView statusTV = null;
    private TextView databaseTV=null;
    private Button startServiceBT = null;
    private Button stopServiceBT = null;
    private Button databaseBT=null;
    private String newLine=System.getProperty("line.separator");
    private MyLocationReceiver locationReceiver=null;
    private Timer timer=null;
    private Task task=null;
    private Intent  dataTransferIntent=null;
    private TrackerDataSource database=null;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer=new Timer();
        statusTV=(TextView)findViewById(R.id.statusTV);
        startServiceBT = (Button) findViewById(R.id.startService);
        startServiceBT.setOnClickListener(this);
        stopServiceBT = (Button) findViewById(R.id.stopService);
        stopServiceBT.setOnClickListener(this);
        statusTV.setText("Status text view....");

        databaseBT=(Button)findViewById(R.id.readDatabase);
        databaseBT.setOnClickListener(this);
        databaseTV=(TextView)findViewById(R.id.databaseTV);
        database=TrackerDataSource.getInstance(this.getApplicationContext());
        /**
        showToast("Service Running : "+AppUtil.isServiceRunning(this,"dk.dtu.locationtracker.gps.LocationServiceUsingGoogle"));
        if(AppUtil.isServiceRunning(this,"dk.dtu.locationtracker.gps.LocationServiceUsingGoogle")){
            startServiceBT.setEnabled(false);
        }else{
            stopServiceBT.setEnabled(true);
        }**/

          // createAndRegisterReceiver();
//        ConnectivityAndPowerService.register(this);
    }

    protected void onStart(){
        super.onStart();
       // createAndRegisterReceiver();

    }
    protected void onResume(){
        super.onResume();
        //createAndRegisterReceiver();
       // ConnectivityAndPowerService.register(this);

    }
    protected void onPause(){
       // unregisterReceiver();
        //ConnectivityAndPowerService.unregister(this);
        super.onPause();

    }
    protected void onStop(){
        //unregisterReceiver();
        //ConnectivityAndPowerService.unregister(this);
        super.onStop();

    }
    protected void onDestroy() {
        //unregisterReceiver();
        //ConnectivityAndPowerService.unregister(this);
        super.onDestroy();}


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.startService:
                /**
                Intent intent=new Intent(this,LocationServiceUsingGoogle.class);
                intent.putExtra(LocationUtils.PROVIDER_NAME, LocationManager.GPS_PROVIDER);

                startService(intent);
                startServiceBT.setEnabled(false);
                stopServiceBT.setEnabled(true);
                statusTV.setText("Starting Location Service"); **/
                //testPrefrence();
                //testAppUtil();
                //testSendLocations();
                //testingTimer();
                //showTimePicker();
                //showDateTimeDialog();
                //testInsertToRecordHistory();
                for(int i=0;i<12;i++){
                    database.insertLocation(new GeoCoordinate(i,i,i));
                }


                break;
            case R.id.stopService:
                //database.handleDatabaseFull();
                //database.deleteLocationInTimeInterval(1,4);
                /**
                stopService(new Intent(this,LocationServiceUsingGoogle.class));
                startServiceBT.setEnabled(true);
                stopServiceBT.setEnabled(false);
                statusTV.setText("Stopping Location Service");
                 **/
                //stopTimer();
                break;
            case R.id.readDatabase:
                //readDatabase();
                List<GeoCoordinate> loc=database.getAllLocations();
                statusTV.setText("Location list size: "+loc.size());
                break;

        }
    }
   public void readDatabase(){
       /*
       RealmResults<GeoCoordinate> result = LocalDatabase.getInstance().readAllGeoCoordinate(Realm.getInstance(this));
       StringBuilder sb=new StringBuilder();
       for(GeoCoordinate geo:result){
           sb.append(new Date(geo.getTime()).toString()).append(newLine)
           .append(geo.getLatitude()).append(newLine)
           .append(geo.getLongitude()).append(newLine)
           .append("===================");


       }
        */
      // databaseTV.setText(sb.toString());
       //sendHttpJsonRequest();
       //sendHttpStringRequest();
        //postUsingRetrofit();

   }

    public void testIsServiceRuning(){
      boolean isRunning=AppUtil.isServiceRunning(this,"dk.dtu.locationtracker.gps.LocationServiceUsingGoogle");
        showToast("Service id running: " + isRunning);
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public class MyLocationReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            StringBuilder sb=new StringBuilder();
            String action=intent.getAction();
            switch(action){
                case LocationUtils.LOCATION_CHANGED_KEY:
                    sb.append("Location changed: ")
                            .append(newLine)
                            .append("Provider: ").append(intent.getStringExtra(LocationUtils.PROVIDER_NAME)).append(newLine)
                            .append("Time: ").append(new Date(intent.getLongExtra(LocationUtils.TIME,-1)).toString()).append(newLine)
                            .append("Accuracy: ").append(intent.getFloatExtra(LocationUtils.ACCURACY,-1)).append(newLine)
                            .append("Speed: ").append(intent.getFloatExtra(LocationUtils.SPEED, -1)).append(newLine)
                            .append("Longitude: ").append(intent.getDoubleExtra(LocationUtils.LONGITUDE, -1)).append(newLine)
                            .append("Latitude: ").append(intent.getDoubleExtra(LocationUtils.LATITUDE, -1));
                    statusTV.setText(sb.toString());
                    break;

                case LocationUtils.PROVIDER_ON_OFF_KEY:
                    sb.append("Provider ")
                            .append(intent.getStringExtra(LocationUtils.PROVIDER_NAME))
                            .append(intent.getStringExtra(LocationUtils.PROVIDER_STATUS));
                    statusTV.setText(sb.toString());
                    break;
                case LocationUtils.PROVIDER_STATUS_CHANGED_KEY:
                    sb.append("Provide status change:")
                            .append(newLine)
                            .append("Name: ").append(intent.getStringExtra(LocationUtils.PROVIDER_NAME))
                            .append(newLine)
                            .append("Status: ");
                    int statusCode=intent.getIntExtra(LocationUtils.PROVIDER_STATUS_CODE,-1);
                    switch (statusCode){
                        case LocationProvider.AVAILABLE:
                            sb.append("Available");
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            sb.append("Temporarily unavailable");
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                            sb.append("Out of service");
                            break;
                        default:
                            sb.append("Strange status code: ").append(statusCode);
                            break;
                    }
                    statusTV.setText(sb.toString());
                    break;
                default:
                    statusTV.setText("Unknown action: "+action);
                    break;
            }
        }
    }
    private void showTimePicker(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment frag=new TimePickerFragment();
        frag.show(ft, "timepicker");
    }
    private void createAndRegisterReceiver() {
        locationReceiver = new MyLocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationUtils.LOCATION_CHANGED_KEY);
        filter.addAction(LocationUtils.PROVIDER_ON_OFF_KEY);
        filter.addAction(LocationUtils.PROVIDER_STATUS_CHANGED_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, filter);
    }
    private void unregisterReceiver() {
        if (locationReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(locationReceiver);
        }
        locationReceiver = null;
    }

    public void testPrefrence(){
        SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(this);
        String url=prfs.getString(getResources().getString(R.string.settings_default_url_key),"No url");
        statusTV.setText(url);
    }
    public void testAppUtil(){
        statusTV.setText("Network connected : " + AppUtil.isNetworkConnected(this) + " Mobile net: " + AppUtil.isMobilNet(this) + " Wifi net : " + AppUtil.isWifiNet(this));
    }

    public void testSendLocations(){
        String url=AppUtil.getBaseUrl(this);
        TrackerRestService client= RESTClient.getClient(this.getApplicationContext());
        List<GeoCoordinate> ls=new ArrayList<>();
        ls.add(new GeoCoordinate(12,12.1212,12.1212));
        ls.add(new GeoCoordinate(13,12.1212,12.1212));
        ls.add(new GeoCoordinate(14, 12.1212, 12.1212));
        Call<List<GeoCoordinate>> call= client.postUserLocations(215,ls);
        call.enqueue(new Callback<List<GeoCoordinate>>() {
            @Override
            public void onResponse(retrofit.Response<List<GeoCoordinate>> response, Retrofit retrofit) {
                statusTV.setText(response.message() + " " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                statusTV.setText(t.getMessage());
            }
        });



    }




    public void stopTimer(){
        stopService(dataTransferIntent);
        task.cancel();
    }
    public void testingTimer(){
        task=new Task();
           timer.scheduleAtFixedRate(task, 5000, 20000);
    }

    public class Task extends TimerTask{


        @Override
        public void run() {

            //testSendLocations();
            startDataTransferService();
        }
    }
    public void startDataTransferService(){
        long uid=10;
        dataTransferIntent=new Intent(this, DataTransferService.class);
        dataTransferIntent.putExtra("uid", uid);
        startService(dataTransferIntent);
    }
    public void showDateTimeDialog(){


        final DateTimeListener lis=new DateTimeListener();
        final Dialog dtd=new Dialog(this);
        AppUtil.prepareDateTimeDialog(dtd, lis, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTV.setText("Date Time : " + lis.getDay() + "-" + lis.getMonth() + "-" + lis.getYear() + "-" + lis.getHour() + ":" + lis.getMinute());
                dtd.dismiss();
            }
        });
        dtd.show();

    }
    public void testInsertToRecordHistory(){
       TrackerDataSource database= TrackerDataSource.getInstance(getApplicationContext());
        database.insertToRecordHistory(new RecordHistory(0, 123, 123));
        List<RecordHistory> ls=database.getAllRecordHistory();
        if(ls!=null){

        statusTV.setText("size of list : "+ls.size());
            for(RecordHistory h : ls){
                statusTV.append(Long.toString(h.getRid()));
            }

        }
    }



}
