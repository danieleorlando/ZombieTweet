package com.zombietweet.android.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Daniele on 12/05/2014.
 */
public class RalewayFontHelper {

    private static Typeface mRalewayFont = null;

    private static Typeface getRalewayTypeface(Context context) {
        if (mRalewayFont == null)
            mRalewayFont = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        return mRalewayFont;
    }

    public static TextView applyFont(Context context, TextView textView) {
        if (textView != null)
            textView.setTypeface(getRalewayTypeface(context));
        return textView;
    }
}
