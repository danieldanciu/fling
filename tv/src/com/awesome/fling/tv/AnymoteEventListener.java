package com.awesome.fling.tv;

import com.awesome.fling.anymotecom.listener.OnAnymoteEvent;

public class AnymoteEventListener implements OnAnymoteEvent {

  public void onPause() {
    System.out.println("=======onPause");
  }

  public void onPlayVideo(String videoId) {
    // TODO Auto-generated method stub

  }

  public void onPlaceTomato(int x, int y) {
    System.out.println("=======onPlaceTomato: " + x + " " + y);
  }

  public void onThrowTomato(int x, int y) {
    System.out.println("=======onThrowTomato: " + x + " " + y);
  }
}
