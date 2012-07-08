package com.awesome.fling.tv;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Animation;

public class Splasher extends AnimatorListenerAdapter implements Animation.AnimationListener
{
    private VideoOverlay videoOverlay;
    private SplashListener splashListener = SplashListener.NO_OP;
    private final AnimatorSet splashDropAnimator;

    public Splasher(VideoOverlay videoOverlay)
    {
        this.videoOverlay = videoOverlay;

        splashDropAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
                videoOverlay.getContext(), R.animator.splash_drop_animator);
        splashDropAnimator.addListener(Splasher.this);
        splashDropAnimator.setTarget(videoOverlay);
    }

    // Animator listeners (new API)
    @Override
    public void onAnimationEnd(Animator animator)
    {
        if (animator != splashDropAnimator)
        {
            videoOverlay.post(new Runnable()
            {
                public void run()
                {
                    videoOverlay.setImageToDraw(R.drawable.splash);
                    splashDropAnimator.start();

//                    Animation dropAnimation = AnimationUtils.loadAnimation(videoOverlay.getContext(), R.anim.splash_drop);
//                    dropAnimation.setAnimationListener(Splasher.this);
//                    videoOverlay.startAnimation(dropAnimation);
                }
            });
        }
        else
        {
            videoOverlay.post(new Runnable()
            {
               public void run()
               {
                   videoOverlay.setVisibility(View.INVISIBLE);
                   splashListener.onSplashFinished(videoOverlay);
               }
            });
        }
    }

    // Animation listeners (old API)

    public void onAnimationStart(Animation animation)
    {
    }

    public void onAnimationEnd(Animation animation)
    {
        videoOverlay.post(new Runnable()
        {
            public void run()
            {
                videoOverlay.setVisibility(View.INVISIBLE);
                splashListener.onSplashFinished(videoOverlay);
            }
        });
    }

    public void onAnimationRepeat(Animation animation)
    {
    }

    public void setSplashListener(SplashListener splashListener)
    {
        this.splashListener = splashListener;
    }
}
