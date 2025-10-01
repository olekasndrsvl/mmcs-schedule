package com.alexanderl.mmcs_schedule.API.primitives;

import java.io.Serializable;
import java.util.List;

public class RawScheduleOfTeacher
    implements Serializable {

    private List<RawLesson>        lessons   = null;
    private List<RawCurriculum>    curricula = null;
    private List<RawGroupAtLesson> groups    = null;

    public RawScheduleOfTeacher(List<RawLesson> lessons, List<RawCurriculum> curricula,
                                List<RawGroupAtLesson> groups) {
        this.lessons = lessons;
        this.curricula = curricula;
        this.groups = groups;
    }

    public List<RawLesson> getLessons() {
        return lessons;
    }

    public List<RawCurriculum> getCurricula() {
        return curricula;
    }

    public List<RawGroupAtLesson> getGroups() {
        return groups;
    }
}
