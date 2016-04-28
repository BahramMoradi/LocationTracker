package dk.dtu.lbs.dto;

/**
 * Created by Bahram Moradi on 15-01-2016.
 */
public class RecordHistoryWrapper {
    private RecordHistory recordHistory;
    private boolean checked =false;

    public RecordHistoryWrapper(){};
    public RecordHistoryWrapper(RecordHistory recordHistory, boolean checked) {
        this.recordHistory=recordHistory;
        this.checked = checked;

    }
    public RecordHistory getRecordHistory(){
        return recordHistory;
    }
    public void setRecordHistory(RecordHistory recordHistory){
        this.recordHistory=recordHistory;
    }
    public boolean isChecked(){
        return checked;
    }
    public void setChecked(boolean checked){
        this.checked = checked;

    }

}
