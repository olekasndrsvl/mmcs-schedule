package com.alexanderl.mmcs_schedule.API.primitives;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleService {
    private static final String BASE_URL = "https://schedule.sfedu.ru/";
    private static ScheduleAPI api;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ScheduleAPI.class);
    }

    public static Call<RawScheduleOfGroup> getGroupSchedule(int groupId) {
        return api.getScheduleOfGroup(groupId);
    }
    public static Call<RawGrade.List> getGrades() {
        return api.getGrades();
    }
    public static Call<RawGroup.List> getGroups(int gradeId) {
        return api.getGroups(gradeId);
    }
    public static Call<RawWeek> getWeekType()
    {
        return api.getCurrentWeek();
    }
    public static Call<RawTeacher.List> getTeachers(){ return api.getTeachers(); }
}