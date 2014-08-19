package com.zombietweet.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zombietweet.android.R;
import com.zombietweet.android.model.Media;

import java.util.List;

/**
 * Created by Daniele on 31/07/2014.
 */
public class MediaAdapter extends BaseAdapter {

    private SharedPreferences prefs;
    private Context context;
    private List<Media> media;
    private LayoutInflater mLayoutInflater = null;

    public MediaAdapter(Context context, List<Media> media) {
        this.context = context;
        this.media = media;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return media.size();
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
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_media, viewGroup, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.item_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context)
                .load(media.get(i).getImage())
                //.transform(new CircleTransform())
                //.resizeDimen(R.dimen.image_avater_zombie,R.dimen.image_avater_zombie)
                .error(R.drawable.ic_launcher)
                //.centerInside()
                .into(holder.image);
        return view;
    }

    static class ViewHolder {
        ImageView image;
    }
}
