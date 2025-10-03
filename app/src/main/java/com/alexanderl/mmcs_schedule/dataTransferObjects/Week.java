package com.alexanderl.mmcs_schedule.dataTransferObjects;

import java.io.Serializable;

public class Week implements Serializable {
    public WeekType weekType;
    private DayOfWeek[] days;

    public Week(WeekType _wt, DayOfWeek[] d)
    {
        weekType=_wt;
        days=d;
    }

    public DayOfWeek[] getDays() {
        return days;
    }

    public String getWeekType()
    {
        return weekType.toString();
    }



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<days.length;i++)
        {
            sb.append(days[i].toString());
        }
        return sb.toString();
    }
}
