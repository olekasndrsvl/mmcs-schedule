package com.alexanderl.mmcs_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alexanderl.mmcs_schedule.API.primitives.RawGrade;
import com.alexanderl.mmcs_schedule.API.primitives.RawGroup;
import com.alexanderl.mmcs_schedule.API.primitives.RawTeacher;
import com.alexanderl.mmcs_schedule.API.primitives.ScheduleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_GROUP_ID = "selected_group_id";
    private static final String KEY_GROUP_NAME = "selected_group_name";
    private String currentScheduleMode;
    private Button showScheduleButton;
    private List<String> gradeList = new ArrayList<>();
    private List<RawGrade> rawGrades = new ArrayList<>();
    private List<String> modesSchedule = new ArrayList<>();
    private ArrayAdapter<String> adapter_courses;
    private ArrayAdapter<String> adapter_groups;
    private ArrayAdapter<String> adapter_modes;
    private List<String> groupList = new ArrayList<>();
    private List<RawGroup> rawGroups = new ArrayList<>();
    private ProgressBar loadingGradesProgressBar;
    private ProgressBar loadingGroupsProgressBar;
    private int selectedGroupId;
    private String selectedGroupName;
    private PreferencesManager prefsManager;

    // Преподавательское
    private LinearLayout layoutGroupContainer;
    private LinearLayout layoutTeacherContainer;
    private List<String> teacherList = new ArrayList<>();
    private List<RawTeacher> rawTeachers = new ArrayList<>();
    private Map<String, Integer> teacherDict = new HashMap<>();
    private ArrayAdapter<String> adapter_teachers;
    private Spinner spinnerTeacher;
    private int selectedTeacherId;
    private String selectedTeacherName;
    private ProgressBar loadingTeachersProgressBar;

    Map<String,Integer> grades_dict = new Hashtable<String, Integer>();
    Map<String,Integer> group_dict = new Hashtable<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = new PreferencesManager(this);

        // Проверяем, была ли выбрана группа ранее
        if (prefsManager.isGroupSelected()) {
            goToScheduleActivityForGroup();
            return;
        }

        // Проверяем, был ли выбран преподаватель ранее
        if (prefsManager.isTeacherSelected()) {
            goToScheduleActivityForTeacher();
            return;
        }

        setContentView(R.layout.activity_main);

        Spinner spinner_mode = findViewById(R.id.spinner_mode);
        modesSchedule= new ArrayList<String>(){{
            add("Группа");
            add("Преподаватель");
            add("Аудитория");
        }};
        currentScheduleMode="Группа";

        adapter_modes = new ArrayAdapter<>(this, R.layout.spinner_item_top, modesSchedule);
        adapter_modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        showScheduleButton = findViewById(R.id.open_schedule_button);
        Spinner spinner1 = findViewById(R.id.spinner2);
        Spinner spinner = findViewById(R.id.spinner_group);
        Spinner spinner_teacher = findViewById(R.id.spinner_teacher);

        loadingGradesProgressBar = findViewById(R.id.progress_bar_direction);
        loadingGroupsProgressBar = findViewById(R.id.progress_bar_group);
        loadingTeachersProgressBar = findViewById(R.id.progress_bar_teacher);

        // Инициализация layout контейнеров
        layoutGroupContainer = findViewById(R.id.layout_group_container);
        layoutTeacherContainer = findViewById(R.id.layout_teacher_container);

        adapter_courses = new ArrayAdapter<>(this, R.layout.spinner_item, gradeList);
        adapter_courses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_groups = new ArrayAdapter<>(this, R.layout.spinner_item, groupList);
        adapter_groups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_teachers = new ArrayAdapter<>(this, R.layout.spinner_item, teacherList);
        adapter_teachers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter_courses);
        spinner.setAdapter(adapter_groups);
        spinner_teacher.setAdapter(adapter_teachers);
        spinner_mode.setAdapter(adapter_modes);

        loadingGradesProgressBar.setVisibility(View.VISIBLE);
        loadGrades();

        // Слушатель для режима расписания
        AdapterView.OnItemSelectedListener itemSelectedListener_mode = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                Log.i("USER_CHOICE_MODE", item);
                currentScheduleMode = item;
                updateUIForScheduleMode(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateUIForScheduleMode("Группа");
            }
        };
        spinner_mode.setOnItemSelectedListener(itemSelectedListener_mode);

        // Слушатель для курсов
        AdapterView.OnItemSelectedListener itemSelectedListener_course = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                Log.i("USER_CHOICE_COURSE", item);
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
                String item = (String)parent.getItemAtPosition(position);
                Log.i("USER_CHOICE_GROUP", item);
                selectedGroupId = group_dict.get(item);
                selectedGroupName = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener_group);


        AdapterView.OnItemSelectedListener itemSelectedListener_teacher = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                Log.i("USER_CHOICE_TEACHER", item);
                selectedTeacherId = teacherDict.get(item);
                selectedTeacherName = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner_teacher.setOnItemSelectedListener(itemSelectedListener_teacher);
    }

    private void goToScheduleActivityForGroup() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("groupid", prefsManager.getSelectedGroupId());
        intent.putExtra("groupname", prefsManager.getSelectedGroupName());
        intent.putExtra("schedule_type", 1); // или ваш тип расписания

        // Очищаем back stack чтобы нельзя было вернуться назад
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToScheduleActivityForTeacher(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("teacherId", prefsManager.getSelectedTeacherId());
        intent.putExtra("teachername", prefsManager.getSelectedTeacherName());
        intent.putExtra("schedule_type", 2);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateUIForScheduleMode(String mode){
        switch (mode){
            case "Группа":
                layoutGroupContainer.setVisibility(View.VISIBLE);
                layoutTeacherContainer.setVisibility(View.GONE);
                break;

            case "Преподаватель":
                layoutGroupContainer.setVisibility(View.GONE);
                layoutTeacherContainer.setVisibility(View.VISIBLE);
                if (teacherList.isEmpty())
                    loadTeachers();
                break;

            case "Аудитория":
                layoutGroupContainer.setVisibility(View.GONE);
                layoutTeacherContainer.setVisibility(View.GONE);
                Toast.makeText(this, "Режим аудитории пока не реализован", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void loadGrades() {

        Call<RawGrade.List> call = ScheduleService.getGrades();
        Log.i( "API", "Waiting response....");
        call.enqueue(new Callback<RawGrade.List>() {
            @Override
            public void onResponse(Call<RawGrade.List> call, Response<RawGrade.List> response) {
                if (response.isSuccessful()) {
                    showScheduleButton.setText("Перейти к расписанию");
                    RawGrade.List grades = response.body();
                    if (grades != null && !grades.isEmpty()) {
                        processGrades(grades);
                        Log.i("API", "Grades loaded successfully");
                    } else {
                        showError("Нет данных для отображения");
                        Log.e("API", "No grades found");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                    Log.e("API", "Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RawGrade.List> call, Throwable t) {
                showError("Ошибка сети: " + t.getMessage());
                showScheduleButton.setText("Попробовать еще раз");
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }

    private void loadGroups(int id)
    {
        Call<RawGroup.List> call = ScheduleService.getGroups(id);
        Log.i( "API", "Waiting response....");
        call.enqueue(new Callback<RawGroup.List>() {
            @Override
            public void onResponse(Call<RawGroup.List> call, Response<RawGroup.List> response) {
                if (response.isSuccessful()) {
                    RawGroup.List groups = response.body();
                    if (groups != null && !groups.isEmpty()) {
                        processGroups(groups);
                        Log.i("API", "Groups loaded successfully");
                    } else {
                        showError("Нет данных для отображения");
                        Log.e("API", "No groups found");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                    Log.e("API", "Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RawGroup.List> call, Throwable t) {
                showError("Ошибка сети: " + t.getMessage());
                showScheduleButton.setText("Попробовать еще раз");
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }

    private void loadTeachers() {
        loadingTeachersProgressBar.setVisibility(View.VISIBLE);
        Call<RawTeacher.List> call = ScheduleService.getTeachers();
        Log.i("API", "Waiting response for teachers....");
        call.enqueue(new Callback<RawTeacher.List>() {
            @Override
            public void onResponse(Call<RawTeacher.List> call, Response<RawTeacher.List> response) {
                loadingTeachersProgressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    RawTeacher.List teachers = response.body();
                    if (teachers != null && !teachers.isEmpty()) {
                        processTeachers(teachers);
                        Log.i("API", "Teachers loaded successfully");
                    } else {
                        showError("Нет данных о преподавателях");
                        Log.e("API", "No teachers found");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                    Log.e("API", "Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RawTeacher.List> call, Throwable t) {
                loadingTeachersProgressBar.setVisibility(View.INVISIBLE);
                showError("Ошибка сети: " + t.getMessage());
                Log.e("API", "Teachers load error: " + t.getMessage());
            }
        });
    }


    private void processGroups(List<RawGroup> groups)
    {
        rawGroups.clear();
        rawGroups.addAll(groups);

        groupList.clear();
        selectedGroupId = groups.get(0).getId();
        selectedGroupName= String.format("%s %s.%s",groups.get(0).getName(), groups.get(0).getGradeId(), groups.get(0).getNum());

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

    private void processTeachers(List<RawTeacher> teachers) {
        rawTeachers.clear();
        rawTeachers.addAll(teachers);

        teacherList.clear();
        teacherDict.clear();

        for (RawTeacher teacher : teachers) {
            String displayName = formatTeacherName(teacher);
            teacherList.add(displayName);
            teacherDict.put(displayName, teacher.getId());
        }

        adapter_teachers.notifyDataSetChanged();

        // Автоматически выбираем первого преподавателя
        if (!teacherList.isEmpty()) {
            selectedTeacherId = rawTeachers.get(0).getId();
            selectedTeacherName = teacherList.get(0);
        }
    }

    private String formatTeacherName(RawTeacher teacher) {
        return teacher.getDegree() + " " + teacher.getName();
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

    }

    public void showSheduleButtonClick(View view) {
        if(Objects.equals(currentScheduleMode, "Группа")) {
            if (!(loadingGradesProgressBar.getVisibility() == View.VISIBLE) && !(loadingGroupsProgressBar.getVisibility() == View.VISIBLE)) {
                Intent intent = new Intent(this, ScheduleActivity.class);
                intent.putExtra("schedule_type", 1);


                intent.putExtra("groupid", selectedGroupId); // id: 53 ФИИТ 3.3 расписание
                intent.putExtra("groupname", selectedGroupName);
                startActivity(intent);
            } else {
                loadGrades();
                showScheduleButton.setText("Загрузка...");
            }
        }
        else if (Objects.equals(currentScheduleMode, "Преподаватель"))
        {
            Toast.makeText(this,"Not implemented!", Toast.LENGTH_LONG);
        }
        else if(Objects.equals(currentScheduleMode, "Аудитория"))
        {
            Toast.makeText(this,"Not implemented!", Toast.LENGTH_LONG);
        }
    }
}