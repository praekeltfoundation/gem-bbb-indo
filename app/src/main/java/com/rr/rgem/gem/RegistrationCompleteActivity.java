package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.pkmmte.view.CircularImageView;
import com.rr.rgem.gem.image.ImageHelper;
import com.rr.rgem.gem.image.ImageStorage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_done);
        ButterKnife.bind(this);

        persisted = new Persisted(this);
        currentImage = (CircularImageView)findViewById(R.id.imageButtonDone);
        currentImageName = ImageHelper.PROFILE_IMAGE_FILENAME;
        currentImageUrl = "/api/profile-image/%d/";

        File profile = Utils.getFileFromName(ImageHelper.IMAGE_DIRECTORY, currentImageName,
                getApplicationContext());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        logout();
        clearHistoryAndStart();
        finish();
    }

    @OnClick(R.id.buttonUpload)
    void onUploadClicked(Button button) {
        File file = Utils.getFileFromName(ImageHelper.IMAGE_DIRECTORY,
                ImageHelper.PROFILE_IMAGE_FILENAME, getApplicationContext());
        uploadImage(currentImageUrl, file);
    }

    void logout() {
        Persisted persisted = new Persisted(getApplicationContext());
        persisted.clearUser();
        persisted.clearToken();
        persisted.setLoggedIn(false);

        ImageStorage storage = new ImageStorage(getApplicationContext(), ImageHelper.IMAGE_DIRECTORY);
        storage.clearDirectory();
    }

    void clearHistoryAndStart() {
        Intent intent = new Intent(RegistrationCompleteActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RegistrationCompleteActivity.this.finish();
    }
}
