package com.alexanderl.mmcs_schedule.API.primitives;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScheduleAPI {

    @GET("/APIv1/grade/list")
    Call<RawGrade.List> getGrades();

    @GET("/APIv0/group/list/{gradeID}")
    Call<RawGroup.List> getGroups(@Path("gradeID") int gradeId);

    @GET("/APIv0/group/forUber/{uberID}")
    Call<RawGroup.List> getGroupsOfUberGroup(@Path("uberID") int uberId);

    @GET("/APIv0/room/list")
    Call<RawRoom.List> getRooms();

    @GET("/APIv0/schedule/lesson/{ID}")
    Call<RawCurriculaOfLesson> getCurriculaForLesson(@Path("ID") int id);

    @GET("/APIv0/schedule/group/{ID}")
    Call<RawScheduleOfGroup> getScheduleOfGroup(@Path("ID") int id);

    @GET("/APIv1/schedule/teacher/{ID}")
    Call<RawScheduleOfTeacher> getScheduleOfTeacher(@Path("ID") int id);

    @GET("/APIv0/subject/list")
    Call<RawSubject.List> getSubjects();

    @GET("/APIv0/teacher/list")
    Call<RawTeacher.List> getTeachers();

    @GET("/APIv0/time/week")
    Call<RawWeek> getCurrentWeek();

    @GET("/APIv0/time/list")
    Call<RawTimeSlot.List> getTimeSlots();
}