package com.awesome.fling.anymotecom;

import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.KeyEvent;

import com.example.google.tv.anymotelibrary.client.AnymoteClientService;
import com.example.google.tv.anymotelibrary.client.AnymoteSender;

public class AnymoteCommImpl implements AnymoteComm {

  private final OnConnectedListener onConnectedListener;
  private final Context context;

  public AnymoteCommImpl(Context context, OnConnectedListener onConnectedListener) {
    Intent intent = new Intent(context.getApplicationContext(), AnymoteClientService.class);
    this.onConnectedListener = onConnectedListener;
    this.context = context;
    context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  static final Map<Character, Integer> map = new HashMap();
  static {
    map.put('0', KeyEvent.KEYCODE_0);
    map.put('1', KeyEvent.KEYCODE_1);
    map.put('2', KeyEvent.KEYCODE_2);
    map.put('3', KeyEvent.KEYCODE_3);
    map.put('4', KeyEvent.KEYCODE_4);
    map.put('5', KeyEvent.KEYCODE_5);
    map.put('6', KeyEvent.KEYCODE_6);
    map.put('7', KeyEvent.KEYCODE_7);
    map.put('8', KeyEvent.KEYCODE_8);
    map.put('9', KeyEvent.KEYCODE_9);
    map.put(' ', KeyEvent.KEYCODE_SPACE);
  }
  
  @Override
  public void sendString(String message) {

//    System.out.println("Sending anymote command: " + message);
      if (anymoteSender != null) {
        anymoteSender.sendKeyPress(KeyEvent.KEYCODE_LEFT_BRACKET);
//      for (int i=0; i< message.length(); ++i) {
//        anymoteSender.sendKeyPress(map.get(message.charAt(i)));
//      }
//      anymoteSender.sendKeyPress(KeyEvent.KEYCODE_RIGHT_BRACKET);
    } else {
      Intent intent2 = new Intent(context.getApplicationContext(), AnymoteClientService.class);
      context.bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
    }
  }
  
//  @Override
//  public void sendString(String message) {
//    System.out.println("Sending anymote command: " + message);
//    if (anymoteSender != null) {
//      Intent intent = new Intent(AnymoteComm.INTENT_SEND_STRING);
//      intent.putExtra(AnymoteComm.DATA_MESSAGE, message);
//      anymoteSender.sendUrl(intent.toUri(Intent.URI_INTENT_SCHEME));
//    } else {
//      Intent intent2 = new Intent(context.getApplicationContext(), AnymoteClientService.class);
//      context.bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
//    }
//  }
  
  @Override
  public void sendXY(int x, int y) {
    if (anymoteSender != null) {
      anymoteSender.sendScroll(x, y);
    } else {
      Intent intent2 = new Intent(context.getApplicationContext(), AnymoteClientService.class);
      context.bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
    }
  }

  private AnymoteSender anymoteSender;

  public class AnymoteListener implements AnymoteClientService.ClientListener {
    @Override
    public void onConnected(final AnymoteSender anymoteSender) {
      if (anymoteSender != null) {
        // Send events to Google TV using anymoteSender.
        // save handle to the anymoteSender instance.
        AnymoteCommImpl.this.anymoteSender = anymoteSender;
        if (onConnectedListener != null) {
          onConnectedListener.onConnected();
        }
      } else {
        // Show message to tell the user that the connection failed.
        // Try to connect again if needed.
      }
    }

    @Override
    public void onDisconnected() {
      // show message to tell the user about disconnection.
      // Try to connect again if needed.
      anymoteSender = null;
    }

    @Override
    public void onConnectionError() {
      // show message to tell the user about disconnection.
      // Try to connect again if needed.

      anymoteSender = null;
    }
  }

  /** Defines callbacks for service binding, passed to bindService() */
  private ServiceConnection mConnection = new ServiceConnection() {
    private AnymoteClientService mAnymoteClientService;
    private final AnymoteListener listener = new AnymoteListener();

    /*
     * ServiceConnection listener methods.
     */
    public void onServiceConnected(ComponentName name, IBinder service) {

      mAnymoteClientService =
          ((AnymoteClientService.AnymoteClientServiceBinder) service).getService();

      mAnymoteClientService.attachClientListener(listener);
    }

    public void onServiceDisconnected(ComponentName name) {
      mAnymoteClientService.detachClientListener(listener);
      mAnymoteClientService = null;
    }
  };

  @Override
  public void init() {
    // TODO Auto-generated method stub

  }

  @Override
  public void release() {
    context.unbindService(mConnection);
  }
}
