package dk.dtu.lbs.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bahram on 23-12-2015.
 */
public class Scheduler {
    private static  volatile Scheduler instance = null;
    private Map<String, Task> tasks = new HashMap<>();
    private static Timer timer = null;

    private Scheduler() {
    }

    public static synchronized Scheduler getInstance() {
        if (instance == null) {
            synchronized (Scheduler.class) {
                if (instance == null) {
                    instance = new Scheduler();
                    timer = new Timer();
                }
            }
        }
        return instance;
    }

    /*only second and minute are supported */
    public synchronized void schedule(Task task) {
        if (task != null && !tasks.containsKey(task.getName())) {
            tasks.put(task.getName(), task);
            int interval = 0;
            if (task.getTimeUnit() == TimeUnit.SECONDS) {
                interval = 1000 * task.getInterval();
            }
            if (task.getTimeUnit() == TimeUnit.MINUTES) {
                interval = 60 * 1000 * task.getInterval();
            }
            timer.scheduleAtFixedRate(task, task.getDelay(), interval);
        }
    }

    public synchronized void cancelTask(Task task) {
        if (tasks.containsKey(task.getName())) {
            tasks.remove(task.getName()).cancel();

        }
    }
}
