package dk.dtu.lbs.map;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.services.RecordLocationService;

/**
 * Created by Bahram on 25-12-2015.
 */
public class MapHelper implements GoogleMap.OnCameraChangeListener{
    private String TAG=this.getClass().getName();
    private GoogleMap map=null;
    private Polyline line=null;
    private CameraPosition position=null;
    private float width=20.0f;
    private int color=Color.RED;
    private float zoom=17.0f;
    private boolean isFirstLocation;
    public MapHelper(GoogleMap map){
        this.map=map;
        isFirstLocation=true;
        map.setOnCameraChangeListener(this);
        newPolyLine(width, color);
        map.getUiSettings().setZoomControlsEnabled(true);

    }
    public void clearMap(){
        map.clear();
    }

    public void  newPolyLine(float with,int color){
        if(map!=null){
           line= map.addPolyline(new PolylineOptions().width(with).color(color).geodesic(true).visible(true));
        }
        /**
        List<LatLng> points= RecordLocationService.getPointList();
        if(points!=null){
            line.setPoints(points);
        }**/
    }
    public void drawMarker(double lat, double lon){
        if (map != null) {
            //map.clear();
           // map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lon))
                    .zoom(zoom)
                    .bearing(position.bearing)
                    .tilt(position.tilt)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Lon: " + lon + " Lat: " + lat).flat(true));
            isFirstLocation=false;

        }

    }
    public void updateRoute(){
        List<LatLng> points = RecordLocationService.getPointList();
        if (points != null) {
            line.setPoints(points);
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude), zoom));
               // map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude), zoom));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude))
                    .zoom(zoom)
                    .bearing(position.bearing)
                    .tilt(position.tilt)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            isFirstLocation=false;



        }
    }
    public void onCameraChange(CameraPosition cameraPosition) {
        this.position=cameraPosition;
        if(!isFirstLocation){
        this.zoom=cameraPosition.zoom;
        }
        Log.d(TAG, "MAX Zoom level: " + map.getMaxZoomLevel());
        Log.d(TAG, "Min Zoom level: " + map.getMinZoomLevel());
        Log.d(TAG, "Zoom level: " + zoom);
    }
    public void drawLocationsOnMap(List<GeoCoordinate> locations){
        if(map!=null){
            line= map.addPolyline(new PolylineOptions().width(width).color(color).geodesic(true).visible(true));
            List<LatLng> points=new ArrayList<>();
            for (GeoCoordinate geo: locations){
                points.add(new LatLng(geo.getLatitude(),geo.getLongitude()));
            }
            line.setPoints(points);
            if(!points.isEmpty()) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude), zoom));
            }
        }
    }
}
