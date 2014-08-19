package com.zombietweet.android;

/**
 * Created by Daniele on 10/05/2014.
 */
public class Const {

    //static String TWITTER_CONSUMER_KEY = "BYkChDL0JUHim7xcKP9vir0bI";
    static String TWITTER_CONSUMER_KEY = "NwgjglIA7c4USLyJhFD60ZR4I";
    //static String TWITTER_CONSUMER_SECRET = "aKED9sP5oY2P4CW1KmfpZ2ZljjGmZ72ViO1NKOUc80RIULb2zr";
    static String TWITTER_CONSUMER_SECRET = "siqsll32lCnB7Zm2JCjOqzDDvFNW5H114TKALFB2TOPg4sKiDQ";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    static final String PREF_KEY_TWITTER_USERNAME = "twitterUsername";
    static final String PREF_KEY_TWITTER_SCREENNAME = "twitterScreenName";
    static final String PREF_KEY_TWITTER_UID = "twitterUID";
    static final String PREF_KEY_TWITTER_ZONE = "twitterZone";
    static final String PREF_KEY_TWITTER_UTC = "twitterUTC";
    static final String PREF_KEY_TWITTER_IMAGE_URL = "twitterImageUrl";

    static final String TWITTER_CALLBACK_URL = "oauth://zombietweet";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    static final String PREF_KEY_ZOMBIETWEET_TOKEN = "zombieToken";

    // api
    static final String DOMAIN_API = "http://alfa.zombietweet.com/api";
    //static final String DOMAIN_API = "http://192.168.0.10:9090/api"; // LOCAL
}
