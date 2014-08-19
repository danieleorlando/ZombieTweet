package com.zombietweet.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Daniele on 10/05/2014.
 */
public class ProfileFragment extends Fragment {

    private static SharedPreferences mSharedPreferences;

    ImageView imageViewBackground;
    ImageView imageViewProfile;
    TextView textViewScreenName;
    TextView textViewUserName;

    public static ProfileFragment newInstance(String string) {
        ProfileFragment myFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString("string", string);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences("MyPref", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        Button buttonLogout = (Button)rootView.findViewById(R.id.logout_button);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString(Const.PREF_KEY_OAUTH_TOKEN, "");
                e.putString(Const.PREF_KEY_OAUTH_SECRET, "");
                e.putBoolean(Const.PREF_KEY_TWITTER_LOGIN, false);
                e.putString(Const.PREF_KEY_TWITTER_USERNAME, "");
                e.putString(Const.PREF_KEY_TWITTER_SCREENNAME, "");
                e.putLong(Const.PREF_KEY_TWITTER_UID, -1);
                e.putString(Const.PREF_KEY_TWITTER_ZONE, "");
                e.putInt(Const.PREF_KEY_TWITTER_UTC,-1);
                e.putString(Const.PREF_KEY_TWITTER_IMAGE_URL,"");
                e.commit();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        /*mSharedPreferences = getActivity().getSharedPreferences("MyPref", 0);
        imageViewBackground = (ImageView)rootView.findViewById(R.id.user_background);
        imageViewProfile = (ImageView)rootView.findViewById(R.id.user_profile);
        textViewScreenName = (TextView)rootView.findViewById(R.id.user_screenname);
        textViewUserName = (TextView)rootView.findViewById(R.id.user_username);
        textViewScreenName.setText(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME,""));
        textViewUserName.setText(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_USERNAME,""));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
                    builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
                    String access_token = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, "");
                    String access_token_secret = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_SECRET, "");
                    AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                    Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                    User user = twitter.showUser(mSharedPreferences.getLong(Const.PREF_KEY_TWITTER_UID, 0));
                    Picasso.with(getActivity()).load(user.getProfileBackgroundImageURL()).into(imageViewBackground);
                    Picasso.with(getActivity()).load(mSharedPreferences.getString(Const.PREF_KEY_TWITTER_IMAGE_URL,"")).into(imageViewProfile);

                } catch (Exception e) {
                    Log.v("err", e.getMessage());
                }
            }
        });
        thread.start();
        */

        return rootView;
    }
}