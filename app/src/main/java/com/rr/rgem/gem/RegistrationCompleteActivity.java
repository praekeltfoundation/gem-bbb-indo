package com.rr.rgem.gem;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pkmmte.view.CircularImageView;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chris on 9/8/2016.
 */
public class RegistrationCompleteActivity extends AppCompatActivity {
    CircularImageView cv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_done);
        cv = (CircularImageView)findViewById(R.id.imageButtonDone);

        File profile = Utils.getFileFromName("profile.jpg", getApplicationContext());
        if (Utils.fileExists(profile))
        {
            try {
                Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(profile));
                cv.setImageBitmap(photo);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        cv.setOnClickListener(new View.OnClickListener(){
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
                RegistrationCompleteActivity.this.finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            File path = Utils.getFileFromName("profile.jpg", getApplicationContext());
            Bitmap profilePicture = null;

            switch(requestCode) {
                case 0: {

                    Bundle extras = data.getExtras();
                    profilePicture = (Bitmap) extras.get("data");
                    cv.setImageBitmap(profilePicture);

                /*
                Bundle extras = data.getExtras();
                InputStream inputStream = null;
                Bitmap bmp = null;
                try {
                    inputStream = contentLayout.getContext().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (inputStream != null) try {
                    bmp = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentImage.setImageBitmap(bmp);
                */
                    break;
                }
                case 1: {

                    Uri selectedImage = data.getData();
                    InputStream inputStream = null;
                    try {
                        inputStream = this.getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (inputStream != null) try {
                        profilePicture = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cv.setImageBitmap(profilePicture);
                    break;
                }
            }

            Utils.writeToFile(profilePicture, path);
        }
    }
}
