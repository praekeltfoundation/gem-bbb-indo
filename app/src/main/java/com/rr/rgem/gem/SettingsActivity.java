package com.rr.rgem.gem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rr.rgem.gem.views.Utils;

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

                if (!Patterns.WEB_URL.matcher(newUrl).matches()) {
                    Utils.toast(SettingsActivity.this, "URL is not valid");
                    return;
                }

                persisted.saveUrl(newUrl);

                // Settings Changed
                if (!prevUrl.equals(newUrl)) {
                    // Clear activity history stack
                    ((GEM) getApplication()).onUrlChanged();
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SettingsActivity.this.finish();
                } else {
                    // Return to previous activity
                    SettingsActivity.this.finish();
                }
            }
        });
    }
}
