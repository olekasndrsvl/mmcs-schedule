package com.alexanderl.mmcs_schedule.adapters;

import com.alexanderl.mmcs_schedule.API.primitives.RawCurriculum;
import com.alexanderl.mmcs_schedule.API.primitives.RawLesson;
import com.alexanderl.mmcs_schedule.API.primitives.RawScheduleOfTeacher;
import com.alexanderl.mmcs_schedule.dataTransferObjects.DayOfWeek;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Lesson;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Week;
import com.alexanderl.mmcs_schedule.dataTransferObjects.WeekType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherScheduleAdapter {
    public static Week convertToWeek(RawScheduleOfTeacher scheduleOfTeacher, WeekType weekType){
        if(scheduleOfTeacher == null)
            return createEmptyWeek(weekType);

        // Создаем словарь для быстрого доступа к урокам по id и заполняем его
        Map<Integer, RawLesson> lessonMap = new HashMap<>();
        for (RawLesson rawLesson : scheduleOfTeacher.getLessons())
            lessonMap.put(rawLesson.getId(), rawLesson);

        // Группируем учебные планы по ключу: день_недели + время_начала + время_окончания + предмет
        Map<String, List<RawCurriculum>> groupedCurricula = new HashMap<>();

        return null;
    }
    private static Week createEmptyWeek(WeekType weekType) {
        DayOfWeek[] days = new DayOfWeek[7];
        String[] dayNames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

        for (int i = 0; i < 7; i++)
            days[i] = new DayOfWeek(dayNames[i], true, new Lesson[0]);

        return new Week(weekType, days);
    }
}
