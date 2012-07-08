package com.awesome.fling.tv;

import android.view.View;

public interface SplashListener
{
    void onSplashFinished(View viewDisplayingTheSplash);

    SplashListener NO_OP = new SplashListener() {
        public void onSplashFinished(View viewDisplayingTheSplash)
        {
        }
    };
}
