package com.awesome.fling.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class VideoOverlay extends View
{

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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 50, paint);
    }

    public void onTomatoThrown()
    {
        setVisibility(View.VISIBLE);
    }
}
