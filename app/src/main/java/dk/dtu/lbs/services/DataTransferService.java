package dk.dtu.lbs.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.util.List;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.wsclient.RESTClient;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataTransferService extends IntentService {
    private String TAG = DataTransferService.class.getName();
    private static int failNr = 0;
    //private Realm realm=null;
    //private LocalDatabase database=null;
    //private RealmResults<GeoCoordinate> locations=null;
    private TrackerDataSource database = null;
    private List<GeoCoordinate> locations = null;
    private long key1 = 0;
    private long key2 = 0;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public DataTransferService() {
        super("DataTransferService");

    }

    public synchronized void restFailNr() {
        failNr = 0;
    }

    public synchronized int getFailNr() {
        return failNr;
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     * source for this doc:http://developer.android.com/intl/es/guide/components/services.html
     */
    protected void onHandleIntent(Intent intent) {
        database = TrackerDataSource.getInstance(this.getApplicationContext());
        long uid = intent.getExtras().getLong("uid", 0);
        final Context context = this.getApplicationContext();
        locations = database.getAllLocations();
        if (!locations.isEmpty()) {
            key1 = locations.get(0).getTime();
            key2 = locations.get(locations.size() - 1).getTime();
        }
        Log.d(TAG, "Size of array : " + locations.size());
        if (!locations.isEmpty()&&uid!=0) {
            TrackerRestService client = RESTClient.getClient(this);
            Call<List<GeoCoordinate>> call = client.postUserLocations(uid, locations);
            call.enqueue(new CallBack(context, key1, key2));
        }
    }


    public class CallBack implements Callback<List<GeoCoordinate>> {
        private Context context;
        private long key1;
        private long key2;

        public CallBack(Context context, long key1, long key2) {
            this.context = context;
            this.key1 = key1;
            this.key2 = key2;

        }

        @Override
        public void onResponse(Response<List<GeoCoordinate>> response, Retrofit retrofit) {
            int code = response.code();
            String msg = response.message();
            Log.d(TAG, "Response Code: " + code);
            Log.d(TAG, "Response Message: " + msg);
            if (code == 200) {
                database.deleteLocationInTimeInterval(key1, key2);
                failNr = 0;
            } else {
                failNr++;
            }

        }

        @Override
        public void onFailure(Throwable t) {
            Log.d(TAG, "onFailure: " + t.getMessage());
            failNr++;

        }
    }


}
