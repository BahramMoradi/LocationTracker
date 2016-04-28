package dk.dtu.lbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.List;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.services.DataTransferService;
import dk.dtu.lbs.utils.AppUtil;

/**
 * Global network connection status receiver.
 * This class is responsible for receiving the network connection status changes.
 * if location service is not running, and there is network connection
 * then we start data transfer service to check if there is data in local database and then send it to remote webservice
 *
 */
public class GlobalNetworkStateMonitor extends BroadcastReceiver {


    private boolean isConnected = false;
    private boolean isWiFi = false;
    private boolean isMobile = false;


    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = null;
        String action = intent.getAction();

        switch (action) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                boolean conn=AppUtil.isNetworkConnected(context);
                boolean wifi=AppUtil.isWifiNet(context);
                boolean mobile=AppUtil.isMobilNet(context);
                //Toast.makeText(context,"Conctivity changed: "+conn+" Wifi: "+wifi+" Mobile: "+mobile,Toast.LENGTH_SHORT).show();
                if (conn&&!AppUtil.isServiceRunning(context,"dk.dtu.lbs.services.RecordLocationService")){
                    List<GeoCoordinate> list=TrackerDataSource.getInstance(context.getApplicationContext()).getAllLocations();
                    if(!list.isEmpty()){
                        long uid=TrackerDataSource.getInstance(context.getApplicationContext()).getUserId();
                        Intent serviceIntent =new Intent(context, DataTransferService.class);
                        serviceIntent.putExtra("uid",uid );
                        context.startService(serviceIntent);
                    }
                }
                break;

        }
    }

}
