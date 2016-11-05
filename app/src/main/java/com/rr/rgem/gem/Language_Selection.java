package com.rr.rgem.gem;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class Language_Selection extends ApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language__selection);

       final Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));

        Button englishButton=(Button)findViewById(R.id.english);
        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale myLocale = new Locale("");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                getNextLayout(false);
            }
        });

        Button pahasaButton=(Button)findViewById(R.id.pahasa);
                pahasaButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Locale myLocale = new Locale("in");
                        Resources res = getResources();
                        DisplayMetrics dm = res.getDisplayMetrics();
                        Configuration conf = res.getConfiguration();
                        conf.locale = myLocale;
                        res.updateConfiguration(conf, dm);
                        getNextLayout(false);

                    }
                });


    }

    void getNextLayout(boolean check)
    {
        if(check==false)
        {
            Intent setting = new Intent(Language_Selection.this, OnBoardingActivity.class);
            startActivity(setting);
            Language_Selection.this.finish();
        }
    }
}
