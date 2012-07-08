package com.awesome.fling.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.youtube.api.YouTubePlayer;

public class TomatoThrownHandler extends BroadcastReceiver implements SplashListener
{
    private YouTubePlayer youtubePlayer;
    private FrameLayout tomatoContainer;

    public TomatoThrownHandler(FrameLayout tomatoContainer, YouTubePlayer youtubePlayer)
    {
        this.tomatoContainer = tomatoContainer;
        this.youtubePlayer = youtubePlayer;
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
        tomatoOverlay.onTomatoThrown();
    }

    public void onSplashFinished(View viewDisplayingTheSplash)
    {
        tomatoContainer.removeView(viewDisplayingTheSplash);
    }
}
