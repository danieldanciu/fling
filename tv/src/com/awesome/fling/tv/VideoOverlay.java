package com.awesome.fling.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class VideoOverlay extends View
{

    private Paint paint;
    private Bitmap tomato;

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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        tomato = BitmapFactory.decodeResource(getResources(), R.drawable.tomato);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(tomato, getWidth() / 2, getHeight() / 2, paint);
    }

    public void onTomatoThrown()
    {
        setVisibility(View.VISIBLE);
    }
}
