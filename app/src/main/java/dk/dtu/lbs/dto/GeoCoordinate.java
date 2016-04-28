package dk.dtu.lbs.dto;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bahram on 08-11-2015.
 */
public class GeoCoordinate extends RealmObject {
    @PrimaryKey
    @SerializedName("time")
    private long time;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    public GeoCoordinate(){
        super();
    }
    public GeoCoordinate(long time,double longitude,double latitude){
        super();
        this.time=time;
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
