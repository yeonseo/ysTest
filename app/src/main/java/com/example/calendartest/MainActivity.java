package com.example.calendartest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.example.calendartest.decorators.EventDecorator;
import com.example.calendartest.decorators.OneDayDecorator;
import com.example.calendartest.decorators.SaturdayDecorator;
import com.example.calendartest.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    String time, kcal, menu;
    EditText edtContent;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    MaterialCalendarView materialCalendarView;
    ArrayList<String> result = new ArrayList<String>();
    Map<String, String> eventDay = new HashMap<>();
    ApiSimulator apiSimulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        edtContent = findViewById(R.id.edtContent);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        eventDay.put("2019,03,18", "");
        eventDay.put("2019,10,18", "");
        eventDay.put("2019,10,18", "");
        eventDay.put("2019,11,8", "");
        eventDay.put("2019,11,20", "");

//        String[] result = {"2019,03,18","2019,09,18","2019,10,17","2019,10,18"};
        eventDrow();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "," + Month + "," + Day;
                String content = eventDay.get(shot_Day);


                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();

                Toast.makeText(getApplicationContext(), shot_Day + "\n" + content, Toast.LENGTH_SHORT).show();
                saveEvent(shot_Day);
            }
        });
    }

    private void eventDrow() {
        for (String mapkey : eventDay.keySet()) {
            result.add(mapkey);
        }
        apiSimulator = (ApiSimulator) new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private void saveEvent(String shot_Day) {
        if (edtContent.getText().equals("")) {
            Toast.makeText(getApplicationContext(), "내용을 적어주세요~~~", Toast.LENGTH_SHORT).show();
        } else {
            if (eventDay.get(shot_Day)==null||eventDay.get(shot_Day).equals("")) {
                result.add(shot_Day);
                eventDay.put(shot_Day, edtContent.getText().toString());
            }
            Toast.makeText(getApplicationContext(), shot_Day + "\n" + eventDay.get(shot_Day), Toast.LENGTH_SHORT).show();
        }
        edtContent.setText("");
        eventDrow();
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        ApiSimulator(ArrayList<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.size(); i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays, MainActivity.this));
        }
    }
}






