package dk.dtu.lbs.listeners;

import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Bahram on 28-12-2015.
 */
public class DateTimeListener implements TimePicker.OnTimeChangedListener,DatePicker.OnDateChangedListener{
    private int year=0;
    private int month=0;
    private int day=0;
    private int hour=0;
    private int minute=0;


    public DateTimeListener(){
        Calendar calendar = Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        minute=calendar.get(Calendar.MINUTE);
        hour=calendar.get(Calendar.HOUR);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        day=dayOfMonth;
        month=monthOfYear;
        this.year=year;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        hour=hourOfDay;
        this.minute=minute;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }


    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

}

