package com.awesome.fling.anymotecom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;

import com.example.google.tv.anymotelibrary.client.AnymoteClientService;
import com.example.google.tv.anymotelibrary.client.AnymoteSender;

public class AnymoteCommImpl implements AnymoteComm {
  
  private final OnConnectedListener onConnectedListener;

  public AnymoteCommImpl(Context context, OnConnectedListener onConnectedListener) {
    Intent intent = new Intent(context.getApplicationContext(), AnymoteClientService.class);
    this.onConnectedListener = onConnectedListener;
    context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }
  
  @Override
  public void sendString(String message) {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM"));
    anymoteSender.sendUrl(intent.toUri(Intent.URI_INTENT_SCHEME));
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
}
