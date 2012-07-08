package com.awesome.fling.tv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

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
        setScaleType(ScaleType.MATRIX);
        setImageResource(R.drawable.tomato);

        objectAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.tomato_splash);
        splasher = new Splasher(this);
        objectAnimatorSet.addListener(splasher);
        objectAnimatorSet.setTarget(this);
    }

    public void onTomatoThrown(int locationX, int locationY)
    {
        locationX -= getWidth()/2;
        locationY -= getHeight()/2;
        Matrix matrix = new Matrix();
        matrix.setTranslate(locationX, locationY);
        setImageMatrix(matrix);

        setPivotX(0);
        setPivotY(0);

        setVisibility(View.VISIBLE);
        objectAnimatorSet.start();
    }

    public void setSplashListener(SplashListener splashListener)
    {
        splasher.setSplashListener(splashListener);
    }
}
