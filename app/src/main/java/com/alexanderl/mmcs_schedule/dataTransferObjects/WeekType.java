package com.alexanderl.mmcs_schedule.dataTransferObjects;

public enum WeekType {
    UPPER("Верхняя"),
    LOWER("Нижняя"),
    COMBINBED("Верхняя и нижняя");

    private String _title;
    WeekType(String s) {
        _title=s;
    }
    @Override
    public String toString()
    {
        return _title;
    }

}
