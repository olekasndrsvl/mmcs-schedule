package com.alexanderl.mmcs_schedule;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alexanderl.mmcs_schedule.dataTransferObjects.Week;
public class DayPageAdapter extends FragmentStateAdapter {
    private final Week week;

    public DayPageAdapter(@NonNull FragmentActivity fragmentActivity, Week week1) {
        super(fragmentActivity);
        this.week = week1;
    }

    @NonNull
    public DayOfWeekView createFragment(int position) {
        return DayOfWeekView.newInstance(week.getDays()[position]);
    }

    @Override
    public int getItemCount() {
        return week.getDays().length;
    }
}
