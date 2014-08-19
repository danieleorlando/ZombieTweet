package com.zombietweet.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zombietweet.android.R;
import com.zombietweet.android.model.Schedule;

import java.util.List;

/**
 * Created by Daniele on 13/05/2014.
 */
public class ScheduleAdapter extends BaseAdapter {

    private SharedPreferences prefs;
    private Context context;
    private List<Schedule> schedules;
    private LayoutInflater mLayoutInflater = null;

    private TextView hashtag;
    private TextView subject;
    private TextView date;

    public ScheduleAdapter(Context context, List<Schedule> schedules) {
        this.context = context;
        this.schedules = schedules;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_schedule, null);
        }
        hashtag = (TextView) view.findViewById(R.id.item_hashtag);
        subject = (TextView) view.findViewById(R.id.item_subject);
        date = (TextView) view.findViewById(R.id.item_date);
        hashtag.setText("#"+ schedules.get(i).getHashtag());
        subject.setText(schedules.get(i).getSubject());
        date.setText(schedules.get(i).getStart_date());

        return view;
    }
}
