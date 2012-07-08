package com.awesome.fling.anymotecom.listener;

public interface OnAnymoteEvent {
  public void onPause();
  public void onPlayVideo(String videoId);
  public void onPlaceTomato(int x, int y);
  public void onThrowTomato(int x, int y);
}
