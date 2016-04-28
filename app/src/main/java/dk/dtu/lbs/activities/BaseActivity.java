package dk.dtu.lbs.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import dk.dtu.lbs.utils.AppUtil;


/**
 * Created by Bahram Moradi on 11-11-2015.
 */

/**
 * This is the base class for all activities with same menu.
 * The activities should remove them self from menu.
 */
public class BaseActivity extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_record:
                Intent record= new Intent(this, RecordLocationActivity.class);
                startActivity(record);
                finish();
                return true;
            case R.id.action_user_profile:
                Intent profile = new Intent(this,ProfileActivity.class);
                startActivity(profile);
                finish();
                return true;
            case R.id.action_my_locations:
                Intent myLocations = new Intent(this, MyLocationsActivity.class);
                startActivity(myLocations);
                finish();
                return true;
            case R.id.action_record_history:
                Intent recordHistory = new Intent(this, RecordHistoryActivity.class);
                startActivity(recordHistory);
                finish();
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                finish();
                return true;
            case R.id.action_help:
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

            /**
             case R.id.action_test:
             Intent test = new Intent(activity, TestActivity.class);
             activity.startActivity(test);
             activity.finish();
             return true;**/


        }

    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
