package dk.dtu.lbs.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.fragments.CreateUpdateProfile;
import dk.dtu.lbs.fragments.ShowProfile;
import dk.dtu.lbs.interfaces.OnFragmentInteractionListener;
import dk.dtu.lbs.utils.AppUtil;


public class ProfileActivity extends BaseActivity implements OnFragmentInteractionListener {
    private static String TAG=ProfileActivity.class.getName();
    private Context context;
    public static final String SHOW_PROFILE="SHOW PROFILE";
    public static final String CREATE_PROFILE="CREATE PROFILE";
    public static final String EDIT_PROFILE="EDIT PROFILE";
    private TrackerDataSource localDatabase=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        context=this.getApplicationContext();
        localDatabase=TrackerDataSource.getInstance(context);
        if(hasProfile()){
            onFragmentInteraction(SHOW_PROFILE);
        }else{
            onFragmentInteraction(CREATE_PROFILE);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_user_profile);
        return super.onPrepareOptionsMenu(menu);
    }

public boolean hasProfile(){
    boolean hasProfile= !localDatabase.readProfile().isEmpty();
    Toast.makeText(this,"Has profile: "+hasProfile,Toast.LENGTH_SHORT).show();
    return hasProfile;

}

    @Override
    public void onFragmentInteraction(String action) {
        FrameLayout layout=(FrameLayout)findViewById(R.id.fragment_container);
        layout.removeAllViews();
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        transaction.setTransition(android.R.animator.fade_out);
        switch (action){
            case SHOW_PROFILE:
                transaction.add(R.id.fragment_container,ShowProfile.newInstance(),"show");
                break;
            case CREATE_PROFILE:
                transaction.add(R.id.fragment_container, CreateUpdateProfile.newInstance(CreateUpdateProfile.CREATE),"create");
                break;
            case EDIT_PROFILE:
                transaction.add(R.id.fragment_container, CreateUpdateProfile.newInstance(CreateUpdateProfile.UPDATE),"edit");
                break;
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
