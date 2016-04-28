package dk.dtu.lbs.fragments;
import dk.dtu.lbs.activities.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;

import dk.dtu.lbs.interfaces.OnSubmitListener;
import dk.dtu.lbs.listeners.DateTimeListener;
import dk.dtu.lbs.utils.AppUtil;

/**
 * Created by Bahram on 30-12-2015.
 */
public class FromToDateFragment extends DialogFragment implements View.OnClickListener{
    private Button from=null;
    private Button to=null;
    private Button ok=null;
    private Button cancel=null;
    private long fromDateTime=0;
    private long toDateTime=0;
    private DateTimeListener listener=null;
    private Dialog dateTimeDialog =null;
    private OnSubmitListener submitListener=null;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener=new DateTimeListener();
        dateTimeDialog=new Dialog(getActivity());
        final Dialog dialog=new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.time_interval_dialog);
        from=(Button)dialog.findViewById(R.id.setFromBT);
        to=(Button) dialog.findViewById(R.id.setToBT);
        ok=(Button)dialog.findViewById(R.id.okSetTimeBT);
        cancel=(Button)dialog.findViewById(R.id.cancelSetTimeBT);
        from.setOnClickListener(this);
        to.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


        return dialog;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.setFromBT:
                showDateTimeDialog(R.id.setFromBT);
            break;
            case R.id.setToBT:
                showDateTimeDialog(R.id.setToBT);
                break;
            case R.id.okSetTimeBT:
                if(submitListener!=null){
                    submitListener.onSubmit(R.id.okSetTimeBT,fromDateTime,toDateTime);
                    this.dismiss();
                }
                break;
            case R.id.cancelSetTimeBT:
                if(submitListener!=null){
                    submitListener.onSubmit(R.id.cancelSetTimeBT,fromDateTime,toDateTime);
                    this.dismiss();
                }
                break;


        }


    }
    public void getDateTime(int which){

        String stringDate=listener.getYear()+"-"+listener.getMonth()+"-"+listener.getDay()+" "+listener.getHour()+":"+listener.getMinute();
        try{
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=formatter.parse(stringDate);
            if(which==R.id.setFromBT){
                from.setText("From : "+stringDate);
                fromDateTime=date.getTime();
            }else{
                to.setText("To : "+stringDate);
                toDateTime=date.getTime();
            }
        }catch(Exception e){}

    }
    public void showDateTimeDialog(final int which){
        AppUtil.prepareDateTimeDialog(dateTimeDialog, listener, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateTime(which);
                dateTimeDialog.dismiss();
            }
        });
        dateTimeDialog.show();

    }
    public void setOnSubmitListener(OnSubmitListener listener){
        submitListener=listener;
    }


}
