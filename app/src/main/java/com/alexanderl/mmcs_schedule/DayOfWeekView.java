package com.alexanderl.mmcs_schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderl.mmcs_schedule.dataTransferObjects.DayOfWeek;

public class DayOfWeekView extends Fragment {
    private static final String ARG_DAY_NAME = "day_name";
    private static final String ARG_DAY_INDEX = "day_index";

    private static DayOfWeek day;



    public static DayOfWeekView newInstance(DayOfWeek _day) {
        DayOfWeekView fragment = new DayOfWeekView();
        Bundle args = new Bundle();
        day=_day;
        args.putString(ARG_DAY_NAME, day.getDayname());
        args.putInt(ARG_DAY_INDEX, day.getDayIndex());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.day_of_week, container, false);
        TextView dayTextView = view.findViewById(R.id.dayTag);
        TextView scheduleTextView = view.findViewById(R.id.scheduleTextView);
        dayTextView.setText(day.getDayname());
        RecyclerView recyclerView = view.findViewById(R.id.list_items);
        LinearLayoutManager l =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(l);
        LessonAdapter adapter = new LessonAdapter(getContext(), day.getLessons());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private String getScheduleForDay() {


        return day.toString();
    }
}
