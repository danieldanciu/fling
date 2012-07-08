package com.awesome.fling.tv;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.awesome.fling.anymotecom.AnymoteComm;
import com.awesome.fling.anymotecom.listener.Listener;
import com.google.android.youtube.api.YouTubeBaseActivity;
import com.google.android.youtube.api.YouTubePlayer;
import com.google.android.youtube.api.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity {
  public static final String ACTION_TOMATO_THROWN = "com.awesome.fling.ACTION_TOMATO_THROWN";
  private static final IntentFilter TOMATO_THROWN_INTENT_FILTER = new IntentFilter(
      ACTION_TOMATO_THROWN);

  private YouTubePlayer youtubePlayer;
  private TomatoThrownHandler tomatoThrownHandler;
  private Listener listener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initializeViews();

    // this will be replaced by incoming message from the device to load video
    youtubePlayer.loadVideo("vbDImUxb2nA");
    listener = new Listener(this, new AnymoteEventListener());
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }


  StringBuffer message = new StringBuffer();
  boolean isParsing;

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    System.out.println("Received key: " + keyCode);
    switch (keyCode) {
      case KeyEvent.KEYCODE_LEFT_BRACKET:
        isParsing = true;
        message.setLength(0);
        return true;
      case KeyEvent.KEYCODE_RIGHT_BRACKET:
        isParsing = false;
        messageDone(message.toString());
        return true;
      default:
        
        if (isParsing) {
          System.out.println("Char of keycode is " + (char)keyCode);
          message.append((char) keyCode);
          return true;
        }
    }
    return false;
  }

//  protected void onNewIntent(Intent intent) {
//    System.out.println("Received intent " + intent);
//    Bundle bundle = intent.getExtras();
//
//    if (bundle != null) {
//      String message = bundle.getString(AnymoteComm.DATA_MESSAGE);
//      System.out.println("Intent message is: " + message);
//      String[] params = message.split(" ");
//      // if ("play".equals(params[0])) {
//      // onAnymoteEvent.onPlayVideo(params[1]);
//      // } else if ("pause".equals(params[0])) {
//      // onAnymoteEvent.onPause();
//      // } else if ("placeTomato".equals(params[0])) {
//      // onAnymoteEvent.onPlaceTomato(Integer.parseInt(params[1]),
//      // Integer.parseInt(params[2]));
//      // } else
//      if ("throwTomato".equals(params[0])) {
//        // onAnymoteEvent.onThrowTomato(Integer.parseInt(params[1]),
//        // Integer.parseInt(params[2]));
//        System.out.println("Sending broadcast for throwing tomato");
//        sendBroadcast(new Intent(ACTION_TOMATO_THROWN));
//      }
//    }
//  }

  private void messageDone(String message) {
    System.out.println("Received message: " + message);
    //String[] params = message.split(" ");
      // params[1] and params[2] contain the coordinates
      Intent intent = new Intent(ACTION_TOMATO_THROWN);
      //intent.putExtra("x", params[0]);
      //intent.putExtra("y", params[1]);
      sendBroadcast(intent);
    // else if ("pause".equals(params[0])) {
    // onAnymoteEvent.onPause();
    // } else if ("placeTomato".equals(params[0])) {
    // onAnymoteEvent.onPlaceTomato(Integer.parseInt(params[1]),
    // Integer.parseInt(params[2]));
    // } else if ("throwTomato".equals(params[0])) {
    // onAnymoteEvent.onThrowTomato(Integer.parseInt(params[1]),
    // Integer.parseInt(params[2]));
    // }
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.throw_tomato) {
      sendBroadcast(new Intent(ACTION_TOMATO_THROWN));
      return true;
    }
    return false;
  }

  @Override
  protected void onStart() {
    super.onStart();
    registerReceiver(tomatoThrownHandler, TOMATO_THROWN_INTENT_FILTER);
    listener.registerReceiver();

  }

  @Override
  protected void onStop() {
    super.onStop();
    unregisterReceiver(tomatoThrownHandler);
    listener.unregisterReceiver();

  }

  private void initializeViews() {
    setContentView(R.layout.main);

    YouTubePlayerView youtubePlayerView =
        (YouTubePlayerView) findViewById(R.id.youtube_player_view);

    registerPlayerView(youtubePlayerView);
    youtubePlayerView.setShowControls(false);
    youtubePlayerView.setUseSurfaceTexture(true);
    youtubePlayer = youtubePlayerView;

    FrameLayout tomatoContainer = (FrameLayout) findViewById(R.id.tomato_container);
    tomatoThrownHandler = new TomatoThrownHandler(tomatoContainer, youtubePlayer);
  }
}
