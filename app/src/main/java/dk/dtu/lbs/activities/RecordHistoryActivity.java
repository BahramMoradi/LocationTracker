package dk.dtu.lbs.activities;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.lbs.adapters.RecordHistoryAdapter;
import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.RecordHistory;
import dk.dtu.lbs.dto.RecordHistoryWrapper;
import dk.dtu.lbs.utils.AppUtil;


public class RecordHistoryActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private RecordHistoryAdapter adapter=null;
    private ListView listView=null;
    private ImageButton deleteBT=null;
    private List<RecordHistoryWrapper> records;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_history);
        this.context=this.getApplicationContext();
        listView=(ListView)findViewById(R.id.record_history_list);
        deleteBT=(ImageButton)findViewById(R.id.recordHistoryDeleteBT);
        deleteBT.setOnClickListener(this);
        records=new ArrayList<>();
        addRecords();


    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_record_history);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        List<Long> rids=new ArrayList<>();
        List<RecordHistoryWrapper> tobeRemoved=new ArrayList<>();
        Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
        StringBuilder builder=new StringBuilder();
        builder.append("Selected: ");
        for(RecordHistoryWrapper recordHistoryObject: records){
            if(recordHistoryObject.isChecked()){
             builder.append(recordHistoryObject.getRecordHistory().getRid()+", ");
             tobeRemoved.add(recordHistoryObject);
                rids.add(recordHistoryObject.getRecordHistory().getRid());
            }

        }
        records.removeAll(tobeRemoved);
        adapter=new RecordHistoryAdapter(context,records);
        listView.setAdapter(adapter);
        deleteFromDatabase(rids);
    }
    private void addRecords(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TrackerDataSource database=TrackerDataSource.getInstance(context);
                List<RecordHistory> rh= database.getAllRecordHistory();
                for (RecordHistory record: rh){
                    records.add(new RecordHistoryWrapper(record,false));
                }
                initAdapter();
            }
        }).start();




    }



    private void initAdapter(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter = new RecordHistoryAdapter(context, records);
                        listView.setAdapter(adapter);
                    }
                });



    }
    public void deleteFromDatabase(final List<Long> toBeDeleted){

               TrackerDataSource.getInstance(context).deleteFromRecordHistory(toBeDeleted);



    }
public boolean onKeyDown(int keyCode,KeyEvent event){
    if(keyCode==KeyEvent.KEYCODE_HOME){
        Toast.makeText(this,"Home presed",Toast.LENGTH_SHORT).show();
    }
    return super.onKeyDown(keyCode,event);
}
}
