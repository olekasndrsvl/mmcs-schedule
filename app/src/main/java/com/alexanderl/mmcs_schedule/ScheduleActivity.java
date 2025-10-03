package com.alexanderl.mmcs_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
    private TextView todayTextView;
    private DayPageAdapter adapter;
    private  RawScheduleOfGroup response_week;
    private WeekType weekType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initViews();
        int type_of_schedule=1;
        int group_id=1;
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
             type_of_schedule = arguments.getInt("schedule_type", 1);
             group_id = arguments.getInt("groupid", 1);
        }


        getCurrentWeekType();
        loadScheduleFromAPI(group_id);


        changeWeekButton.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(ScheduleActivity.this, changeWeekButton);


            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(menuItem -> {

                String temp =menuItem.getTitle().toString();
                Week week;
                switch (temp)
                {
                    case "Текущая неделя":
                        week = ScheduleAdapter.convertToWeek(response_week,weekType);
                        setupViewPager(week);
                        break;
                    case "Верхняя неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.UPPER);
                        setupViewPager(week);
                        break;
                    case "Нижняя неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.LOWER);
                        setupViewPager(week);
                        break;
                    case "Полная неделя":
                        week = ScheduleAdapter.convertToWeek(response_week, WeekType.COMBINBED);
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

                }
            }

            @Override
            public void onFailure(Call<RawWeek> call, Throwable t) {
                //hideLoading();
                Toast.makeText(ScheduleActivity.this, "Не удалось получить тип недели!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadScheduleFromAPI(int groupid) {
        // Показываем индикатор загрузки
        //showLoading();


        ScheduleService.getGroupSchedule(groupid).enqueue(new Callback<RawScheduleOfGroup>() {
            @Override
            public void onResponse(Call<RawScheduleOfGroup> call, Response<RawScheduleOfGroup> response) {
                //hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Конвертируем raw данные в нашу модель
                    response_week = response.body();


                    Week week1 = ScheduleAdapter.convertToWeek(response_week,weekType);
                    setupViewPager(week1);

                } else {
                    // Если API не доступно, используем тестовые данные
                    useTestData();
                }
            }

            @Override
            public void onFailure(Call<RawScheduleOfGroup> call, Throwable t) {
                //hideLoading();
                // При ошибке используем тестовые данные
                useTestData();
                Toast.makeText(ScheduleActivity.this, "Используются тестовые данные", Toast.LENGTH_SHORT).show();
            }
        });
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

        // Настраиваем TabLayout для отображения индикаторов
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(getDayOfWeekByNumber(position + 1));
                    }
                }).attach();


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
            case 7:
                return "ВС";
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
        super.onRestoreInstanceState(savedInstanceState);
        int currentDay = savedInstanceState.getInt("CURRENT_DAY", 0);
        viewPager.setCurrentItem(currentDay, false);
    }

    public void goBackToChangeSchedulePage(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void changeWeekTypeButtonClicked(View view)
    {

    }
}