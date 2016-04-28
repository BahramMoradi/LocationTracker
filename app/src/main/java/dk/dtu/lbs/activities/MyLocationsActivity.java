package dk.dtu.lbs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.fragments.FromToDateFragment;
import dk.dtu.lbs.interfaces.OnSubmitListener;
import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.map.MapHelper;
import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.wsclient.RESTClient;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MyLocationsActivity extends BaseActivity  implements OnMapReadyCallback, View.OnClickListener, OnSubmitListener {
    private GoogleMap mMap=null;
    private ImageButton myLocationsBT = null;
    private ImageButton findMyLocationBT = null;
    private ImageButton deleteAllLocationsBT = null;
    private ImageButton deleteLocationsInIntervalBT= null;
    private final String TAG=MyLocationsActivity.class.getName();
    private Context context=null;
    private MapHelper mapHelper=null;
    private int showDateTimeCallerId=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_location);
        myLocationsBT = (ImageButton) findViewById(R.id.myLocations);
        myLocationsBT.setOnClickListener(this);
        findMyLocationBT = (ImageButton) findViewById(R.id.findMylocation);
        findMyLocationBT.setOnClickListener(this);
        deleteAllLocationsBT=(ImageButton)findViewById(R.id.deleteAllLocations);
        deleteAllLocationsBT.setOnClickListener(this);
        deleteLocationsInIntervalBT=(ImageButton)findViewById(R.id.deleteInTimeInterval);
        deleteLocationsInIntervalBT.setOnClickListener(this);

        context=this.getApplicationContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.findMayLocationMap);
        mapFragment.getMapAsync(this);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_my_locations);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myLocations:
                findLocations(true,0,0);
                break;
            case R.id.findMylocation:
                showDateTimeCallerId=R.id.findMylocation;
                showDateTimeDialog();
                break;
            case R.id.deleteAllLocations:
                deleteLocation(true, 0, 0);
                break;
            case R.id.deleteInTimeInterval:
                showDateTimeCallerId=R.id.deleteInTimeInterval;
                showDateTimeDialog();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mapHelper=new MapHelper(mMap);
    }

    /***
     * This method finds all or location in interval form <code>from</code> and to <code> to </code>
     * @param findAllLocation : boolean : if true , all location is retrieved from backend, else
     *                        value of <code> from</code> and <code>to </code> interval is used.
     * @param from long : from date
     * @param to to : to date
     */
    public void findLocations(boolean findAllLocation, long from, long to) {
        if(!AppUtil.isNetworkConnected(this)){
            AppUtil.showNoNetworkDialog(this);
            return;
        }
        long uid = TrackerDataSource.getInstance(this.getApplicationContext()).getUserId();
        if (uid == 0) {
            showDialog();
        } else {
            TrackerRestService client = RESTClient.getClient(this.getApplicationContext());
            Call<List<GeoCoordinate>> call =null;
            if(findAllLocation){
                call=client.getUserLocations(uid);
            }
            else{
                call=client.getUserLocationsInTimeInterval(uid, from, to);
            }
            call.enqueue(new Callback<List<GeoCoordinate>>() {
                public void onResponse(Response<List<GeoCoordinate>> response, Retrofit retrofit) {
                    int code = response.code();
                    String msg = response.message();
                    if (code == 200) {
                        List<GeoCoordinate> locations = response.body();
                        mapHelper.drawLocationsOnMap(locations);

                    }
                    //handle other HTTP status code here later.
                    else {
                        Toast.makeText(context, "Code:" + code + " Messsage : " + msg, Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFailure(Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void showDateTimeDialog() {
        FromToDateFragment dialog = new FromToDateFragment();
        dialog.setOnSubmitListener(this);
        dialog.show(getFragmentManager(), "FromToDateFragment");


    }

    @Override
    public void onSubmit(int btID, long fromDateTime, long toDateTime) {
        if (btID == R.id.okSetTimeBT) {
            Log.d(TAG, "t1 : " + fromDateTime + " t2 : " + toDateTime);
            //Toast.makeText(context, "t1 : " + fromDateTime + " t2 : " + toDateTime, Toast.LENGTH_SHORT).show();
            if(showDateTimeCallerId==R.id.findMylocation){
            findLocations(false,fromDateTime, toDateTime);
            }
            if(showDateTimeCallerId==R.id.deleteInTimeInterval){
                deleteLocation(false,fromDateTime,toDateTime);
            }



        }
    }

    /**
     *
     * @param deleteAllLocations : if true all location would be deleted else the location between
     * time interval "from" and "to"
     * @param from :start time
     * @param to : end time
     */
    public void deleteLocation(final boolean deleteAllLocations, long from, long to){
        if(!AppUtil.isNetworkConnected(this)){
            AppUtil.showNoNetworkDialog(this);
            return;
        }
        long uid = TrackerDataSource.getInstance(this.getApplicationContext()).getUserId();
        if (uid == 0) {
            showDialog();
        } else {
            TrackerRestService client = RESTClient.getClient(this.getApplicationContext());
            Call<GeoCoordinate> call =null ;
            if(deleteAllLocations) {
                call=client.deleteUserLocations(uid);
            }else{
                call=client.deleteUserLocationsInTimeInterval(uid, from, to);
            }
            call.enqueue(new Callback<GeoCoordinate>() {
                public void onResponse(Response<GeoCoordinate> response, Retrofit retrofit) {
                    int code = response.code();
                    String msg = response.message();
                    if (code == 200) {
                        Toast.makeText(context, "All your locations are deleted", Toast.LENGTH_SHORT).show();
                        if(deleteAllLocations&&mapHelper!=null){
                            mapHelper.clearMap();

                        }

                    }
                    //handle other HTTP status code here later.
                    else {
                        Toast.makeText(context, "Code:" + code + " Messsage : " + msg, Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFailure(Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User Profile");

        // set dialog message
        alertDialogBuilder
                .setMessage("You should create a user profile!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        startActivity(new Intent(context, ProfileActivity.class));
                        dialog.dismiss();


                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
