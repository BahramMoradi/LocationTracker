package dk.dtu.lbs.dto;

/**
 * Created by Bahram Moradi on 20-12-2015.
 */
public class RecordHistory {
    long rid;
    long from;
    long to;
    public RecordHistory(){};
    public RecordHistory(long rid, long from, long to){
        this.rid=rid;
        this.from=from;
        this.to=to;
    }


    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }
}
