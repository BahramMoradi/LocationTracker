package dk.dtu.lbs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.dto.Profile;
import dk.dtu.lbs.dto.RecordHistory;

/**
 * Created by Bahram Moradi on 27-12-2015.
 */
public class TrackerDataSource {
    private String TAG = TrackerDataSource.class.getName();
    private SQLiteDatabase database;
    private TrackerSQLiteHelper helper;
    private static volatile TrackerDataSource instance = null;
    private String[] userColumn = {TrackerSQLiteHelper.COLUMN_UID, TrackerSQLiteHelper.COLUMN_NAME, TrackerSQLiteHelper.COLUMN_PHONE, TrackerSQLiteHelper.COLUMN_MAIL, TrackerSQLiteHelper.COLUMN_DESCRIPTION};
    private String[] locationColumn = {TrackerSQLiteHelper.COLUMN_TIME, TrackerSQLiteHelper.COLUMN_LONGITUDE, TrackerSQLiteHelper.COLUMN_LATITUDE};
    private String[] recordHistoryColumn = {TrackerSQLiteHelper.COLUMN_RECORD_ID, TrackerSQLiteHelper.COLUMN_RECORD_FROM, TrackerSQLiteHelper.COLUMN_RECORD_TO};

    public static synchronized TrackerDataSource getInstance(Context context) {
        if (instance == null) {
            synchronized (TrackerDataSource.class) {
                if (instance == null) {
                    instance = new TrackerDataSource(context);
                }
            }
        }
        return instance;
    }

    private TrackerDataSource(Context context) {
        helper = TrackerSQLiteHelper.getInstance(context);
        Log.d(TAG, "TrackerDataSource instance created");
    }

    public void open() {
        database = helper.getWritableDatabase();
        Log.d(TAG, "open");
    }

    public void close() {
        helper.close();
        Log.d(TAG, "close");
    }

    public synchronized long saveProfile(Profile profile) {
        Log.d(TAG, "saveProfile");
        long id = -1;
        try {
            open();
            id = database.insert(TrackerSQLiteHelper.TABLE_USER, null, toContentValue(profile));
            Log.d(TAG, "saveProfile return code: " + id);
        } catch (Exception e) {
            if (e instanceof SQLiteFullException) {
                handleDatabaseFullException();
            } else {
                Log.d(TAG + "/saveProfile", e.getMessage());
            }
        } finally {
            close();
        }

        return id;
    }

    public synchronized List<Profile> readProfile() {
        Log.d(TAG, "readProfile");
        List<Profile> profiles = new ArrayList<>();
        Cursor cursor = null;
        try {
            open();
            cursor = database.query(TrackerSQLiteHelper.TABLE_USER, userColumn, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                profiles.add(toProfile(cursor));
                cursor.moveToNext();

            }
        } catch (Exception e) {
            Log.d(TAG + "/ readProfile", e.getMessage());
        } finally {
            cursor.close();
            close();
        }


        return profiles;
    }

    public synchronized void updateProfile(Profile profile) {
        Log.d(TAG, "updateProfile");
        ContentValues value = toContentValue(profile);
        value.remove(TrackerSQLiteHelper.COLUMN_UID);

        open();
        int rows = database.update(TrackerSQLiteHelper.TABLE_USER, value, TrackerSQLiteHelper.COLUMN_UID + " = " + profile.getUid(), null);
        Log.d(TAG, "Number of row affected: " + rows);
        close();
    }

    public synchronized void deleteProfile() {
        Log.d(TAG, "deleteProfile");
        open();
        int row = database.delete(TrackerSQLiteHelper.TABLE_USER, null, null);
        Log.d(TAG, "deleteProfile, Row affected : " + row);
        close();

    }
    public synchronized  void clearProfileTable(){
        open();
        database.delete(TrackerSQLiteHelper.TABLE_USER, null, null);
        close();
    }

    public synchronized long getUserId() {
        Log.d(TAG, "getUserId");
        long uid = 0;
        Cursor cursor = null;
        try {
            open();
            cursor = database.query(TrackerSQLiteHelper.TABLE_USER, new String[]{TrackerSQLiteHelper.COLUMN_UID}, null, null, null, null, null);

            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                uid = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.d(TAG + "/ getUserId", e.getMessage());
        } finally {

            cursor.close();
            close();
        }
        return uid;
    }

