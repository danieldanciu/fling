package com.awesome.fling.anymotecom;

import android.content.Context;

import com.awesome.fling.anymotecom.AnymoteComm.OnConnectedListener;

public class FlingCommImpl implements FlingComm {
  
  private AnymoteCommImpl anymoteComm;

  public FlingCommImpl(Context context, OnConnectedListener onConnectedListener) {
    this.anymoteComm = new AnymoteCommImpl(context, onConnectedListener);
  }

  @Override
  public void throwTomato(boolean isFresh, int x, int y) {
    anymoteComm.sendString("sendString " + x + " " + y);
  }
  
  @Override
  public void pauseVideo() {
    anymoteComm.sendString("pause");
  }

  @Override
  public void playVideo(String videoId) {
    anymoteComm.sendString("play " + videoId);
  }

  @Override
  public void release() {
    anymoteComm.release();
  }

}
