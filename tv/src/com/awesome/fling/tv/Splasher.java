package com.awesome.fling.tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class Splasher extends AnimatorListenerAdapter
{
    private VideoOverlay videoOverlay;

    public Splasher(VideoOverlay videoOverlay)
    {
        this.videoOverlay = videoOverlay;
    }

    @Override
    public void onAnimationEnd(Animator animator)
    {
        videoOverlay.setImageResource(R.drawable.splash);
    }

}
