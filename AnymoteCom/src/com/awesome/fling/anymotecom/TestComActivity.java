package com.awesome.fling.anymotecom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

import com.example.google.tv.anymotelibrary.client.AnymoteClientService;
import com.example.google.tv.anymotelibrary.client.AnymoteSender;

public class TestComActivity extends Activity {

  public class AnymoteListener implements AnymoteClientService.ClientListener {

    private AnymoteSender anymoteSender;

    @Override
    public void onConnected(final AnymoteSender anymoteSender) {
      if (anymoteSender != null) {
        // Send events to Google TV using anymoteSender.
        // save handle to the anymoteSender instance.
        this.anymoteSender = anymoteSender;
      } else {
        // Show message to tell the user that the connection failed.
        // Try to connect again if needed.
      }
    }

    @Override
    public void onDisconnected() {
      // show message to tell the user about disconnection.
      // Try to connect again if needed.
      this.anymoteSender = null;
    }

    @Override
    public void onConnectionError() {
      // show message to tell the user about disconnection.
      // Try to connect again if needed.

      this.anymoteSender = null;
    }
  }
  
  /** Defines callbacks for service binding, passed to bindService() */
  private ServiceConnection mConnection = new ServiceConnection() {
      private AnymoteClientService mAnymoteClientService;

      /*
       * ServiceConnection listener methods.
       */
      public void onServiceConnected(ComponentName name, IBinder service) {

          mAnymoteClientService = ((AnymoteClientService.AnymoteClientServiceBinder) service)
                  .getService();
          mAnymoteClientService.attachClientListener(AnymoteListener.this);
      }

      public void onServiceDisconnected(ComponentName name) {
          mAnymoteClientService.detachClientListener(AnymoteListener.this);
          mAnymoteClientService = null;
      }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_com);
 // Bind to the AnymoteClientService
    Intent intent = new Intent(getApplicationContext(), AnymoteClientService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_test_com, menu);
    return true;
  }


}
