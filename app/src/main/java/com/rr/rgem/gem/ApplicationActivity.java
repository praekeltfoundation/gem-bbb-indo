package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.service.FileUploader;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.service.model.User;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;

/**
 * Created by jacob on 2016/10/04.
 */

public class ApplicationActivity extends AppCompatActivity {

    private static final String TAG = "ApplicationActivity";

    public ImageView currentImage;
    public String currentImageName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.wtf(TAG, "onActivityResult ; Result code: " + resultCode + " ; Request code: " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (Validation.isEmpty(currentImageName)) {
                //Date timeStamp = Calendar.getInstance().getTime();
                //SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
                //currentImageName =  formatter.format(timeStamp);
                //currentImageName += ".jpg";
                currentImageName = "profile.jpg";
            }

            Bundle extras = data.getExtras();
            File path = Utils.getFileFromName("imageDir", currentImageName, getApplicationContext());
            Bitmap profilePicture = null;

            switch(requestCode) {
                case 0: {
                    profilePicture = (Bitmap) extras.get("data");
                    currentImage.setImageBitmap(profilePicture);
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
                    currentImage.setImageBitmap(profilePicture);
                    break;
                }
            }

            Utils.writeImageToFile(profilePicture, path);
            uploadImage(path);
        }
    }

    protected void uploadImage(File file) {
        Utils.toast(this, "Uploading Image");
        Persisted persisted = new Persisted(this);
        User user = persisted.loadUser();

        if (!user.hasId()) {
            Utils.toast(this, "User id not set");
            return;
        }

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        OkHttpClient client = factory.getClient();
        FileUploader uploader = new FileUploader(client);

        String url;
        String mediaType = "image/jpg";

        try {
            url = factory.joinUrl(String.format("/api/profile-image/%d/", user.getId()));
        } catch (MalformedURLException e) {
            return;
        }

        uploader.send(url, mediaType, file, new FileUploader.UploadCallback() {
            @Override
            public void onComplete() {
                Utils.toast(ApplicationActivity.this, "File Upload Success");
            }

            @Override
            public void onFailure() {
                Utils.toast(ApplicationActivity.this, "File Upload Failed");
            }
        });
    }
}
