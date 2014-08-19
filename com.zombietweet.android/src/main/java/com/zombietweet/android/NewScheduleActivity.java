package com.zombietweet.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


public class NewScheduleActivity extends ActionBarActivity {

    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

    NotificationManager mNotificationManager;

    private EditText editTextHashtag;
    private EditText editTextSubject;
    private Button buttonStartDateTime;
    private Button buttonEndDateTime;

    private static SharedPreferences mSharedPreferences;

    private String hashtag;
    private String subject;
    private String start;
    private String end;

    private static final int STARTTIMERESULT = 1;
    private static final int ENDTIMERESULT = 2;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextHashtag = (EditText)findViewById(R.id.edit_hashtag);
        editTextSubject = (EditText)findViewById(R.id.edit_subject);
        buttonStartDateTime = (Button)findViewById(R.id.button_start_schedule);
        buttonEndDateTime = (Button)findViewById(R.id.button_end_schedule);

        buttonStartDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewScheduleActivity.this,DateTimeActivity.class);
                intent.putExtra("TIME",STARTTIMERESULT);
                startActivityForResult(intent,STARTTIMERESULT);
            }
        });

        buttonEndDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewScheduleActivity.this,DateTimeActivity.class);
                intent.putExtra("TIME",ENDTIMERESULT);
                startActivityForResult(intent,ENDTIMERESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_schedule, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            createSchedules();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==STARTTIMERESULT) {
            // String message =data.getStringExtra("start");
            start = data.getStringExtra("TIME");
            buttonStartDateTime.setText(start);
        }
        if(requestCode==ENDTIMERESULT) {
            // String message =data.getStringExtra("start");
            end = data.getStringExtra("TIME");
            buttonEndDateTime.setText(end);
        }
    }


    public void createSchedules() {
        hashtag = editTextHashtag.getText().toString();
        subject = editTextSubject.getText().toString();

        mSharedPreferences = getSharedPreferences("MyPref", 0);
        String token = mSharedPreferences.getString(Const.PREF_KEY_ZOMBIETWEET_TOKEN, "");

        httppost = new HttpPost(Const.DOMAIN_API+"/create_schedule");
        httppost.setHeader("Content-Type","application/json");
        httppost.addHeader(BasicScheme.authenticate(
                new UsernamePasswordCredentials(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""), token),
                "UTF-8", false));
        mSharedPreferences = getSharedPreferences("MyPref", 0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("end_date", end);
                    jsonObject.accumulate("hashtag", hashtag);
                    jsonObject.accumulate("start_date", start);
                    jsonObject.accumulate("subject", subject);

                    String json = jsonObject.toString();

                    StringEntity se = new StringEntity(json);
                    httppost.setEntity(se);
                    HttpResponse response = httpclient.execute(httppost);

                    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    sendSimpleNotification();

                    finish();

                } catch (Exception e) {
                    Log.v("err",e.getMessage());
                }
            }
        });
        thread.start();
    }

    private void sendSimpleNotification() {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NewScheduleActivity.this);

        notificationBuilder.setContentTitle(getString(R.string.startrecording));
        notificationBuilder.setContentText("#"+hashtag);

        notificationBuilder.setTicker(getString(R.string.startrecording));

        notificationBuilder.setWhen(System.currentTimeMillis());

        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);

        Intent notificationIntent = new Intent(this, NewScheduleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notificationBuilder.setContentIntent(contentIntent);

        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(NOTIFICATION_ID,
                notificationBuilder.build());
    }

}
