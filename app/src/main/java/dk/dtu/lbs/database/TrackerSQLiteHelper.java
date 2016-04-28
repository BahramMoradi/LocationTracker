package dk.dtu.lbs.database;

/**
 * Created by Bahram Moradi on 27-12-2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*package private class*/
class TrackerSQLiteHelper extends SQLiteOpenHelper {
    /* Database */
    private static final String DATABASE_NAME = "tracker.db";
    private static final int DATABASE_SIZE = 250 * 1000000;
    private static final int DATABASE_VERSION = 1;
    /* user table*/
    protected static final String TABLE_USER = "user";
    protected static final String COLUMN_UID = "uid";
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_PHONE = "phone";
    protected static final String COLUMN_MAIL = "mail";
    protected static final String COLUMN_DESCRIPTION = "description";
    /* location table*/
    protected static final String TABLE_LOCATION = "location";
    protected static final String COLUMN_LOCATION_ID = "lid";
    protected static final String COLUMN_TIME = "time";
    protected static final String COLUMN_LONGITUDE = "longitude";
    protected static final String COLUMN_LATITUDE = "latitude";
    /* Record history table*/
    protected static final String TABLE_RECORD_HISTORY = "record_history";
    protected static final String COLUMN_RECORD_ID = "rid";
    protected static final String COLUMN_RECORD_FROM = "from_date";
    protected static final String COLUMN_RECORD_TO = "to_data";

    /* sql queries for creating tables */
    private static final String CREATE_USER_TABLE = "CREATE TABLE "
            + TABLE_USER + "("
            + COLUMN_UID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PHONE + " INTEGER, "
            + COLUMN_MAIL + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT );";
    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + TABLE_LOCATION + "("
            + COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TIME + " INTEGER ,"
            + COLUMN_LONGITUDE + " REAL, "
            + COLUMN_LATITUDE + " REAL );";
    private static final String CREATE_RECORD_HISTORY_TABLE = "CREATE TABLE "
            + TABLE_RECORD_HISTORY + " ("
            + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_RECORD_FROM + " INTEGER, "
            + COLUMN_RECORD_TO + " INTEGER );";
    protected static String DELETE_FROM_RECORD_HISTORY = "DELETE FROM " + TABLE_RECORD_HISTORY + " WHERE " + COLUMN_RECORD_ID + " IN (%s);";
    protected static String MAX_LOCATION_ID = "SELECT MAX(" + COLUMN_LOCATION_ID + ") FROM " + TABLE_LOCATION + " ;";
    protected static String MIN_LOCATION_ID = "SELECT MIN(" + COLUMN_LOCATION_ID + ") FROM " + TABLE_LOCATION + " ;";
    protected static String DELETE_FROM_LOCATION = "DELETE FROM " + TABLE_LOCATION + " WHERE " + COLUMN_LOCATION_ID + " IN (%s);";


    /**
     * Use always the context.getApplicationContext() for this class
     *
     * @param context
     * @return
     */

    private static volatile TrackerSQLiteHelper instance = null;

    public static synchronized TrackerSQLiteHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (TrackerSQLiteHelper.class) {
                if (instance == null) {
                    instance = new TrackerSQLiteHelper(context);
                }
            }
        }
        return instance;
    }

    private TrackerSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.setMaximumSize(DATABASE_SIZE);
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_RECORD_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TrackerSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER + ";");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION + ";");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD_HISTORY + ";");
        onCreate(database);
    }

}
