package com.awesome.fling.anymotecom;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TestComActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_com);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test_com, menu);
        return true;
    }

    
}
