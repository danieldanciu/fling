package com.awesome.fling.anymotecom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TestComActivity extends Activity {
  private AnymoteComm anymoteComm;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_com);
    anymoteComm =
        new AnymoteCommImpl(this, new AnymoteComm.OnConnectedListener() {
          @Override
          public void onConnected() {
            anymoteComm.sendString("ba");
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_test_com, menu);
    return true;
  }

}
