package com.rr.rgem.gem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Wimpie Victor on 2016/10/26.
 */
public class SettingsActivity extends AppCompatActivity {

    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        persisted = new Persisted(getApplicationContext());

        EditText editViewUrl = (EditText) findViewById(R.id.editTextUrl);
        editViewUrl.setText(persisted.loadUrl());

        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editViewUrl = (EditText) findViewById(R.id.editTextUrl);

                String prevUrl = persisted.loadUrl();
                String newUrl = editViewUrl.getText().toString();

                persisted.saveUrl(newUrl);

                // Settings Changed
                if (!prevUrl.equals(newUrl)) {
                    // Clear activity history stack
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                    SettingsActivity.this.finish();
                } else {
                    // Return to previous activity
                    SettingsActivity.this.finish();
                }
            }
        });
    }
}
