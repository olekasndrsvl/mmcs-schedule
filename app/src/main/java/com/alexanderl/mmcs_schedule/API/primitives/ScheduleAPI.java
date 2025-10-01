package com.alexanderl.mmcs_schedule.API.primitives;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface ScheduleAPI {

    @GET("/APIv1/grade/list")
    RawGrade.List getGrades();

    @GET("/APIv0/group/list/{gradeID}")
    RawGroup.List getGroups(@Path("gradeID") int gradeId);

    @GET("/APIv0/group/forUber/{uberID}")
    RawGroup.List getGroupsOfUberGroup(@Path("uberID") int uberId);

    @GET("/APIv0/room/list")
    RawRoom.List getRooms();

    @GET("/APIv0/schedule/lesson/{ID}")
    RawCurriculaOfLesson getCurriculaForLesson(@Path("ID") int id);

    @GET("/APIv0/schedule/group/{ID}")
    RawScheduleOfGroup getScheduleOfGroup(@Path("ID") int id);

    @GET("/APIv1/schedule/teacher/{ID}")
    RawScheduleOfTeacher getScheduleOfTeacher(@Path("ID") int id);

    @GET("/APIv0/subject/list")
    RawSubject.List getSubjects();

    @GET("/APIv0/teacher/list")
    RawTeacher.List getTeachers();

    @GET("/APIv0/time/week")
    RawWeek getCurrentWeek();

    @GET("/APIv0/time/list")
    RawTimeSlot.List getTimeSlots();
}