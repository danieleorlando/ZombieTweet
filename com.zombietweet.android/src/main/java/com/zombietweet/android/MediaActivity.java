package com.zombietweet.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zombietweet.android.adapter.MediaAdapter;
import com.zombietweet.android.model.Media;

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

/**
 * Created by Daniele on 30/07/2014.
 */
public class MediaActivity extends Activity {

    private List<Media> mediaList = new ArrayList<Media>();
    //private GridView gridView;
    private StaggeredGridView gridView;
    private ProgressBar progressBar;
    private MediaAdapter adapter;
    private static SharedPreferences mSharedPreferences;

    String id;
    String hashtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        id = extras.getString("ID");
        hashtag = extras.getString("HASHTAG");

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(hashtag);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        gridView = (StaggeredGridView) findViewById(R.id.grid_media);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        new TaskMedia().execute();

    }

    public class TaskMedia extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String token = mSharedPreferences.getString(Const.PREF_KEY_ZOMBIETWEET_TOKEN, "");

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(Const.DOMAIN_API+"/get_media/"+id);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""), token),
                    "UTF-8", false));
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StringBuilder sb = inputStreamToString(httpResponse.getEntity().getContent());
                Log.v("sb", sb.toString());
                JsonParser parser = new JsonParser();
                JsonObject rootObejct = parser.parse(sb.toString()).getAsJsonObject();
                JsonElement mediaElement = rootObejct.get("results");
                Gson gson = new Gson();
                if (mediaElement.isJsonObject()) {
                    Media media = gson.fromJson(mediaElement, Media.class);
                    mediaList.add(media);
                } else if (mediaElement.isJsonArray()) {
                    Type projectListType = new TypeToken<List<Media>>() {
                    }.getType();
                    mediaList = gson.fromJson(mediaElement, projectListType);
                }
            } catch (Exception e) {
                Log.v("E", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            progressBar.setVisibility(View.GONE);
            adapter = new MediaAdapter(MediaActivity.this, mediaList);
            gridView.setAdapter(adapter);
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

