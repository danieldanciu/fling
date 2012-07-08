package com.awesome.fling.tv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class VideoOverlay extends ImageView
{
    private AnimatorSet objectAnimatorSet = new AnimatorSet();
    private Splasher splasher;

    public VideoOverlay(Context context)
    {
        super(context);
        init();
    }

    public VideoOverlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public VideoOverlay(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        setImageResource(R.drawable.tomato);
        setScaleType(ScaleType.CENTER);
        objectAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.tomato_splash);
        splasher = new Splasher(this);
        objectAnimatorSet.addListener(splasher);
        objectAnimatorSet.setTarget(this);
    }

    public void onTomatoThrown()
    {
        setVisibility(View.VISIBLE);
        objectAnimatorSet.start();
    }

    public void setSplashListener(SplashListener splashListener)
    {
        splasher.setSplashListener(splashListener);
    }
}
