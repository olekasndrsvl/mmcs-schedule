package com.alexanderl.mmcs_schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alexanderl.mmcs_schedule.API.primitives.RawScheduleOfGroup;
import com.alexanderl.mmcs_schedule.API.primitives.RawWeek;
import com.alexanderl.mmcs_schedule.API.primitives.ScheduleService;
import com.alexanderl.mmcs_schedule.adapters.DayPageAdapter;
import com.alexanderl.mmcs_schedule.adapters.ScheduleAdapter;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Lesson;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Room;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Teacher;
import com.alexanderl.mmcs_schedule.dataTransferObjects.TestWeekBuilder;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Week;
import com.alexanderl.mmcs_schedule.dataTransferObjects.WeekType;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageButton changeWeekButton;
    private TextView currentWeekTextView;
    private TextView groupNameTextView;
    private DayPageAdapter adapter;
    private  RawScheduleOfGroup response_week;
    private WeekType weekType;
    private PreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initViews();
        int type_of_schedule=1;
        int group_id=1;
        String groupName ="<group>";

        prefsManager = new PreferencesManager(this);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            type_of_schedule = arguments.getInt("schedule_type", 1);
            group_id = arguments.getInt("groupid", prefsManager.getSelectedGroupId());
            groupName = arguments.getString("groupname", prefsManager.getSelectedGroupName());

            // Сохраняем выбранную группу (на случай если пришли с новым выбором)
            if (arguments.containsKey("groupid")) {
                prefsManager.saveSelectedGroup(group_id, groupName);
            }
        } else {
            // Если Intent пустой, используем сохраненную группу
            group_id = prefsManager.getSelectedGroupId();
            groupName = prefsManager.getSelectedGroupName();
        }

        groupNameTextView.setText(groupName);

        getCurrentWeekType();
        loadScheduleFromAPI(group_id);


        changeWeekButton.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(ScheduleActivity.this, changeWeekButton);


            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(menuItem -> {

                String temp =menuItem.getTitle().toString();
                Week week;
                String wktp;
                switch (temp)
                {
                    case "Текущая неделя":
                        week = ScheduleAdapter.convertToWeek(response_week,weekType);
                        wktp = String.format("Текущая неделя:\n %s", weekType.toString());
                        currentWeekTextView.setText(wktp);

                        setupViewPager(week);
                        break;
                    case "Верхняя неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.UPPER);
                        wktp = String.format("Выбранная неделя:\n %s", WeekType.UPPER.toString());
                        currentWeekTextView.setText(wktp);
                        setupViewPager(week);
                        break;
                    case "Нижняя неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.LOWER);
                        wktp = String.format("Выбранная неделя:\n %s", WeekType.LOWER.toString());
                        currentWeekTextView.setText(wktp);
                        setupViewPager(week);
                        break;
                    case "Полная неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.COMBINBED);
                        wktp = String.format("Выбранная неделя:\n %s", WeekType.COMBINBED.toString());
                        currentWeekTextView.setText(wktp);
                        setupViewPager(week);
                        break;
                }

                // For debug
                //Toast.makeText(ScheduleActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });


            popupMenu.show();
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        changeWeekButton = findViewById(R.id.ChangeWeekButton);
        currentWeekTextView= findViewById(R.id.CurrentWeek);
        groupNameTextView = findViewById(R.id.group_name_textview);
    }

    private void getCurrentWeekType()
    {
        ScheduleService.getWeekType().enqueue(new Callback<RawWeek>() {
            @Override
            public void onResponse(Call<RawWeek> call, Response<RawWeek> response) {
                //hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Конвертируем raw данные в нашу модель
                    weekType = convertWeekType( response.body().getType());
                    Log.i("API", "Week type loaded successfully");
                }
            }

            @Override
            public void onFailure(Call<RawWeek> call, Throwable t) {
                //hideLoading();
                Toast.makeText(ScheduleActivity.this, "Не удалось получить тип недели!", Toast.LENGTH_SHORT).show();
                Log.e("API", "Week type error: " + t.getMessage());
            }
        });

    }
    private void loadScheduleFromAPI(int groupid) {
        // Показываем индикатор загрузки
        //showLoading();


        try {
            ScheduleService.getGroupSchedule(groupid).enqueue(new Callback<RawScheduleOfGroup>() {
                @Override
                public void onResponse(Call<RawScheduleOfGroup> call, Response<RawScheduleOfGroup> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            response_week = response.body();
                            Log.i("API", "Schedule loaded successfully");

                            Week week1 = ScheduleAdapter.convertToWeek(response_week, weekType);
                            String wktp = String.format("Текущая неделя:\n %s", weekType.toString());
                            currentWeekTextView.setText(wktp);
                            setupViewPager(week1);
                        } else {
                            useTestData();
                        }
                    } catch (Exception e) {
                        Log.e("ScheduleActivity", "Error processing schedule response", e);
                        useTestData();
                    }
                }

                @Override
                public void onFailure(Call<RawScheduleOfGroup> call, Throwable t) {
                    Log.e("API", "Schedule error: " + t.getMessage());
                    useTestData();
                    Toast.makeText(ScheduleActivity.this, "Используются тестовые данные", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.e("ScheduleActivity", "Error loading schedule from API", e);
            useTestData();
        }
    }

    @Override
    public void onBackPressed() {
        // При нажатии назад очищаем выбор группы и возвращаемся к MainActivity
        super.onBackPressed();
        prefsManager.clearSelectedGroup();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private WeekType convertWeekType(RawWeek.WeekType wt)
    {
        switch (wt)
        {
            case LOWER:
                return WeekType.LOWER;
            case UPPER:
                return WeekType.UPPER;
            default:
                return WeekType.COMBINBED;
        }
    }

    private void useTestData() {
        Week testWeek = TestWeekBuilder.createTestWeek();
        setupViewPager(testWeek);
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private void setupViewPager(Week w) {

        //w = TestWeekBuilder.createTestWeek();
        // Создаем адаптер
        adapter = new DayPageAdapter(this, w);

        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(getDayOfWeekByNumber(position + 1));
                    }
                }).attach();


    }

    @SuppressLint("SetTextI18n")
    void showLessonPopup(View anchorView, Lesson lesson) {
        // Создаем и настраиваем popup окно
        PopupWindow popupWindow = new PopupWindow(this);
        View popupView = getLayoutInflater().inflate(R.layout.popup_lesson_details, null);

        // Настраиваем содержимое popup
        TextView tvSubject = popupView.findViewById(R.id.tvSubject);
        TextView tvTeacher = popupView.findViewById(R.id.tvTeacher);
        TextView tvTime = popupView.findViewById(R.id.tvTime);
        TextView tvClassroom = popupView.findViewById(R.id.tvClassroom);

        Button btnClose = popupView.findViewById(R.id.btnClose);

        tvSubject.setText(lesson.getCurriculaName());

        // Формируем строку преподавателей
        StringBuilder teachersString = new StringBuilder();
        for (Teacher teacher : lesson.getTeachers()) {
            if (teachersString.length() > 0) {
                teachersString.append("\n");
            }
            teachersString.append(teacher.toString());
        }
        tvTeacher.setText( (teachersString.length() > 0 ? teachersString.toString() : "не указан"));

        tvTime.setText("Время: " + lesson.getTbegin() + " - " + lesson.getTend());

        // Формируем строку аудиторий
        StringBuilder roomsString = new StringBuilder();
        for (Room room : lesson.getRooms()) {
            if (roomsString.length() > 0) {
                roomsString.append(", ");
            }
            roomsString.append(room.getRoomName());
        }
        tvClassroom.setText( (roomsString.length() > 0 ? roomsString.toString() : "не указана"));



        // Обработчик закрытия
        btnClose.setOnClickListener(v -> popupWindow.dismiss());

        // Настройка popup window для отображения по центру на всю ширину
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Показываем popup по центру экрана
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }


    private String getDayOfWeekByNumber(int num)
    {
        switch (num)
        {
            case 1:
                return "ПН";
            case 2:
                return "ВТ";
            case 3:
                return "СР";
            case 4:
                return "ЧТ";
            case 5:
                return "ПТ";
            case 6:
                return "СБ";
            default:
                return "Unknown!";
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_DAY", viewPager.getCurrentItem());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
            int currentDay = savedInstanceState.getInt("CURRENT_DAY", 0);
            viewPager.setCurrentItem(currentDay, false);
        } catch (Exception e) {
            Log.e("ScheduleActivity", "Error in onRestoreInstanceState", e);
        }
    }

    public void goBackToChangeSchedulePage(View view)
    {

        try {
            prefsManager.clearSelectedGroup();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("ScheduleActivity", "Error going back to main", e);
            finish();
        }

    }

}