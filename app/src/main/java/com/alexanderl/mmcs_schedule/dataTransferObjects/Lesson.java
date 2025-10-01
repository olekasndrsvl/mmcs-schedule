package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Lesson {

    private Time tbegin;
    private Time tend;
    private String curriculaName;
    private String additional_info;
    private Room room;
    private Teacher teacher;

    public Lesson(Time tb, Time te, String curriculaName, String additional_info, Room room, Teacher teacher) {
        this.tbegin = tb;
        this.tend=te;
        this.curriculaName = curriculaName;
        this.additional_info = additional_info;
        this.room = room;
        this.teacher = teacher;
    }

    public Room getRoom() {
        return room;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public String getCurriculaName() {
        return curriculaName;
    }

    public Time getTbegin() {
        return tbegin;
    }

    public Time getTend() {
        return tend;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString()
    {
        return tbegin+"-"+tend+" "+curriculaName+" "+ room+ " "+teacher+ " "+additional_info;
    }
}
