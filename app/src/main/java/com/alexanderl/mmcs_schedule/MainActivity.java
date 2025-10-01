package com.alexanderl.mmcs_schedule;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alexanderl.mmcs_schedule.dataTransferObjects.TestWeekBuilder;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Week;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


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
        setupViewPager();

    }
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }


    private void setupViewPager() {

        Week w = TestWeekBuilder.createTestWeek();
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