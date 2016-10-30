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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chris on 9/8/2016.
 */
public class RegistrationCompleteActivity extends ApplicationActivity {

    private static final String TAG = "RegistrationCompleteActivity";

    @BindView(R.id.buttonRegistrationDone) Button buttonDone;
    @BindView(R.id.buttonRegistrationUndo) Button buttonDeregister;
    @BindView(R.id.buttonUpload) Button buttonUpload;

    private Persisted persisted;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_done);
        ButterKnife.bind(this);

        persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));
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
    }

    @OnClick(R.id.buttonRegistrationDone)
    void onDoneClicked(Button button) {
        Intent intent = new Intent(RegistrationCompleteActivity.this, SavingsActivity.class);
        RegistrationCompleteActivity.this.startActivity(intent);
        RegistrationCompleteActivity.this.finish();
    }

    @OnClick(R.id.buttonRegistrationUndo)
    void onDeregisterClicked(Button button) {
        persisted.setRegistered(false);
        persisted.clearToken();
        persisted.clearUser();
        persisted.setLoggedIn(false);
        finish();
    }

    @OnClick(R.id.buttonUpload)
    void onUploadClicked(Button button) {
        File file = Utils.getFileFromName("imageDir", "profile.jpg", getApplicationContext());
        uploadImage(file);
    }
}
