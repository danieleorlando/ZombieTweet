package com.zombietweet.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zombietweet.android.adapter.ScheduleAdapter;
import com.zombietweet.android.adapter.ZombieAdapter;
import com.zombietweet.android.model.Schedule;
import com.zombietweet.android.model.Zombie;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ZombieActivity extends Activity {

    private List<Zombie> zombieList = new ArrayList<Zombie>();
    private ListView listView;
    private ProgressBar progressBar;
    private ZombieAdapter adapter;
    private static SharedPreferences mSharedPreferences;

    String id;
    String hashtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zombie);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        id = extras.getString("ID");
        hashtag = "#" + extras.getString("HASHTAG");

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(hashtag);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        listView = (ListView) findViewById(R.id.list_zombie);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        new TaskZombies().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String zombieID = id;
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_media) {
            Intent intent = new Intent(this,MediaActivity.class);
            intent.putExtra("ID",zombieID);
            intent.putExtra("HASHTAG",hashtag);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.zombie, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public class TaskZombies extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String token = mSharedPreferences.getString(Const.PREF_KEY_ZOMBIETWEET_TOKEN, "");

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(Const.DOMAIN_API+"/get_zombies/"+id);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""), token),
                    "UTF-8", false));
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StringBuilder sb = inputStreamToString(httpResponse.getEntity().getContent());
                JsonParser parser = new JsonParser();
                JsonObject rootObejct = parser.parse(sb.toString()).getAsJsonObject();
                JsonElement zombieElement = rootObejct.get("zombies");
                Gson gson = new Gson();
                if (zombieElement.isJsonObject()) {
                    Zombie zombie = gson.fromJson(zombieElement, Zombie.class);
                    zombieList.add(zombie);
                } else if (zombieElement.isJsonArray()) {
                    Type projectListType = new TypeToken<List<Zombie>>() {
                    }.getType();
                    zombieList = gson.fromJson(zombieElement, projectListType);
                }
            } catch (Exception e) {
                Log.v("E", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            progressBar.setVisibility(View.GONE);
            adapter = new ZombieAdapter(ZombieActivity.this, zombieList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            //
        }
        return total;
    }
}
