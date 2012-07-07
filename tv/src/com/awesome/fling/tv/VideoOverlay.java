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

    private Bitmap tomato;
    private int tomatoWidth;
    private int tomatoHeight;

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
        tomato = BitmapFactory.decodeResource(getResources(), R.drawable.tomato);
        tomatoWidth = tomato.getWidth();
        tomatoHeight = tomato.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(tomato, (getWidth() / 2) - tomatoWidth / 2, (getHeight() / 2) - tomatoHeight / 2, null);
    }

    public void onTomatoThrown()
    {
        setVisibility(View.VISIBLE);
    }
}
