package com.alexanderl.mmcs_schedule.adapters;

import com.alexanderl.mmcs_schedule.API.primitives.*;
import com.alexanderl.mmcs_schedule.dataTransferObjects.*;

import java.util.*;

public class ScheduleAdapter {

    public static Week convertToWeek(RawScheduleOfGroup rawSchedule, WeekType weekType) {
        if (rawSchedule == null) {
            return createEmptyWeek(weekType);
        }

        // Создаем мапу для быстрого доступа к урокам по ID
        Map<Integer, RawLesson> lessonMap = new HashMap<>();
        for (RawLesson rawLesson : rawSchedule.getLessons()) {
            lessonMap.put(rawLesson.getId(), rawLesson);
        }

        // Группируем учебные планы по ключу: день_недели + время_начала + время_окончания + предмет
        Map<String, List<RawCurriculum>> groupedCurricula = new HashMap<>();

        for (RawCurriculum curriculum : rawSchedule.getCurricula()) {
            RawLesson lesson = lessonMap.get(curriculum.getLessonId());
            if (lesson != null) {
                TimeslotData timeslot = parseTimeslot(lesson.getTimeSlot());
                if (timeslot != null && matchesWeekType(timeslot.weekType, weekType)) {
                    String groupKey = timeslot.dayOfWeek + "_" +
                            timeslot.startHours + "_" + timeslot.startMinutes + "_" +
                            timeslot.endHours + "_" + timeslot.endMinutes + "_" +
                            curriculum.getSubjectId();

                    if (!groupedCurricula.containsKey(groupKey)) {
                        groupedCurricula.put(groupKey, new ArrayList<>());
                    }
                    groupedCurricula.get(groupKey).add(curriculum);
                }
            }
        }

        // Создаем уроки из сгруппированных учебных планов
        Map<Integer, List<Lesson>> lessonsByDay = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            lessonsByDay.put(i, new ArrayList<>());
        }

        for (Map.Entry<String, List<RawCurriculum>> entry : groupedCurricula.entrySet()) {
            List<RawCurriculum> curriculaList = entry.getValue();
            if (!curriculaList.isEmpty()) {
                RawCurriculum firstCurriculum = curriculaList.get(0);
                RawLesson lesson = lessonMap.get(firstCurriculum.getLessonId());
                if (lesson != null) {
                    TimeslotData timeslot = parseTimeslot(lesson.getTimeSlot());
                    if (timeslot != null) {
                        Lesson convertedLesson = convertToGroupedLesson(curriculaList, timeslot, lesson);
                        if (convertedLesson != null) {
                            lessonsByDay.get(timeslot.dayOfWeek).add(convertedLesson);
                        }
                    }
                }
            }
        }

        // Сортируем занятия по времени начала для каждого дня
        for (List<Lesson> dayLessons : lessonsByDay.values()) {
            dayLessons.sort(Comparator.comparing(Lesson::getTbegin,
                    Comparator.comparing(Time::getHours).thenComparing(Time::getMinutes)));
        }

        // Создаем дни недели
        DayOfWeek[] days = new DayOfWeek[6];
        String[] dayNames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

        for (int i = 0; i < 6; i++) {
            List<Lesson> dayLessons = lessonsByDay.get(i);
            boolean isDayOff = dayLessons.isEmpty();
            days[i] = new DayOfWeek(dayNames[i], isDayOff, dayLessons.toArray(new Lesson[0]));
        }

