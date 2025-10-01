package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Teacher
{
    private String name;
    private String degeree;

    public Teacher(String _name, String _degeree)
    {
        name=_name;
        degeree=_degeree;
    }
    @Override
    public String toString()
    {
        return degeree+" "+name;
    }
}
