package com.awesome.fling.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.youtube.api.YouTubePlayer;

public class TomatoThrownHandler extends BroadcastReceiver
{
    private VideoOverlay overlay;
    private YouTubePlayer youtubePlayer;

    public TomatoThrownHandler(VideoOverlay overlay, YouTubePlayer youtubePlayer)
    {
        this.overlay = overlay;
        this.youtubePlayer = youtubePlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        youtubePlayer.pause();
        overlay.onTomatoThrown();
    }
}
