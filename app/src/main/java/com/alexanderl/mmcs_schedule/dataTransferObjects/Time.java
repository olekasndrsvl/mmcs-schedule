package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Time {
    private int hours;
    private int minutes;
    public Time(int _hours,int _minutes)
    {
        hours=_hours;
        minutes=_minutes;
    }


    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    @Override
    public String toString()
    {
        return String.format("%02d", hours)+":"+String.format("%02d", minutes);
    }
}
