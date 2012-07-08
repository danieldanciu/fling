package com.awesome.fling.tv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int bitmapWidth;
    private int bitmapHeight;

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
        int drawableResource = R.drawable.tomato;
        setImageResource(drawableResource);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), drawableResource, opts);
        bitmapWidth = opts.outWidth;
        bitmapHeight = opts.outHeight;


        objectAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.tomato_splash);
        splasher = new Splasher(this);
        objectAnimatorSet.addListener(splasher);
        objectAnimatorSet.setTarget(this);
    }

    public void onTomatoThrown(int locationX, int locationY)
    {
        int cornerLocationX = locationX - bitmapWidth;
        int cornerLocationY = locationY - bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.setTranslate(cornerLocationX, cornerLocationY);
        setImageMatrix(matrix);

        setPivotX(locationX);
        setPivotY(locationY);

        setVisibility(View.VISIBLE);
        objectAnimatorSet.start();
    }

    public void setSplashListener(SplashListener splashListener)
    {
        splasher.setSplashListener(splashListener);
    }
}
