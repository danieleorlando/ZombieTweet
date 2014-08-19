package com.zombietweet.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zombietweet.android.adapter.ScheduleAdapter;
import com.zombietweet.android.model.Schedule;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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
 * Created by Daniele on 10/05/2014.
 */
public class SchedulesFragment extends Fragment {

    private List<Schedule> scheduleList = new ArrayList<Schedule>();
    private ListView listView;
    private ProgressBar progressBar;
    private ScheduleAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static SharedPreferences mSharedPreferences;

    public static SchedulesFragment newInstance(String string) {
        SchedulesFragment myFragment = new SchedulesFragment();

        Bundle args = new Bundle();
        args.putString("string", string);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSharedPreferences = getActivity().getBaseContext().getSharedPreferences("MyPref", 0);
        new TaskSchedules().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_schedules, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_schedule);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new TaskSchedules().execute();
            }
        });
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_green_dark, R.color.zombietweet_color, R.color.black_overlay, android.R.color.holo_green_light);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = scheduleList.get(i).get_id();
                String hashtag = scheduleList.get(i).getHashtag();
                Intent intent = new Intent(getActivity(),ZombieActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("HASHTAG",hashtag);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String id = scheduleList.get(i).get_id();

                String[] opt = {
                    getString(R.string.delete),
                    getString(R.string.share),
                    getString(R.string.favourite),
                    getString(R.string.love)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(opt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // delete
                        if (which == 0) {
                            new TaskDeleteSchedules().execute(id);
                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(getActivity(), NewScheduleActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public class TaskSchedules extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String token = mSharedPreferences.getString(Const.PREF_KEY_ZOMBIETWEET_TOKEN, "");
            Log.v("token schedules",token);
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(Const.DOMAIN_API+"/get_schedules");
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""), token),
                    "UTF-8", false));
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StringBuilder sb = inputStreamToString(httpResponse.getEntity().getContent());
                //Log.v("sb",sb.toString());
                JsonParser parser = new JsonParser();
                JsonObject rootObejct = parser.parse(sb.toString()).getAsJsonObject();
                JsonElement scheduleElement = rootObejct.get("schedules");
                Gson gson = new Gson();
                if (scheduleElement.isJsonObject()) {
                    Schedule schedule = gson.fromJson(scheduleElement, Schedule.class);
                    scheduleList.add(schedule);
                } else if (scheduleElement.isJsonArray()) {
                    Type projectListType = new TypeToken<List<Schedule>>() {
                    }.getType();
                    scheduleList = gson.fromJson(scheduleElement, projectListType);
                }
            } catch (Exception e) {
                Log.v("E", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            progressBar.setVisibility(View.GONE);
            adapter = new ScheduleAdapter(getActivity(),scheduleList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);

        }
    }

    public class TaskDeleteSchedules extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            Log.v("delete", params[0]);
            String token = mSharedPreferences.getString(Const.PREF_KEY_ZOMBIETWEET_TOKEN, "");

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpDelete httpDelete = new HttpDelete(Const.DOMAIN_API+"/delete_schedule/"+params[0]);

            httpDelete.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""), token),
                    "UTF-8", false));
            try {
                HttpResponse httpResponse = httpClient.execute(httpDelete);
            } catch (Exception e) {
                Log.v("E", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            //progressBar.setVisibility(View.GONE);
            //adapter = new ScheduleAdapter(getActivity(),scheduleList);
            //listView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            new TaskSchedules().execute();
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
