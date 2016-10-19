package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.pkmmte.view.CircularImageView;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by chris on 9/8/2016.
 */
public class RegistrationCompleteActivity extends ApplicationActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_done);
        currentImage = (CircularImageView)findViewById(R.id.imageButtonDone);
        currentImageName = "profile.jpg";

        File profile = Utils.getFileFromName("imageDir", "profile.jpg", getApplicationContext());
        if (Utils.fileExists(profile))
        {
            try {
                Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(profile));
                currentImage.setImageBitmap(photo);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        currentImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                FragmentManager manager = getSupportFragmentManager();
                ImageUploadDialog dialog = new ImageUploadDialog();
                dialog.show(manager, "dialog");
            }
        });
        final Button button = (Button) findViewById(R.id.buttonRegistrationDone);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationCompleteActivity.this, SavingsActivity.class);
                RegistrationCompleteActivity.this.startActivity(intent);
                RegistrationCompleteActivity.this.finish();
            }
        });
        final Button buttonDeregister = (Button) findViewById(R.id.buttonRegistrationUndo);
        buttonDeregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));
                persisted.setRegistered(false);
                persisted.clearToken();
                persisted.clearUser();
                RegistrationCompleteActivity.this.finish();
            }
        });
    }
}
