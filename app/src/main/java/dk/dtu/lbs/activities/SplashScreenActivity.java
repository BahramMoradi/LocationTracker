package dk.dtu.lbs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.utils.AppUtil;

public class SplashScreenActivity extends Activity implements Animation.AnimationListener {

    private ImageButton network=null;
    private ImageButton gps=null;
    private ImageButton mobileDateBT=null;
    private ImageView progress=null;
    private AnimationDrawable frameAnimation;
    private TextView textView =null;

    private Context context=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this.getApplicationContext();
        progress = (ImageView) findViewById(R.id.splash_progress);
        network = (ImageButton) findViewById(R.id.networkStatusImBT);
        mobileDateBT=(ImageButton) findViewById(R.id.mobileDataBT);
        gps = (ImageButton) findViewById(R.id.gpsStatusBT);
        textView =(TextView)findViewById(R.id.trackerTV);

        AlphaAnimation fadeInForStatus = new AlphaAnimation(0.0f , 1.0f ) ;
        fadeInForStatus.setDuration(2000);
        fadeInForStatus.setFillAfter(true);
        /*check network connection*/
        if(AppUtil.isWifiNet(this)){
            network.setBackgroundResource(R.mipmap.ic_action_device_signal_wifi);
        }else{
            network.setBackgroundResource(R.mipmap.ic_action_device_signal_wifi_off);
        }
        /*check gps status*/
        if(AppUtil.isMobilNet(this)){
            mobileDateBT.setBackgroundResource(R.mipmap.ic_action_mobile_data);
        }
        else{
            mobileDateBT.setBackgroundResource(R.mipmap.ic_action_ic_action_no_mobile_data);
        }
        if(AppUtil.isGpsEnabled(this)){
            gps.setBackgroundResource(R.mipmap.ic_action_icona_gps);
        }
        else{
            gps.setBackgroundResource(R.mipmap.ic_action_device_location_disabled);
        }
        network.startAnimation(fadeInForStatus);
        mobileDateBT.startAnimation(fadeInForStatus);
        gps.startAnimation(fadeInForStatus);

        progress.setBackgroundResource(R.drawable.splash_progress_animation);
        frameAnimation = (AnimationDrawable) progress.getBackground();
        frameAnimation.start();
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        fadeIn.setDuration(5000); //2470
        fadeIn.setFillAfter(true);
        textView.startAnimation(fadeIn);
        fadeIn.setAnimationListener(this);



    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
            finish();
        if(frameAnimation.isRunning()){
            frameAnimation.stop();
        }
        if(TrackerDataSource.getInstance(context).getUserId()==0){
            Intent profile=new Intent(this,ProfileActivity.class);
            this.startActivity(profile);
        }else{
            Intent intent=new Intent(this,RecordLocationActivity.class);
            this.startActivity(intent);

        }




    }
    public void onAnimationRepeat(Animation animation) {

    }
}
