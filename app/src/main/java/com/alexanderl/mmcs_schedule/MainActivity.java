package com.alexanderl.mmcs_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alexanderl.mmcs_schedule.API.primitives.RawGrade;
import com.alexanderl.mmcs_schedule.API.primitives.RawGroup;
import com.alexanderl.mmcs_schedule.API.primitives.ScheduleService;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private Button showScheduleButton;
    private List<String> gradeList = new ArrayList<>();
    private List<RawGrade> rawGrades = new ArrayList<>();
    private ArrayAdapter<String> adapter_courses;
    private ArrayAdapter<String> adapter_groups;
    private List<String> groupList = new ArrayList<>();
    private List<RawGroup> rawGroups = new ArrayList<>();
    private ProgressBar loadingGradesProgressBar;
    private ProgressBar loadingGroupsProgressBar;
    private int selectedGroupId;
    private String selectedGroupName;
    Dictionary<String,Integer> grades_dict = new Hashtable<String, Integer>();
    Dictionary<String,Integer> group_dict = new Hashtable<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showScheduleButton = findViewById(R.id.open_schedule_button);
        Spinner spinner1 = findViewById(R.id.spinner2);
        Spinner spinner = findViewById(R.id.spinner_group);

        loadingGradesProgressBar = findViewById(R.id.progress_bar_direction);
        loadingGroupsProgressBar= findViewById(R.id.progress_bar_group);

        adapter_courses = new ArrayAdapter<>(this, R.layout.spinner_item, gradeList);
        adapter_courses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_groups = new ArrayAdapter<>(this, R.layout.spinner_item, groupList);
        adapter_groups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        spinner1.setAdapter(adapter_courses);
        spinner.setAdapter(adapter_groups);
        loadingGradesProgressBar.setVisibility(View.VISIBLE);
        loadGrades();
        loadingGroupsProgressBar.setVisibility(View.VISIBLE);


        AdapterView.OnItemSelectedListener itemSelectedListener_course = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                //Toast.makeText(MainActivity.this,"Selected!",Toast.LENGTH_LONG);
                int id_g = grades_dict.get(item);
                loadGroups(id_g);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadGroups(1);
            }
        };
        spinner1.setOnItemSelectedListener(itemSelectedListener_course);


        AdapterView.OnItemSelectedListener itemSelectedListener_group = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                //Toast.makeText(MainActivity.this,"Selected!",Toast.LENGTH_LONG);
                selectedGroupId = group_dict.get(item);
                selectedGroupName= item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener_group);
    }


    private void loadGrades() {

        Call<RawGrade.List> call = ScheduleService.getGrades();

        call.enqueue(new Callback<RawGrade.List>() {
            @Override
            public void onResponse(Call<RawGrade.List> call, Response<RawGrade.List> response) {
                if (response.isSuccessful()) {
                    showScheduleButton.setText("Перейти к расписанию");
                    RawGrade.List grades = response.body();
                    if (grades != null && !grades.isEmpty()) {
                        processGrades(grades);
                    } else {
                        showError("Нет данных для отображения");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RawGrade.List> call, Throwable t) {
                showError("Ошибка сети: " + t.getMessage());
                showScheduleButton.setText("Попробовать еще раз");
            }
        });
    }
    private void loadGroups(int id)
    {
        Call<RawGroup.List> call = ScheduleService.getGroups(id);

        call.enqueue(new Callback<RawGroup.List>() {
            @Override
            public void onResponse(Call<RawGroup.List> call, Response<RawGroup.List> response) {
                if (response.isSuccessful()) {
                    RawGroup.List groups = response.body();
                    if (groups != null && !groups.isEmpty()) {
                        processGroups(groups);
                    } else {
                        showError("Нет данных для отображения");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RawGroup.List> call, Throwable t) {
                showError("Ошибка сети: " + t.getMessage());
                showScheduleButton.setText("Попробовать еще раз");
            }
        });
    }


    private void processGroups(List<RawGroup> groups)
    {
        rawGroups.clear();
        rawGroups.addAll(groups);

        groupList.clear();
        for(RawGroup group : groups)
        {
            String temp = String.format("%s %s.%s",group.getName(), group.getGradeId(), group.getNum());
            groupList.add(temp);
            group_dict.put(temp,group.getId());
        }
        adapter_groups.notifyDataSetChanged();
        loadingGroupsProgressBar.setVisibility(View.INVISIBLE);
    }
    private void processGrades(List<RawGrade> grades) {
        rawGrades.clear();
        rawGrades.addAll(grades);

        gradeList.clear();

        for (RawGrade grade : grades) {
            String displayText = getDisplayText(grade);
            int grade_id = grade.getId();
            grades_dict.put(displayText,grade_id);
            gradeList.add(displayText);
        }
        adapter_courses.notifyDataSetChanged();
        loadingGradesProgressBar.setVisibility(View.INVISIBLE);
    }

    private String getDisplayText(RawGrade grade) {
        switch (grade.getDegree()) {
            case BACHELOR:
                return "Бакалавриат " + grade.getNum() + " курс";
            case MASTER:
                return "Магистратура " + grade.getNum() + " курс";
            case SPECIALIST:
                return "Специалитет " + grade.getNum() + " курс";
            case POSTGRADUATE:
                return "Аспирантура " + grade.getNum() + " курс";
            default:
                return grade.getDegree().toString() + " " + grade.getNum() + " курс";
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Добавляем заглушку в Spinner
//        gradeList.clear();
//        gradeList.add("Не удалось загрузить данные");
//        adapter_courses.notifyDataSetChanged();
    }

    public void showSheduleButtonClick(View view) {
        if(!(loadingGradesProgressBar.getVisibility() == View.VISIBLE) && !(loadingGroupsProgressBar.getVisibility() == View.VISIBLE)) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("schedule_type", 1);


            intent.putExtra("groupid", selectedGroupId); // id: 53 ФИИТ 3.3 расписание
            intent.putExtra("groupname", selectedGroupName);
            startActivity(intent);
        }
        else
        {
            loadGrades();
            showScheduleButton.setText("Загрузка...");
        }
    }
}