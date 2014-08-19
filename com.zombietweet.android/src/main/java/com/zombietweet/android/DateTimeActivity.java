package com.zombietweet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeActivity extends ActionBarActivity {

    DatePicker datePicker;
    TimePicker timePicker;

    private int time;
    private String selectedtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        time = extras.getInt("TIME");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_save) {
            //"2014-05-29 19:43:06";
            int day  = datePicker.getDayOfMonth();
            int month= datePicker.getMonth()+1;
            int year = datePicker.getYear();
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            String date = year+"-"+month+"-"+day+" "+hour+":"+minute;
            Log.v("date",date);
            DateFormat inputFormat = new SimpleDateFormat("yyyy-M-d H:m");
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsed = new Date();
            try {
                parsed = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            selectedtime = outputFormat.format(parsed);
            Log.v("selectedtime", selectedtime);
            Intent intent=new Intent();
            intent.putExtra("TIME",selectedtime);
            setResult(time,intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
