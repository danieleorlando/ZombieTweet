package com.zombietweet.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zombietweet.android.adapter.ScheduleAdapter;
import com.zombietweet.android.helper.RalewayFontHelper;
import com.zombietweet.android.model.Schedule;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends Activity {

    Button btnLogin;

    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;

    private Context _context;
    private static SharedPreferences mSharedPreferences;
    private static String myTokenJson;
    private static String myToken;
    private List<Schedule> scheduleList = new ArrayList<Schedule>();

    static final int GET_TWITTER_TOKEN = 1;

    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView textView = (TextView)findViewById(R.id.fullscreen_content);
        RalewayFontHelper.applyFont(this,textView);

        btnLogin = (Button)findViewById(R.id.login_button);

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setVisibility(View.GONE);
                ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                loginToTwitter();
            }
        });

        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(Const.TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                btnLogin.setVisibility(View.GONE);
                ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                final String verifier = uri.getQueryParameter(Const.URL_TWITTER_OAUTH_VERIFIER);
                try {
                    Thread thread = new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                // Get the access token
                                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

                                long userID = accessToken.getUserId();
                                User user = twitter.showUser(userID);
                                String username = user.getName();
                                String screenName = user.getScreenName();
                                String timeZone = user.getTimeZone();
                                int utc = user.getUtcOffset();
                                String imageUrl = user.getProfileImageURL();


                                // Shared Preferences
                                SharedPreferences.Editor e = mSharedPreferences.edit();
                                e.putString(Const.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                                e.putString(Const.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                                e.putBoolean(Const.PREF_KEY_TWITTER_LOGIN, true);
                                e.putString(Const.PREF_KEY_TWITTER_USERNAME, username);
                                e.putString(Const.PREF_KEY_TWITTER_SCREENNAME,screenName);
                                e.putLong(Const.PREF_KEY_TWITTER_UID, userID);
                                e.putString(Const.PREF_KEY_TWITTER_ZONE,timeZone);
                                e.putInt(Const.PREF_KEY_TWITTER_UTC,utc);
                                e.putString(Const.PREF_KEY_TWITTER_IMAGE_URL,imageUrl);
                                e.commit();

                                new postLogin().execute();

                            } catch (Exception e) {
                                Log.v("Error login",e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error create", "> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }

    public class postLogin extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            httppost = new HttpPost(Const.DOMAIN_API + "/post_login");
            httppost.setHeader("Content-Type", "application/json");
            mSharedPreferences = getSharedPreferences("MyPref", 0);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("twitter_user", mSharedPreferences.getString(Const.PREF_KEY_TWITTER_SCREENNAME, ""));
                jsonObject.accumulate("twitter_id", String.valueOf(mSharedPreferences.getLong(Const.PREF_KEY_TWITTER_UID, 0)));
                jsonObject.accumulate("time_zone", mSharedPreferences.getString(Const.PREF_KEY_TWITTER_ZONE, ""));
                jsonObject.accumulate("utc_offset", String.valueOf(mSharedPreferences.getInt(Const.PREF_KEY_TWITTER_UTC, 0)));
                jsonObject.accumulate("profile_image_url", mSharedPreferences.getString(Const.PREF_KEY_TWITTER_IMAGE_URL, ""));

                String json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                httppost.setEntity(se);
                HttpResponse response = httpclient.execute(httppost);
                myTokenJson = EntityUtils.toString(response.getEntity());
                JSONObject jObj = new JSONObject(myTokenJson);
                myToken = jObj.getString("response");
                Log.v("token", myToken);
            } catch (Exception e) {
                Log.v("err", e.getMessage());
            }
            return myToken;
        }

        @Override
        protected void onPostExecute(String token) {
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(Const.PREF_KEY_ZOMBIETWEET_TOKEN,token);
            e.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginToTwitter() {
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        requestToken = twitter.getOAuthRequestToken(Const.TWITTER_CALLBACK_URL);
                        LoginActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                        finish();
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            // user already logged into twitter
            Toast.makeText(getApplicationContext(),"Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(Const.PREF_KEY_TWITTER_LOGIN, false);
    }

}
