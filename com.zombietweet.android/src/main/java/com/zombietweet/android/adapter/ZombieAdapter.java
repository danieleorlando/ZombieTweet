package com.zombietweet.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zombietweet.android.Const;
import com.zombietweet.android.R;
import com.zombietweet.android.model.Zombie;
import com.zombietweet.android.util.CircleTransform;

import org.w3c.dom.Text;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Daniele on 13/05/2014.
 */
public class ZombieAdapter extends BaseAdapter {

    private SharedPreferences prefs;
    private Context context;
    private List<Zombie> zombies;
    private LayoutInflater mLayoutInflater = null;

    //private TextView author;
    //private TextView created_at;
    //private TextView text;

    public ZombieAdapter(Context context, List<Zombie> zombies) {
        this.context = context;
        this.zombies = zombies;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return zombies.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_zombie, viewGroup, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.item_image);
            holder.author = (TextView) view.findViewById(R.id.item_author);
            holder.created_at = (TextView) view.findViewById(R.id.item_created);
            holder.text = (TextView) view.findViewById(R.id.item_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.author.setText(zombies.get(i).getAuthor());
        holder.created_at.setText(zombies.get(i).getCreated_at());
        holder.text.setText(zombies.get(i).getText());
        Picasso.with(context)
                .load(zombies.get(i).getAvatar())
                .transform(new CircleTransform())
                .resizeDimen(R.dimen.image_avater_zombie, R.dimen.image_avater_zombie)
                .error(R.drawable.ic_launcher)
                .centerInside()
                .into(holder.image);
        return view;
    }

    static class ViewHolder {
        ImageView image;
        TextView author;
        TextView created_at;
        TextView text;

    }
}
