package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class DayOfWeek {
    private String dayname;
    boolean isDayOff;
    Lesson[] lesson;

    public DayOfWeek(String day,boolean isdayoff, Lesson[] lessons)
    {
        dayname=day;
        lesson=lessons;
        isDayOff=isdayoff;
    }

    public Lesson[] getLessons() {
        return lesson;
    }

    public String getDayname()
    {
        return dayname;
    }
    public int getDayIndex()
    {
        switch (dayname.toLowerCase()) {
            case "понедельник":
                return 0;
            case "вторник":
                return 1;
            case "среда":
                return 2;
            case "четверг":
                return 3;
            case "пятница":
                return 4;
            case "суббота":
                return 5;
            case "воскресенье":
                return 6;
            default:
                return -1; // или выбросить исключение
        }
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(dayname);
        sb.append('\n');
        for(int i=0; i<lesson.length;i++)
        {
            sb.append(lesson[i].toString());
            sb.append('\n');
        }
        return sb.toString();
    }


}
