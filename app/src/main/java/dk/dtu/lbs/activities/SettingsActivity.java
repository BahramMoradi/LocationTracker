package dk.dtu.lbs.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import dk.dtu.lbs.fragments.SettingsFragment;
import dk.dtu.lbs.utils.AppUtil;


/**
 * Created by Bahram Moradi on 08-11-2015.
 */
public class SettingsActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_settings);
        return super.onPrepareOptionsMenu(menu);
    }


}
