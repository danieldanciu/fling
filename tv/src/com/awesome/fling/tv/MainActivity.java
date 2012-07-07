package com.awesome.fling.tv;

import android.os.Bundle;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity
{

    private YouTubePlayer youtubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeViews();

        // this will be replaced by incoming message from the device to load video
        youtubePlayer.loadVideo("2FuG66lT414");
    }

    private void initializeViews()
    {
        setContentView(R.layout.main);

        YouTubePlayerView youtubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        registerPlayerView(youtubePlayerView);
        youtubePlayer = youtubePlayerView;
    }
}
