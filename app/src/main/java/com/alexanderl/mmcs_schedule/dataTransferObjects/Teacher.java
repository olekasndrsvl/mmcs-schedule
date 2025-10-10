package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Teacher
{
    private String name;
    private String degeree;

    private int teacherId;
    public Teacher(int teacherid, String _name, String _degeree)
    {
        name=_name;
        degeree=_degeree;
        teacherId=teacherid;

    }
    @Override
    public String toString()
    {
        return degeree+" "+name;
    }
}
