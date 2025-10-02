package com.alexanderl.mmcs_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alexanderl.mmcs_schedule.API.primitives.RawGrade;
import com.alexanderl.mmcs_schedule.API.primitives.ScheduleService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private List<String> gradeList = new ArrayList<>();
    private List<RawGrade> rawGrades = new ArrayList<>();
    private ArrayAdapter<String> adapter_courses;
    private ArrayAdapter<String> adapter_groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner_group);
        Spinner spinner1 = findViewById(R.id.spinner2);

        adapter_courses = new ArrayAdapter<>(this, R.layout.spinner_item, gradeList);
        adapter_courses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter_courses);
        spinner1.setAdapter(adapter_courses);

        loadGrades();
    }


    private void loadGrades() {

        Call<RawGrade.List> call = ScheduleService.getGrades();

        call.enqueue(new Callback<RawGrade.List>() {
            @Override
            public void onResponse(Call<RawGrade.List> call, Response<RawGrade.List> response) {
                if (response.isSuccessful()) {
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
            }
        });
    }

    private void processGrades(List<RawGrade> grades) {
        rawGrades.clear();
        rawGrades.addAll(grades);

        gradeList.clear();

        for (RawGrade grade : grades) {
            String displayText = getDisplayText(grade);
            gradeList.add(displayText);
        }
        adapter_courses.notifyDataSetChanged();
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
        gradeList.clear();
        gradeList.add("Не удалось загрузить данные");
        adapter_courses.notifyDataSetChanged();
    }

    public void showSheduleButtonClick(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("schedule_type",1);
        intent.putExtra("groupid",53); // id: 53 ФИИТ 3.3 расписание
        startActivity(intent);
    }
}