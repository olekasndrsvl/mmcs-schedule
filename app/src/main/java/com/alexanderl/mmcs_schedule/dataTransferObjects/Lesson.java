package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Lesson {

    private Time tbegin;
    private Time tend;
    private String curriculaName;
    private String additional_info;
    private Room[] room;
    private Teacher[] teachers;

    public Lesson(Time tb, Time te, String curriculaName, String additional_info, Room[] room, Teacher[] teacher) {
        this.tbegin = tb;
        this.tend=te;
        this.curriculaName = curriculaName;
        this.additional_info = additional_info;
        this.room = room;
        this.teachers = teacher;
    }

    public Room[] getRooms() {
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

    public Teacher[] getTeachers() {
        return teachers;
    }

    @Override
    public String toString()
    {
        return tbegin+"-"+tend+" "+curriculaName+" "+ room+ " "+teachers[0]+ " "+additional_info;
    }


}
