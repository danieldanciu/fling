package com.awesome.fling.anymotecom.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.awesome.fling.anymotecom.AnymoteComm;

public class Listener {

  private OnAnymoteEvent onAnymoteEvent;
  private Context context;
  private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle = intent.getExtras();

      if (bundle != null) {
        String message = bundle.getString(AnymoteComm.DATA_MESSAGE);
        String[] params = message.split(" ");
        if ("play".equals(params[0])) {
          onAnymoteEvent.onPlayVideo(params[1]);
        } else if ("pause".equals(params[0])) {
          onAnymoteEvent.onPause();
        } else if ("placeTomato".equals(params[0])) {
          onAnymoteEvent.onPlaceTomato(Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        } else if ("throwTomato".equals(params[0])) {
          onAnymoteEvent.onThrowTomato(Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        }
      }
    }
  };
  
  public void registerReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(AnymoteComm.INTENT_SEND_STRING);
    context.registerReceiver(receiver, filter);
  }
  
  public void unregisterReceiver() {
    context.unregisterReceiver(receiver);
  }
}
