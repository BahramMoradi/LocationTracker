package dk.dtu.lbs.wsclient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;


import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.utils.AppUtil;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Bahram Moradi on 18-11-2015.
 */
public class RESTClient {
    public static String TAG = "RESTClient";
    private  TrackerRestService trackerAPI;

    public synchronized static TrackerRestService getClient(Context context) {

        String baseUrl= AppUtil.getBaseUrl(context);
        Log.d(TAG,"baseUrl"+baseUrl);
        if(baseUrl==null){
            Log.d(TAG,"URL is null");
            //Toast.makeText(context,"Endpoint url is null",Toast.LENGTH_SHORT).show();
        }

            OkHttpClient okClient = new OkHttpClient();
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        TrackerRestService trackerAPI = client.create(TrackerRestService.class);

        return trackerAPI;
    }



}