        return new Week(weekType, days);
    }

    private static Lesson convertToGroupedLesson(List<RawCurriculum> curriculaList, TimeslotData timeslot, RawLesson rawLesson) {
        try {
            if (curriculaList.isEmpty()) return null;

            RawCurriculum firstCurriculum = curriculaList.get(0);

            // Создаем время начала и окончания
            Time startTime = new Time(timeslot.startHours, timeslot.startMinutes);
            Time endTime = new Time(timeslot.endHours, timeslot.endMinutes);

            // Определяем аудиторию (если все одинаковые - берем первую, иначе указываем несколько)
            String roomInfo = getGroupedRoomInfo(curriculaList);
            Room room = new Room(0); // Создаем фиктивную комнату, т.к. может быть несколько

            // Определяем преподавателя (если все одинаковые - берем первого, иначе указываем несколько)
            String teacherInfo = getGroupedTeacherInfo(curriculaList);
            Teacher teacher = new Teacher(teacherInfo, "");

            // Формируем дополнительную информацию с учетом подгрупп
            String additionalInfo = buildGroupedAdditionalInfo(curriculaList, rawLesson, roomInfo);

            return new Lesson(startTime, endTime, firstCurriculum.getSubjectName(), additionalInfo, room, teacher);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getGroupedRoomInfo(List<RawCurriculum> curriculaList) {
        if (curriculaList.isEmpty()) return "Ауд. не указана";

        Set<String> uniqueRooms = new HashSet<>();
        for (RawCurriculum curriculum : curriculaList) {
            if (curriculum.getRoomName() != null && !curriculum.getRoomName().trim().isEmpty()) {
                uniqueRooms.add(curriculum.getRoomName());
            }
        }

        if (uniqueRooms.isEmpty()) return "Ауд. не указана";
        if (uniqueRooms.size() == 1) return "Ауд. " + uniqueRooms.iterator().next();

        // Если аудиторий несколько, формируем строку
        return "Ауд. " + String.join(", ", uniqueRooms);
    }

    private static String getGroupedTeacherInfo(List<RawCurriculum> curriculaList) {
        if (curriculaList.isEmpty()) return "Преподаватель не указан";

        Set<String> uniqueTeachers = new HashSet<>();
        for (RawCurriculum curriculum : curriculaList) {
            String teacherName = curriculum.getTeacherName();
            if (teacherName != null && !teacherName.trim().isEmpty() && !teacherName.equals("null")) {
                uniqueTeachers.add(teacherName.trim());
            }
        }

        if (uniqueTeachers.isEmpty()) return "Преподаватель не указан";
        if (uniqueTeachers.size() == 1) return uniqueTeachers.iterator().next();

        // Если преподавателей несколько
        return "Несколько преподавателей";
    }

    private static String buildGroupedAdditionalInfo(List<RawCurriculum> curriculaList, RawLesson rawLesson, String roomInfo) {
        StringBuilder info = new StringBuilder();




        // Добавляем информацию о подгруппах
        Set<Integer> subGroups = new TreeSet<>();
        for (RawCurriculum curriculum : curriculaList) {
            if (curriculum.getSubNum() > 0) {
                subGroups.add(curriculum.getSubNum());
            }
        }

        if (!subGroups.isEmpty()) {
            if (info.length() > 0) info.append(", ");
            if (subGroups.size() == 1) {
                info.append("подгр. ").append(subGroups.iterator().next());
            } else {
                info.append("подгр. ").append(formatSubgroups(subGroups));
            }
        }

        // Добавляем информацию об аудитории
        if (info.length() > 0) info.append(", ");
        info.append(roomInfo);

        // Добавляем информацию из урока если есть
        if (rawLesson.getInfo() != null && !rawLesson.getInfo().isEmpty()) {
            if (info.length() > 0) info.append(", ");
            info.append(rawLesson.getInfo());
        }

        return info.toString();
    }

    private static String formatSubgroups(Set<Integer> subGroups) {
        List<Integer> sorted = new ArrayList<>(subGroups);
        Collections.sort(sorted);

        // Пытаемся найти последовательности
        List<String> ranges = new ArrayList<>();
        int start = sorted.get(0);
        int end = start;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) == end + 1) {
                end = sorted.get(i);
            } else {
                if (start == end) {
                    ranges.add(String.valueOf(start));
                } else {
                    ranges.add(start + "-" + end);
                }
                start = sorted.get(i);
                end = start;
            }
        }

        if (start == end) {
            ranges.add(String.valueOf(start));
        } else {
            ranges.add(start + "-" + end);
        }

        return String.join(",", ranges);
    }

    // Остальные методы остаются без изменений
    private static TimeslotData parseTimeslot(String timeslot) {
        if (timeslot == null || !timeslot.startsWith("(") || !timeslot.endsWith(")")) {
            return null;
        }

        try {
            String content = timeslot.substring(1, timeslot.length() - 1);
            String[] parts = content.split(",");

            if (parts.length < 4) return null;

            int dayOfWeek = Integer.parseInt(parts[0].trim());

            String[] startTimeParts = parts[1].trim().split(":");
            int startHours = Integer.parseInt(startTimeParts[0]);
            int startMinutes = Integer.parseInt(startTimeParts[1]);

            String[] endTimeParts = parts[2].trim().split(":");
            int endHours = Integer.parseInt(endTimeParts[0]);
            int endMinutes = Integer.parseInt(endTimeParts[1]);

            String weekType = parts[3].trim().toLowerCase();

            return new TimeslotData(dayOfWeek, startHours, startMinutes, endHours, endMinutes, weekType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean matchesWeekType(String timeslotWeekType, WeekType targetWeekType) {
        if ("full".equals(timeslotWeekType)) {
            return true;
        }

        switch (targetWeekType) {
            case UPPER:
                return "upper".equals(timeslotWeekType);
            case LOWER:
                return "lower".equals(timeslotWeekType);
            case COMBINBED:
                return true;
            default:
                return false;
        }
    }

    private static Week createEmptyWeek(WeekType weekType) {
        DayOfWeek[] days = new DayOfWeek[7];
        String[] dayNames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

        for (int i = 0; i < 7; i++) {
            days[i] = new DayOfWeek(dayNames[i], true, new Lesson[0]);
        }

        return new Week(weekType, days);
    }

    private static class TimeslotData {
        int dayOfWeek;
        int startHours;
        int startMinutes;
        int endHours;
        int endMinutes;
        String weekType;

        TimeslotData(int dayOfWeek, int startHours, int startMinutes, int endHours, int endMinutes, String weekType) {
            this.dayOfWeek = dayOfWeek;
            this.startHours = startHours;
            this.startMinutes = startMinutes;
            this.endHours = endHours;
            this.endMinutes = endMinutes;
            this.weekType = weekType;
        }
    }
}