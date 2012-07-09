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
import java.util.concurrent.atomic.AtomicInteger;

public class TomatoThrownHandler extends BroadcastReceiver implements SplashListener
{
    private YouTubePlayer youtubePlayer;
    private int screenWidth;
    private int screenHeight;
    private FrameLayout tomatoContainer;

    Random random = new Random();
    private boolean isPausing;
    private final AtomicInteger count = new AtomicInteger();
    private int totalTomatosForVideo;
    private boolean doneWithVideo;

    public TomatoThrownHandler(FrameLayout tomatoContainer, YouTubePlayer youtubePlayer)
    {
        this.tomatoContainer = tomatoContainer;
        this.youtubePlayer = youtubePlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        totalTomatosForVideo++;
        count.incrementAndGet();
        isPausing = true;
        youtubePlayer.pause();
        VideoOverlay tomatoOverlay = new VideoOverlay(context);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        tomatoContainer.addView(tomatoOverlay, layoutParams);
        tomatoOverlay.setSplashListener(this);

        float x = random.nextFloat();
        float y = random.nextFloat();

        tomatoOverlay.onTomatoThrown(x, y);
        if (totalTomatosForVideo > 7)
        {
            doneWithVideo = true;
        }
    }

    public void onSplashFinished(View viewDisplayingTheSplash)
    {
        tomatoContainer.removeView(viewDisplayingTheSplash);
        isPausing = false;
        int currentCount = count.decrementAndGet();
        if (currentCount == 0)
        {
            totalTomatosForVideo = 0;
            if (doneWithVideo)
            {
                doneWithVideo = false;
                if (youtubePlayer.hasNext())
                {
                    youtubePlayer.next();
                }
            }
            else
            {
                youtubePlayer.play();
            }
        }
    }
}
