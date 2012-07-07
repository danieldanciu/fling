package com.awesome.fling.tv;

import android.app.Application;
import com.google.android.youtube.api.YouTube;

public class FlingApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        YouTube.initialize(this, getResources().getString(R.string.youtube_api_key));
    }
}
