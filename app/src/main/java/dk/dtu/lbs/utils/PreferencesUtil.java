package dk.dtu.lbs.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bahram on 05-11-2015.
 */
public class PreferencesUtil {
    SharedPreferences sharedPref=null;
    public int readInt(Context context,String pref_name,int mode,String key){
        sharedPref=context.getSharedPreferences(pref_name,mode);
        int  value = sharedPref.getInt(key,-1);
    return value;
    }
    public void writeInt(Context context,String pref_name,int mode,String key,String value){
        SharedPreferences.Editor editor=context.getSharedPreferences(pref_name, mode).edit();
        editor.putString(key,value);
        editor.commit();


    }
}
