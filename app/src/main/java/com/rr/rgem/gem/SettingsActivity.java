package com.rr.rgem.gem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by Wimpie Victor on 2016/10/26.
 */
public class SettingsActivity extends AppCompatActivity {

    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        persisted = new Persisted(getApplicationContext());


    }
}
