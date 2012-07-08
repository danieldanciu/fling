package com.awesome.fling.tv;

public interface SplashListener
{
    void onSplashFinished();

    SplashListener NO_OP = new SplashListener() {

        public void onSplashFinished()
        {
        }
    };
}
