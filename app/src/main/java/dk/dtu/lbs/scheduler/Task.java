package dk.dtu.lbs.scheduler;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bahram on 23-12-2015.
 */
public abstract class Task extends  TimerTask{
    public abstract void setName(String name);
    public abstract void setDelay(int delay);
    public abstract void setInterval(int interval);
    public abstract void setTimeUnit(TimeUnit unit);
    public abstract String getName();
    public abstract int getDelay();
    public abstract int getInterval();
    public abstract TimeUnit getTimeUnit();

}
