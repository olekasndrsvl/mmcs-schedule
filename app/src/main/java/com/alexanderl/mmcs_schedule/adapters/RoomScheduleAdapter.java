package com.alexanderl.mmcs_schedule.adapters;

import com.alexanderl.mmcs_schedule.API.primitives.*;
import com.alexanderl.mmcs_schedule.dataTransferObjects.*;

import java.util.*;

public class RoomScheduleAdapter {

    public static Week convertToWeek(RawScheduleOfRoom rawSchedule, WeekType weekType) {
        if (rawSchedule == null) {
            return createEmptyWeek(weekType);
        }

        // Создаем мапу для быстрого доступа к урокам по ID
        Map<Integer, RawLesson> lessonMap = new HashMap<>();
        for (RawLesson rawLesson : rawSchedule.getLessons()) {
            lessonMap.put(rawLesson.getId(), rawLesson);
        }

        // Создаем мапу для имен групп по uberid
        Map<Integer, String> groupNameMap = new HashMap<>();
        if (rawSchedule.getGroups() != null) {
            for (RawGroupAtLesson group : rawSchedule.getGroups()) {
                groupNameMap.put(group.getUberId(), group.getName());
            }
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
        // Инициализируем все дни от 0 до 5
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
                        Lesson convertedLesson = convertToGroupedLesson(curriculaList, timeslot, lesson, weekType, groupNameMap, lessonMap);
                        if (convertedLesson != null) {
                            // Проверяем, что день недели в допустимом диапазоне
                            if (timeslot.dayOfWeek >= 0 && timeslot.dayOfWeek < 6) {
                                List<Lesson> dayLessons = lessonsByDay.get(timeslot.dayOfWeek);
                                if (dayLessons != null) {
                                    dayLessons.add(convertedLesson);
                                } else {
                                    // Если списка для этого дня нет, создаем его
                                    lessonsByDay.put(timeslot.dayOfWeek, new ArrayList<>());
                                    lessonsByDay.get(timeslot.dayOfWeek).add(convertedLesson);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Сортируем занятия по времени начала для каждого дня
        for (List<Lesson> dayLessons : lessonsByDay.values()) {
            if (dayLessons != null) {
                dayLessons.sort(Comparator.comparing(Lesson::getTbegin,
                        Comparator.comparing(Time::getHours).thenComparing(Time::getMinutes)));
            }
        }

        // Создаем дни недели
        DayOfWeek[] days = new DayOfWeek[6];
        String[] dayNames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

        for (int i = 0; i < 6; i++) {
            List<Lesson> dayLessons = lessonsByDay.get(i);
            boolean isDayOff = (dayLessons == null || dayLessons.isEmpty());
            days[i] = new DayOfWeek(dayNames[i], isDayOff,
                    isDayOff ? new Lesson[0] : dayLessons.toArray(new Lesson[0]));
        }

        return new Week(weekType, days);
    }

    private static Lesson convertToGroupedLesson(List<RawCurriculum> curriculaList, TimeslotData timeslot,
                                                 RawLesson rawLesson, WeekType targetWeekType,
                                                 Map<Integer, String> groupNameMap, Map<Integer, RawLesson> lessonMap) {
        try {
            if (curriculaList == null || curriculaList.isEmpty()) return null;

            RawCurriculum firstCurriculum = curriculaList.get(0);

            // Создаем время начала и окончания
            Time startTime = new Time(timeslot.startHours, timeslot.startMinutes);
            Time endTime = new Time(timeslot.endHours, timeslot.endMinutes);

            // Для аудитории собираем уникальные комнаты
            Room[] rooms = getUniqueRooms(curriculaList);

            // Собираем уникальных преподавателей
            Teacher[] teachers = getUniqueTeachers(curriculaList);

            // Формируем название предмета с информацией о преподавателях и группах
            String subjectNameWithInfo = buildSubjectNameWithInfo(
                    firstCurriculum.getSubjectName(),
                    curriculaList,
                    teachers,
                    groupNameMap,
                    lessonMap
            );

            // Формируем дополнительную информацию о типе недели и другой информации
            String additionalInfo = buildAdditionalInfo(rawLesson, timeslot.weekType, targetWeekType);

            return new Lesson(startTime, endTime, subjectNameWithInfo, additionalInfo, rooms, teachers);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String buildSubjectNameWithInfo(String baseSubjectName, List<RawCurriculum> curriculaList,
                                                   Teacher[] teachers, Map<Integer, String> groupNameMap,
                                                   Map<Integer, RawLesson> lessonMap) {
        if (baseSubjectName == null) {
            baseSubjectName = "Не указано";
        }

        StringBuilder subjectNameBuilder = new StringBuilder(baseSubjectName);

        // Добавляем информацию о преподавателях
        if (teachers != null && teachers.length > 0) {
            subjectNameBuilder.append(" (");
            for (int i = 0; i < teachers.length; i++) {
                if (i > 0) subjectNameBuilder.append(", ");
                // Берем только фамилию преподавателя для краткости
                String teacherName = teachers[i].toString();
                String[] nameParts = teacherName.split(" ");
                if (nameParts.length > 0) {
                    subjectNameBuilder.append(nameParts[0]); // Фамилия
                } else {
                    subjectNameBuilder.append(teacherName);
                }
            }
            subjectNameBuilder.append(")");
        }

        // Добавляем информацию о группах
        Set<String> groups = new TreeSet<>();
        for (RawCurriculum curriculum : curriculaList) {
            RawLesson lesson = lessonMap.get(curriculum.getLessonId());
            if (lesson != null && groupNameMap != null) {
                String groupName = groupNameMap.get(lesson.getUberId());
                if (groupName != null && !groupName.isEmpty()) {
                    groups.add(groupName);
                }
            }
        }

        if (!groups.isEmpty()) {
            subjectNameBuilder.append(" - ");
            subjectNameBuilder.append(String.join(", ", groups));
        }

        return subjectNameBuilder.toString();
    }

    private static String buildAdditionalInfo(RawLesson rawLesson, String lessonWeekType, WeekType targetWeekType) {
        StringBuilder info = new StringBuilder();

        // Добавляем информацию о типе недели, если отображается COMBINED неделя
        if (targetWeekType == WeekType.COMBINBED) {
            String weekTypeInfo = getWeekTypeDisplayName(lessonWeekType);
            if (weekTypeInfo != null && !weekTypeInfo.isEmpty()) {
                if (info.length() > 0) info.append(", ");
                info.append(weekTypeInfo);
            }
        }

        // Добавляем информацию из урока если есть
        if (rawLesson != null && rawLesson.getInfo() != null && !rawLesson.getInfo().isEmpty()) {
            if (info.length() > 0) info.append(", ");
            info.append(rawLesson.getInfo());
        }

        return info.length() > 0 ? info.toString() : "";
    }

    private static Room[] getUniqueRooms(List<RawCurriculum> curriculaList) {
        if (curriculaList == null || curriculaList.isEmpty()) return new Room[0];

        Map<Integer, Room> uniqueRooms = new HashMap<>();
        for (RawCurriculum curriculum : curriculaList) {
            if (curriculum.getRoomName() != null && !curriculum.getRoomName().trim().isEmpty()) {
                int roomId = curriculum.getRoomId();
                if (!uniqueRooms.containsKey(roomId)) {
                    uniqueRooms.put(roomId, new Room(curriculum.getRoomId(), curriculum.getRoomName()));
                }
            }
        }

        return uniqueRooms.values().toArray(new Room[0]);
    }

    private static Teacher[] getUniqueTeachers(List<RawCurriculum> curriculaList) {
        if (curriculaList == null || curriculaList.isEmpty()) return new Teacher[0];

        Map<String, Teacher> uniqueTeachers = new HashMap<>();
        for (RawCurriculum curriculum : curriculaList) {
            String teacherName = curriculum.getTeacherName();
            if (teacherName != null && !teacherName.trim().isEmpty() && !teacherName.equals("null")) {
                String teacherFullName = teacherName.trim();
                if (!uniqueTeachers.containsKey(teacherFullName)) {
                    uniqueTeachers.put(teacherFullName, new Teacher(curriculum.getTeacherId(), teacherFullName,
                            curriculum.getTeacherDegree() != null ? curriculum.getTeacherDegree() : ""));
                }
            }
        }

        return uniqueTeachers.values().toArray(new Teacher[0]);
    }

    private static String getWeekTypeDisplayName(String weekType) {
        if (weekType == null) return "";

        switch (weekType.toLowerCase()) {
            case "upper":
                return "верхняя неделя";
            case "lower":
                return "нижняя неделя";
            case "full":
                return "";
            default:
                return weekType;
        }
    }

    private static TimeslotData parseTimeslot(String timeslot) {
        if (timeslot == null || !timeslot.startsWith("(") || !timeslot.endsWith(")")) {
            return null;
        }

        try {
            String content = timeslot.substring(1, timeslot.length() - 1);
            String[] parts = content.split(",");

            if (parts.length < 4) return null;

            int dayOfWeek = Integer.parseInt(parts[0].trim()) - 1; // Convert to 0-based index

            // Проверяем корректность дня недели
            if (dayOfWeek < 0 || dayOfWeek > 5) {
                return null;
            }

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
        if (timeslotWeekType == null) return false;

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
        DayOfWeek[] days = new DayOfWeek[6];
        String[] dayNames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

        for (int i = 0; i < 6; i++) {
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