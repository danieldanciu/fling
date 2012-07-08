package com.awesome.fling.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.google.android.youtube.api.YouTubePlayer;

import java.util.Random;

public class TomatoThrownHandler extends BroadcastReceiver implements SplashListener
{
    private YouTubePlayer youtubePlayer;
    private int screenWidth;
    private int screenHeight;
    private FrameLayout tomatoContainer;

    Random random = new Random();

    public TomatoThrownHandler(FrameLayout tomatoContainer, YouTubePlayer youtubePlayer)
    {
        this.tomatoContainer = tomatoContainer;
        this.youtubePlayer = youtubePlayer;

        init();
    }

    private void init()
    {
        WindowManager wm = (WindowManager) tomatoContainer.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        // we are using api 12 which doesn't have the method getSize so we use the deprecated methods.
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        youtubePlayer.pause();
        VideoOverlay tomatoOverlay = new VideoOverlay(context);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        tomatoContainer.addView(tomatoOverlay, layoutParams);
        tomatoOverlay.setSplashListener(this);

        float x = random.nextFloat();
        float y = random.nextFloat();

        int locationX = (int) (screenWidth * x);
        int locationY = (int) (screenHeight * y);

        tomatoOverlay.onTomatoThrown(locationX, locationY);
    }

    public void onSplashFinished(View viewDisplayingTheSplash)
    {
        tomatoContainer.removeView(viewDisplayingTheSplash);
    }
}
