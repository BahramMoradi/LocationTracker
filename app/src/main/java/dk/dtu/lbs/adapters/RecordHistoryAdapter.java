package dk.dtu.lbs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import dk.dtu.lbs.activities.R;
import dk.dtu.lbs.dto.RecordHistory;
import dk.dtu.lbs.dto.RecordHistoryWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




/**
 * Created by Bahram Moradi on 04-02-2016.
 */
public class RecordHistoryAdapter extends BaseAdapter{
    LayoutInflater inflater=null;
    Context context=null;
    List<RecordHistoryWrapper> mainData=null;
    private List<Long> selectedRID=null;




    public RecordHistoryAdapter(Context context,List<RecordHistoryWrapper> mainData){
        this.context=context;
        this.mainData=mainData;
        inflater=LayoutInflater.from(context);
        selectedRID=new ArrayList<>();
    }
    public List<Long> getSelectedRID(){
        return selectedRID;
    }
    public void clearSelectedRID(){
        selectedRID.clear();
    }
    public int getCount() {
        return mainData.size();
    }

    @Override
    public Object getItem(int position) {

        return mainData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }
   public void removeList(RecordHistoryWrapper object){
       mainData.remove(object);
       notifyDataSetChanged();
   }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view==null){
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.record_history_row,null);
            holder.fromTV=(TextView)view.findViewById(R.id.fromDateTimeTV);
            holder.toTV=(TextView)view.findViewById(R.id.toDateTimeTV);
            holder.checkBox=(CheckBox)view.findViewById(R.id.selectRecordCheckBox);
            view.setTag(holder);
            view.setTag(R.id.fromDateTimeTV,holder.fromTV);
            view.setTag(R.id.toDateTimeTV,holder.toTV);
            view.setTag(R.id.selectRecordCheckBox, holder.checkBox);

        }else{
            holder=(ViewHolder)view.getTag();
        }

        holder.checkBox.setTag(position);
        RecordHistoryWrapper recordHistoryObject=mainData.get(position);
        RecordHistory recordHistory=recordHistoryObject.getRecordHistory();
        holder.fromTV.setText(toDate(recordHistory.getFrom()));
        holder.toTV.setText(toDate(recordHistory.getTo()));
        holder.checkBox.setChecked(recordHistoryObject.isChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) buttonView.getTag();
                mainData.get(position).setChecked(buttonView.isChecked());


            }
        });

        return view;
    }
    static  class ViewHolder{
        protected TextView fromTV;
        protected TextView toTV;
        protected CheckBox checkBox;
    }
    private String toDate(long value){
        DateFormat format= new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String date=format.format(new Date(value));
        return date;
    }
}
