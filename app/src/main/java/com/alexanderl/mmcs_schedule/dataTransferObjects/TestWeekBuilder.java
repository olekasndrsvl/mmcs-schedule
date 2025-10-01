package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class TestWeekBuilder {

    public static Week createTestWeek() {
        // Создаем учителей
        Teacher teacher1 = new Teacher("Иванов Иван Иванович", "доц.");
        Teacher teacher2 = new Teacher("Петрова Мария Сергеевна", "проф.");
        Teacher teacher3 = new Teacher("Сидоров Алексей Петрович", "ст.преп.");
        Teacher teacher4 = new Teacher("Кузнецова Ольга Владимировна", "асс.");

        // Создаем аудитории
        Room room101 = new Room(101);
        Room room205 = new Room(205);
        Room room310 = new Room(310);
        Room room412 = new Room(412);
        Room room115 = new Room(115);
        Room room201 = new Room(201);

        // Создаем уроки для понедельника
        Lesson[] mondayLessons = new Lesson[3];
        mondayLessons[0] = new Lesson(
                new Time(9, 0), new Time(10, 30),
                "Математический анализ", "Лекция",
                room101, teacher1
        );
        mondayLessons[1] = new Lesson(
                new Time(10, 45), new Time(12, 15),
                "Физика", "Практика",
                room205, teacher2
        );
        mondayLessons[2] = new Lesson(
                new Time(13, 0), new Time(14, 30),
                "Программирование", "Лабораторная",
                room310, teacher3
        );

        // Создаем уроки для вторника
        Lesson[] tuesdayLessons = new Lesson[2];
        tuesdayLessons[0] = new Lesson(
                new Time(9, 30), new Time(11, 0),
                "История", "Лекция",
                room115, teacher4
        );
        tuesdayLessons[1] = new Lesson(
                new Time(11, 15), new Time(12, 45),
                "Иностранный язык", "Практика",
                room205, teacher2
        );

        // Создаем уроки для среды
        Lesson[] wednesdayLessons = new Lesson[3];
        wednesdayLessons[0] = new Lesson(
                new Time(8, 30), new Time(10, 0),
                "Химия", "Лекция",
                room310, teacher1
        );
        wednesdayLessons[1] = new Lesson(
                new Time(10, 15), new Time(11, 45),
                "Физкультура", "Практика",
                room412, teacher3
        );
        wednesdayLessons[2] = new Lesson(
                new Time(12, 0), new Time(13, 30),
                "Экономика", "Семинар",
                room101, teacher4
        );

        // Создаем уроки для четверга
        Lesson[] thursdayLessons = new Lesson[2];
        thursdayLessons[0] = new Lesson(
                new Time(9, 0), new Time(10, 30),
                "Базы данных", "Лекция",
                room205, teacher3
        );
        thursdayLessons[1] = new Lesson(
                new Time(10, 45), new Time(12, 15),
                "Веб-разработка", "Лабораторная",
                room310, teacher1
        );

        // Создаем уроки для пятницы
        Lesson[] fridayLessons = new Lesson[4];
        fridayLessons[0] = new Lesson(
                new Time(8, 0), new Time(9, 30),
                "Алгоритмы", "Лекция",
                room101, teacher2
        );
        fridayLessons[1] = new Lesson(
                new Time(9, 45), new Time(11, 15),
                "Операционные системы", "Практика",
                room205, teacher3
        );
        fridayLessons[2] = new Lesson(
                new Time(11, 30), new Time(13, 0),
                "Сети", "Лекция",
                room310, teacher1
        );
        fridayLessons[3] = new Lesson(
                new Time(14, 0), new Time(15, 30),
                "Проектная деятельность", "Семинар",
                room412, teacher4
        );

        // Создаем уроки для субботы
        Lesson[] saturdayLessons = new Lesson[1];
        saturdayLessons[0] = new Lesson(
                new Time(10, 0), new Time(11, 30),
                "Элективный курс", "Семинар",
                room115, teacher2
        );

        // Воскресенье - выходной
        Lesson[] sundayLessons = new Lesson[0];

        // Создаем дни недели
        DayOfWeek[] days = new DayOfWeek[7];
        days[0] = new DayOfWeek("Понедельник", false, mondayLessons);
        days[1] = new DayOfWeek("Вторник", false, tuesdayLessons);
        days[2] = new DayOfWeek("Среда", false, wednesdayLessons);
        days[3] = new DayOfWeek("Четверг", false, thursdayLessons);
        days[4] = new DayOfWeek("Пятница", false, fridayLessons);
        days[5] = new DayOfWeek("Суббота", false, saturdayLessons);
        days[6] = new DayOfWeek("Воскресенье", true, sundayLessons);

        // Создаем неделю
        Week week = new Week(WeekType.UPPER, days);

        return week;
    }

    // Дополнительный метод для создания недели с нижней неделей
    public static Week createLowerWeek() {
        // Создаем учителей
        Teacher teacher1 = new Teacher("Иванов Иван Иванович", "доц.");
        Teacher teacher3 = new Teacher("Сидоров Алексей Петрович", "ст.преп.");
        Teacher teacher5 = new Teacher("Васильев Дмитрий Николаевич", "проф.");

        // Создаем аудитории
        Room room201 = new Room(201);
        Room room305 = new Room(305);
        Room room410 = new Room(410);

        // Создаем уроки для понедельника (меньше занятий на нижней неделе)
        Lesson[] mondayLessons = new Lesson[2];
        mondayLessons[0] = new Lesson(
                new Time(9, 0), new Time(10, 30),
                "Математический анализ", "Семинар",
                room201, teacher1
        );
        mondayLessons[1] = new Lesson(
                new Time(11, 0), new Time(12, 30),
                "Физика", "Лабораторная",
                room305, teacher5
        );

        // Создаем уроки для вторника
        Lesson[] tuesdayLessons = new Lesson[1];
        tuesdayLessons[0] = new Lesson(
                new Time(10, 0), new Time(11, 30),
                "Программирование", "Лекция",
                room410, teacher3
        );

        // Среда - выходной на нижней неделе
        Lesson[] wednesdayLessons = new Lesson[0];

        // Создаем уроки для четверга
        Lesson[] thursdayLessons = new Lesson[2];
        thursdayLessons[0] = new Lesson(
                new Time(9, 30), new Time(11, 0),
                "Базы данных", "Практика",
                room201, teacher3
        );
        thursdayLessons[1] = new Lesson(
                new Time(11, 15), new Time(12, 45),
                "Веб-разработка", "Лекция",
                room305, teacher1
        );

        // Пятница - один урок
        Lesson[] fridayLessons = new Lesson[1];
        fridayLessons[0] = new Lesson(
                new Time(13, 0), new Time(14, 30),
                "Проектная деятельность", "Консультация",
                room410, teacher5
        );

        // Суббота и воскресенье - выходные
        Lesson[] saturdayLessons = new Lesson[0];
        Lesson[] sundayLessons = new Lesson[0];

        // Создаем дни недели
        DayOfWeek[] days = new DayOfWeek[7];
        days[0] = new DayOfWeek("Понедельник", false, mondayLessons);
        days[1] = new DayOfWeek("Вторник", false, tuesdayLessons);
        days[2] = new DayOfWeek("Среда", true, wednesdayLessons);
        days[3] = new DayOfWeek("Четверг", false, thursdayLessons);
        days[4] = new DayOfWeek("Пятница", false, fridayLessons);
        days[5] = new DayOfWeek("Суббота", true, saturdayLessons);
        days[6] = new DayOfWeek("Воскресенье", true, sundayLessons);

        // Создаем неделю с нижним типом
        Week week = new Week(WeekType.LOWER, days);

        return week;
    }

    // Метод для создания комбинированной недели
    public static Week createCombinedWeek() {
        Week upperWeek = createTestWeek();
        Week lowerWeek = createLowerWeek();

        // Здесь можно добавить логику объединения недель
        // Пока просто возвращаем верхнюю неделю как комбинированную
        return new Week(WeekType.COMBINBED, upperWeek.getDays());
    }

    // Метод для тестирования вывода
    public static void printWeekSchedule(Week week) {
        System.out.println("=== " + week.getWeekType() + " неделя ===");

        DayOfWeek[] days = week.getDays();
        for (DayOfWeek day : days) {
            System.out.println("\n--- " + day.getDayname() + " ---");

            if (day.isDayOff) {
                System.out.println("Выходной день");
            } else {
                Lesson[] lessons = day.lesson;
                if (lessons.length == 0) {
                    System.out.println("Занятий нет");
                } else {
                    for (Lesson lesson : lessons) {
                        System.out.println(lesson.toString());
                    }
                }
            }
        }
    }
}