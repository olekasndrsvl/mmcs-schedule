package com.alexanderl.mmcs_schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderl.mmcs_schedule.API.primitives.RawGrade;
import com.alexanderl.mmcs_schedule.API.primitives.RawGroup;
import com.alexanderl.mmcs_schedule.API.primitives.ScheduleService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


@RunWith(AndroidJUnit4.class)
public class APITests {


    @Test
    public void testForGrades() {

        CountDownLatch latch = new CountDownLatch(1);
        final RawGrade.List[] resultGrades = new RawGrade.List[1];
        final boolean[] success = {false};

        Call<RawGrade.List> call = ScheduleService.getGrades();

        call.enqueue(new Callback<RawGrade.List>() {
            @Override
            public void onResponse(Call<RawGrade.List> call, Response<RawGrade.List> response) {
                if (response.isSuccessful()) {
                    resultGrades[0] = response.body();
                    success[0] = true;

                } else {
                    success[0] = false;
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<RawGrade.List> call, Throwable t) {
                success[0] = false;

                latch.countDown();
            }
        });

        try {
            Log.i( "API", "Waiting response....");
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert
        assertTrue("Request failed", success[0]);
        assertNotNull("Grades should not be null", resultGrades[0]);
        assertEquals("Should have 9 grades", 9, resultGrades[0].size());
        Log.i("API", "Test completed successfully");
    }


    @Test
    public void testForGroups()
    {

        CountDownLatch latch = new CountDownLatch(1);
        final RawGroup.List[] resultGroups = new RawGroup.List[1];
        final boolean[] success = {false};

        Call<RawGroup.List> call = ScheduleService.getGroups(1);

        call.enqueue(new Callback<RawGroup.List>() {
            @Override
            public void onResponse(Call<RawGroup.List> call, Response<RawGroup.List> response) {
                if (response.isSuccessful()) {
                    resultGroups[0] = response.body();
                    success[0] = true;
                } else {
                    success[0] = false;
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<RawGroup.List> call, Throwable t) {
                success[0] = false;
                Log.e("API", "Request failed: " + t.getMessage());
                latch.countDown();
            }
        });

        try {
            Log.i( "API", "Waiting response....");
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }




        // Assert
        assertTrue("Request failed", success[0]);
        assertNotNull("Groups should not be null", resultGroups[0]);
        assertEquals("Should have 14 groups", 14, resultGroups[0].size());
        Log.i("API", "Test completed successfully");
    }

}