package com.awesome.fling.tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.google.android.youtube.api.YouTube;
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

        YouTube.initialize(this, getResources().getString(R.string.youtube_api_key));

        setContentView(R.layout.main);

        YouTubePlayerView youtubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);

        registerPlayerView(youtubePlayerView);

        youtubePlayer = youtubePlayerView;

        youtubePlayer.loadVideo("2FuG66lT414");
    }
}
