package com.awesome.fling.anymotecom;

public interface AnymoteComm {
  public interface OnConnectedListener {
    public void onConnected();
  }
  
  public void sendString(String message);
}