    public synchronized List<GeoCoordinate> getAllLocations() {
        Log.d(TAG, "getAllLocations");
        List<GeoCoordinate> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            open();
            cursor = database.query(TrackerSQLiteHelper.TABLE_LOCATION, locationColumn, null, null, null, null, TrackerSQLiteHelper.COLUMN_TIME + " ASC ");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(toGeoCoordinate(cursor));
                cursor.moveToNext();

            }
        } catch (Exception e) {
            Log.d(TAG + "/ getAllLocations", e.getMessage());
        } finally {

            cursor.close();
            close();
        }
        Log.d(TAG, "getAllLocations size : " + list.size());
        return list;
    }

    /*  loss tolerant : loosing one location data is not important but the database some
        data from database should be removed
     */
    public synchronized void insertLocation(GeoCoordinate geoCoordinate) {
        Log.d(TAG, "insertLocation");
        try {
            open();
            long id = database.insert(TrackerSQLiteHelper.TABLE_LOCATION, null, toContentValues(geoCoordinate));
            Log.d(TAG, "id of inserted GeoCoordinate: " + id);
        } catch (Exception e) {
            if (e instanceof SQLiteFullException) {
                handleDatabaseFullException();
            } else {
                Log.d(TAG + "insertLocation", e.getMessage());
            }
        } finally {
            close();
        }
    }

    public synchronized void deleteLocationInTimeInterval(long from, long to) {
        Log.d(TAG, "deleteLocationInTimeInterval");
        open();
        int rows = database.delete(TrackerSQLiteHelper.TABLE_LOCATION, TrackerSQLiteHelper.COLUMN_TIME + " BETWEEN ? AND  ? ;", new String[]{Long.toString(from), Long.toString(to)});
        Log.d(TAG, "deleteLocationInTimeInterval rows affected: " + rows);
        close();
    }
    public synchronized  void clearLocationTable(){
        open();
        database.delete(TrackerSQLiteHelper.TABLE_LOCATION, null, null);
        close();
    }

    /*notify user if database was full to tray again.*/
    public synchronized long insertToRecordHistory(RecordHistory record) {
        Log.d(TAG, "insertToRecordHistory");
        ContentValues value = toContentValues(record);
        boolean isFull = false;
        long id=-1;
        try {
            open();
            id=database.insert(TrackerSQLiteHelper.TABLE_RECORD_HISTORY, null, value);
            Log.d(TAG, "insertToRecordHistory id : "+id);
        } catch (SQLiteFullException full) {
            handleDatabaseFullException();
            isFull = true;
        } finally {
            close();
        }
        return id;

    }

    public synchronized void deleteFromRecordHistory(List<Long> ids) {
        String args=TextUtils.join(",",ids);
        Log.d(TAG, "deleteFromRecordHistory/ size "+ids.size());
        open();
        //"DELETE FROM " + TrackerSQLiteHelper.TABLE_RECORD_HISTORY + " WHERE " + TrackerSQLiteHelper.COLUMN_RECORD_ID + " IN (%s);"
        database.execSQL(String.format(TrackerSQLiteHelper.DELETE_FROM_RECORD_HISTORY, args));


        close();
    }
    public synchronized void clearRecordHistoryTable(){
        open();
        database.delete(TrackerSQLiteHelper.TABLE_RECORD_HISTORY, null, null);
        close();
    }

    public synchronized List<RecordHistory> getAllRecordHistory() {
        Log.d(TAG, "getAllRecordHistory");
        List<RecordHistory> records = null;
        Cursor cursor = null;
        try {
            open();
            cursor = database.query(TrackerSQLiteHelper.TABLE_RECORD_HISTORY, recordHistoryColumn, null, null, null, null,null);
            records = toRecordHistoryList(cursor);

        } catch (Exception e) {
            Log.d(TAG + "/ getAllRecordHistory", e.getMessage());
        } finally {
            if(cursor!=null){
            cursor.close();}
            close();

        }
        Log.d(TAG + "/ getAllRecordHistory ", "Size of returned list: " + records.size());
        return records;
    }
    private void deleteOneRecord(long id){
        Log.d(TAG, "deleteOneRecord");
       int row= database.delete(TrackerSQLiteHelper.TABLE_RECORD_HISTORY,TrackerSQLiteHelper.COLUMN_RECORD_ID,new String[]{Long.toString(id)});
        Log.d(TAG, "Row affected: "+row);
    }

    private ContentValues toContentValues(GeoCoordinate geo) {
        ContentValues value = new ContentValues();
        value.put(TrackerSQLiteHelper.COLUMN_TIME, geo.getTime());
        value.put(TrackerSQLiteHelper.COLUMN_LONGITUDE, geo.getLongitude());
        value.put(TrackerSQLiteHelper.COLUMN_LATITUDE, geo.getLatitude());
        return value;
    }

    private GeoCoordinate toGeoCoordinate(Cursor cur) {
        GeoCoordinate geo = new GeoCoordinate();
        geo.setTime(cur.getLong(0));
        geo.setLongitude(cur.getDouble(1));
        geo.setLatitude(cur.getDouble(2));
        return geo;

    }

    private ContentValues toContentValue(Profile profile) {
        ContentValues value = new ContentValues();
        value.put(TrackerSQLiteHelper.COLUMN_UID, profile.getUid());
        value.put(TrackerSQLiteHelper.COLUMN_NAME, profile.getName());
        value.put(TrackerSQLiteHelper.COLUMN_PHONE, profile.getPhone());
        value.put(TrackerSQLiteHelper.COLUMN_MAIL, profile.getMail());
        value.put(TrackerSQLiteHelper.COLUMN_DESCRIPTION, profile.getDescription());
        return value;
    }

    private Profile toProfile(Cursor cursor) {
        Profile profile = new Profile();
        profile.setUid(cursor.getLong(0));
        profile.setName(cursor.getString(1));
        profile.setPhone(cursor.getLong(2));
        profile.setMail(cursor.getString(3));
        profile.setDescription(cursor.getString(4));
        return profile;
    }

    private ContentValues toContentValues(RecordHistory record) {
        ContentValues value = new ContentValues();
        value.put(TrackerSQLiteHelper.COLUMN_RECORD_FROM, record.getFrom());
        value.put(TrackerSQLiteHelper.COLUMN_RECORD_TO, record.getTo());
        return value;
    }

    private List<RecordHistory> toRecordHistoryList(Cursor cursor) {
        List<RecordHistory> records = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(new RecordHistory(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2)));
            cursor.moveToNext();
        }

        return records;
    }
    /*deleting every forth row to get more space*/
    private void handleDatabaseFullException() {
        Log.d(TAG, "Handle database full exception");
        long min=0;
        long max=0;
        int incrementValue=4;
        List<Long> tobeDeleted=new ArrayList<>();
        open();
        /*find minimum id*/
        Cursor mincur=database.rawQuery(TrackerSQLiteHelper.MIN_LOCATION_ID, null);
        if(mincur!=null){
            mincur.moveToFirst();
            if(!mincur.isAfterLast()){
                mincur.getLong(0);
            }
            mincur.close();
        }
        Log.d(TAG, "Min ID : "+min);
         /*find max id*/
        Cursor maxcur=database.rawQuery(TrackerSQLiteHelper.MAX_LOCATION_ID,null);
        if(maxcur!=null){
            maxcur.moveToFirst();
            if(!maxcur.isAfterLast()){
                max=maxcur.getLong(0);
            }
            maxcur.close();
        }
        for(long i=min;i<max;i+=incrementValue){
            tobeDeleted.add(i);
        }
        Log.d(TAG, "MAX ID : "+max);
        /*join and delete locations*/
        String ids=TextUtils.join(",",tobeDeleted);
        database.execSQL(String.format(TrackerSQLiteHelper.DELETE_FROM_LOCATION,ids));
        close();

    }


    public List<String> getTable(){
        List<String> ta=new ArrayList<>();
        open();
        Cursor cursor=database.rawQuery("SELECT name FROM sqlite_master WHERE type='table';",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ta.add(cursor.getString(0));
            cursor.moveToNext();
        }
        if(cursor!=null){
            cursor.close();
        }
        close();
        return ta;
    }


}
