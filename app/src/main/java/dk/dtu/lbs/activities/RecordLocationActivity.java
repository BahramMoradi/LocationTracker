package dk.dtu.lbs.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import dk.dtu.lbs.interfaces.OnSubmitListener;
import dk.dtu.lbs.map.MapHelper;
import dk.dtu.lbs.services.RecordLocationService;
import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.utils.LocationUtils;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecordLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, OnSubmitListener {
    private GoogleMap mMap = null;
    private Polyline route = null;
    private ImageButton recordBT = null;
    private AnimationDrawable recordAnim = null;
    private Context context = null;
    private String TAG = "RecordLocationActivity";
    private LocationBroadcastReceiver locationReceiver = null;
    private MapHelper mapHelper = null;


    protected void onCreate(Bundle savedInstanceState) {
        /*showing indeterminate progress for loading map , it must be called before adding any content to the activity */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_location);
        context = this.getApplicationContext();
        createButtons();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if(savedInstanceState==null){
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);


    }

    public void createButtons() {
        recordBT = (ImageButton) findViewById(R.id.startLocationService);
        recordBT.setOnClickListener(this);
        if (AppUtil.isServiceRunning(this, "dk.dtu.lbs.services.RecordLocationService")) {
            recordBT.setBackgroundResource(R.drawable.blink);
            recordAnim = (AnimationDrawable) recordBT.getBackground();
            recordAnim.start();

        } else {
            recordBT.setBackgroundResource(R.mipmap.ic_record_location_one);
        }




    }

    protected void onResume() {
        super.onResume();
        createAndRegisterReceiver();
    }

    protected void onPause() {
        unregisterReceiver();
        super.onPause();

    }

    protected void onStop() {
        unregisterReceiver();
        super.onStop();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_record);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "onclick called", Toast.LENGTH_SHORT).show();
        if(!AppUtil.isPlayServiceAvailable(this.getApplicationContext())){
            AppUtil.showDialog(this,"No Play Service","Please download Google Play Service");
        }
        switch (v.getId()) {
            case R.id.startLocationService:
                toggleRecordButton();
                break;

        }


    }

    @Override
    public void onSubmit(int viewId, long fromDateTime, long toDateTime) {
        if (viewId == R.id.okSetTimeBT) {
            Log.d(TAG, "t1 : " + fromDateTime + " t2 : " + toDateTime);
            Toast.makeText(context, "t1 : " + fromDateTime + " t2 : " + toDateTime, Toast.LENGTH_SHORT).show();


        }


    }

    /*Broadcast receiver class for receiving location information*/
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case LocationUtils.LOCATION_CHANGED_KEY:
                    //setUpMap(intent.getDoubleExtra(LocationUtils.LATITUDE, -1),intent.getDoubleExtra(LocationUtils.LONGITUDE, -1));

                    if(mapHelper!=null&&AppUtil.isMarker(context)){
                        mapHelper.drawMarker(intent.getDoubleExtra(LocationUtils.LATITUDE, -1),intent.getDoubleExtra(LocationUtils.LONGITUDE, -1));
                    }
                    else{
                        if(mapHelper!=null){
                            mapHelper.updateRoute();
                        }


                    }
                    break;
            }
        }
    }

    private void createAndRegisterReceiver() {
        locationReceiver = new LocationBroadcastReceiver();
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

    private void toggleRecordButton() {
        if (AppUtil.isServiceRunning(this, "dk.dtu.lbs.services.RecordLocationService")) {
            stopService(new Intent(this, RecordLocationService.class));
            recordAnim.stop();
            recordBT.setBackgroundResource(R.mipmap.ic_record_location_one);
        } else {
            startService(new Intent(this, RecordLocationService.class));
            recordBT.setBackgroundResource(R.drawable.blink);
            recordAnim = (AnimationDrawable) recordBT.getBackground();
            recordAnim.start();

        }
    }

    public void onMapReady(GoogleMap map) {
        setProgressBarIndeterminateVisibility(false);
        mMap=map;
        mapHelper = new MapHelper(map);
    }

    private void setUpMap(double lat, double lon) {

        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Lon: " + lon + " Lat: " + lat));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 18));
        }
    }







}
