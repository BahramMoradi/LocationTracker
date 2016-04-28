package dk.dtu.lbs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Calendar;

import dk.dtu.lbs.activities.HelpActivity;
import dk.dtu.lbs.activities.MyLocationsActivity;
import dk.dtu.lbs.activities.RecordHistoryActivity;
import dk.dtu.lbs.activities.RecordLocationActivity;
import dk.dtu.lbs.activities.SettingsActivity;
import dk.dtu.lbs.activities.ProfileActivity;
import dk.dtu.lbs.activities.TestActivity;
import dk.dtu.lbs.listeners.DateTimeListener;
import dk.dtu.lbs.activities.R;

/**
 * Created by Bahram on 21-12-2015.
 */
public class AppUtil {
    private static String TAG = "AppUtil";

    /**
     *
     * @param context
     * @param servicenName : full qualified service name : package and name: like dk.dtu.locationtracker.RecordLocationService
     * @return
     */
    public static boolean isServiceRunning(Context context,String servicenName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d(TAG, "Service Name: " + service.service.getClassName());
            if (servicenName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = netInfo(context);
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isWifiNet(Context context) {
        NetworkInfo activeNetwork= netInfo(context);
        return activeNetwork != null && activeNetwork.getType()== ConnectivityManager.TYPE_WIFI;
    }

    public static  boolean isMobilNet(Context context) {
        NetworkInfo activeNetwork= netInfo(context);
        return activeNetwork != null && activeNetwork.getType()== ConnectivityManager.TYPE_MOBILE;
    }
    public static boolean isGpsEnabled(Context context){
        LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static NetworkInfo netInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork;
    }
    public static String getBaseUrl(Context context){
        SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(context);
        String url=prfs.getString(context.getResources().getString(R.string.settings_default_url_key), context.getResources().getString(R.string.settings_default_url_value));
        return url;
    }

    /**
     *  if true draw locations as marker else draw line
     * @param context
     * @return boolean true or false
     */
    public static boolean isMarker(Context context){
        String key=context.getResources().getString(R.string.settings_marker_option_key);
        String defaultValue=context.getResources().getString(R.string.settings_marker_option_default_value);
        String markerOrLine=readFromSharedPreferences(context,key,defaultValue);
        if(markerOrLine!=null&&markerOrLine.equalsIgnoreCase("Marker")){
            return true;
        }else{
            return false;
        }
    }
    private static String readFromSharedPreferences(Context context,String key,String defaultValue){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }
    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static void  prepareDialog(Dialog dialog,String title,String msg,int icon,View.OnClickListener yes,View.OnClickListener no){
        dialog.setContentView(R.layout.dialog_delete_layout);
        dialog.setTitle(title);
        ImageView dialogIcon=(ImageView)dialog.findViewById(R.id.dialogIcon);
        dialogIcon.setBackgroundResource(icon);
        TextView dialogMsg=(TextView)dialog.findViewById(R.id.dialogMessageTV);
        dialogMsg.setText(msg);
        Button ok=(Button)dialog.findViewById(R.id.okDialogBT);
        ok.setOnClickListener(yes);
        Button cancel=(Button)dialog.findViewById(R.id.cancelDialogBT);
        cancel.setOnClickListener(no);


    }
    public static void prepareDateTimeDialog(Dialog dateTimeDialog,final DateTimeListener dateTimeListener,View.OnClickListener onclick){
        Calendar calendar = Calendar.getInstance();
        //dateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dateTimeDialog.setContentView(R.layout.data_time_dialog);
        dateTimeDialog.setTitle("Select Date and time");
        final DatePicker datePicker=(DatePicker) dateTimeDialog.findViewById(R.id.datePicker);
        final TimePicker timePicker=(TimePicker)dateTimeDialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(dateTimeListener);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), dateTimeListener);
        Button okButton=(Button)dateTimeDialog.findViewById(R.id.datetimeOkBT);
        okButton.setOnClickListener(onclick);

    }

    public static void showNoNetworkDialog(final Context context){
        int icon=R.mipmap.ic_no_network;
        String title="No Network Connection";
        String msg="Please enable network connection";

        final Dialog dialog=new Dialog(context);
        View.OnClickListener yes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        };

        View.OnClickListener no=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        dialog.setContentView(R.layout.dialog_delete_layout);
        dialog.setTitle(title);
        ImageView dialogIcon=(ImageView)dialog.findViewById(R.id.dialogIcon);
        dialogIcon.setBackgroundResource(icon);
        TextView dialogMsg=(TextView)dialog.findViewById(R.id.dialogMessageTV);
        dialogMsg.setText(msg);
        Button ok=(Button)dialog.findViewById(R.id.okDialogBT);
        ok.setOnClickListener(yes);
        Button cancel=(Button)dialog.findViewById(R.id.cancelDialogBT);
        cancel.setOnClickListener(no);
        dialog.show();

    }
    public static void showDialog(final Context context, String title, String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }




    public static boolean isEmailValid(CharSequence email){
        if(TextUtils.isEmpty(email)){
            return false;
        }
        else{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

    }
    public static boolean isPlayServiceAvailable(Context context){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        if(api.isGooglePlayServicesAvailable(context)==ConnectionResult.SUCCESS){
            return true;
        }else{
            return false;
        }
    }





}
