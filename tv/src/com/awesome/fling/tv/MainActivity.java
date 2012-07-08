package com.awesome.fling.tv;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.FrameLayout;
import com.awesome.fling.anymotecom.listener.Listener;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity
{
    public static final String ACTION_TOMATO_THROWN = "com.awesome.fling.ACTION_TOMATO_THROWN";
    private static final IntentFilter TOMATO_THROWN_INTENT_FILTER = new IntentFilter(ACTION_TOMATO_THROWN);

    private YouTubePlayer youtubePlayer;
    private TomatoThrownHandler tomatoThrownHandler;
    private Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeViews();

        // this will be replaced by incoming message from the device to load video
        youtubePlayer.loadVideo("vbDImUxb2nA");
        listener = new Listener(this, new AnymoteEventListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.throw_tomato)
        {
            sendBroadcast(new Intent(ACTION_TOMATO_THROWN));
            return true;
        }
        return false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(tomatoThrownHandler);
        listener.unregisterReceiver();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(tomatoThrownHandler, TOMATO_THROWN_INTENT_FILTER);
        listener.registerReceiver();
    }
    
    private void initializeViews()
    {
        setContentView(R.layout.main);

        YouTubePlayerView youtubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);

        registerPlayerView(youtubePlayerView);
        youtubePlayerView.setShowControls(false);
        youtubePlayerView.setUseSurfaceTexture(true);
        youtubePlayer = youtubePlayerView;

        FrameLayout tomatoContainer = (FrameLayout) findViewById(R.id.tomato_container);
        tomatoThrownHandler = new TomatoThrownHandler(tomatoContainer, youtubePlayer);
    }
}
