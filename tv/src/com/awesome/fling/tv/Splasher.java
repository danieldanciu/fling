package com.awesome.fling.tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Splasher extends AnimatorListenerAdapter implements Animation.AnimationListener
{
    private VideoOverlay videoOverlay;
    private SplashListener splashListener = SplashListener.NO_OP;

    public Splasher(VideoOverlay videoOverlay)
    {
        this.videoOverlay = videoOverlay;
    }

    // Animator listeners (new API)
    @Override
    public void onAnimationEnd(Animator animator)
    {

        videoOverlay.post(new Runnable()
        {
            @Override
            public void run()
            {
                videoOverlay.setImageResource(R.drawable.splash);
                Animation dropAnimation = AnimationUtils.loadAnimation(videoOverlay.getContext(), R.anim.splash_drop);
                dropAnimation.setAnimationListener(Splasher.this);
                videoOverlay.startAnimation(dropAnimation);
            }
        });
    }

    // Animation listeners (old API)

    @Override
    public void onAnimationStart(Animation animation)
    {
    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        videoOverlay.post(new Runnable()
        {
            @Override
            public void run()
            {
                videoOverlay.setVisibility(View.INVISIBLE);
                splashListener.onSplashFinished(videoOverlay);
            }
        });
    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {
    }

    public void setSplashListener(SplashListener splashListener)
    {
        this.splashListener = splashListener;
    }
}
