package com.awesome.fling.tv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
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
    private Bitmap bitmap;
    private int cornerLocationX;
    private int cornerLocationY;
    private Paint paint;

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
        setImageToDraw(R.drawable.tomato);


        objectAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.tomato_splash);
        splasher = new Splasher(this);
        objectAnimatorSet.addListener(splasher);
        objectAnimatorSet.setTarget(this);

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        paint.setAlpha((int) (255 * getAlpha()));
        canvas.drawBitmap(bitmap, cornerLocationX, cornerLocationY +  getTranslationY(), paint);
    }

    public void onTomatoThrown(int locationX, int locationY)
    {
        cornerLocationX = locationX - bitmapWidth;
        cornerLocationY = locationY - bitmapHeight;

        setPivotX(locationX);
        setPivotY(locationY);

        setVisibility(View.VISIBLE);
        objectAnimatorSet.start();
    }

    public void setSplashListener(SplashListener splashListener)
    {
        splasher.setSplashListener(splashListener);
    }

    public void setImageToDraw(int resourceId)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts);
        bitmapWidth = opts.outWidth;
        bitmapHeight = opts.outHeight;
    }
}
