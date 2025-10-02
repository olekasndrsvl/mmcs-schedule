package com.alexanderl.mmcs_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




    public void showSheduleButtonClick(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("schedule_type",1);
        intent.putExtra("groupid",53); // id: 53 ФИИТ 3.3 расписание
        startActivity(intent);
    }
}