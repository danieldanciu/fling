package com.awesome.fling.anymotecom;

public interface AnymoteComm {
  public static final String INTENT_SEND_STRING = "com.awesome.fling.anymotecom.SEND_STRING";
  public static final String DATA_MESSAGE = "message";
  public interface OnConnectedListener {
    public void onConnected();
  }
  
  public void init();
  public void sendString(String message);
  public void release();
  void sendXY(int x, int y);
}
