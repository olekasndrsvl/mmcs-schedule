package com.alexanderl.mmcs_schedule;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alexanderl.mmcs_schedule.API.primitives.RawScheduleOfGroup;
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


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView todayTextView;
    private DayPageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadScheduleFromAPI();
       // setupViewPager();

    }
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }
    private void loadScheduleFromAPI() {
        // Показываем индикатор загрузки
        //showLoading();

        ScheduleService.getGroupSchedule(53).enqueue(new Callback<RawScheduleOfGroup>() {
            @Override
            public void onResponse(Call<RawScheduleOfGroup> call, Response<RawScheduleOfGroup> response) {
                //hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Конвертируем raw данные в нашу модель
                    Week week = ScheduleAdapter.convertToWeek(response.body(), WeekType.COMBINBED);
                    setupViewPager(week);
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
                Toast.makeText(MainActivity.this, "Используются тестовые данные", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void useTestData() {
        Week testWeek = TestWeekBuilder.createTestWeek();
        setupViewPager(testWeek);
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

}