package com.awesome.fling.anymotecom.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.awesome.fling.anymotecom.AnymoteComm;
import com.awesome.fling.anymotecom.FlingComm;
import com.awesome.fling.anymotecom.FlingCommImpl;
public class TestCommActivity extends Activity {
  private FlingComm anymoteComm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_comm);
        anymoteComm =
            new FlingCommImpl(this, new AnymoteComm.OnConnectedListener() {
              public void onConnected() {
                anymoteComm.throwTomato(true, 10, 10);
              }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test_comm, menu);
        return true;
    }
    
    @Override
    public void onDestroy() {
      super.onDestroy();
      anymoteComm.release();
    }

}
