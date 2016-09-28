package com.rr.rgem.gem.views;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rr.rgem.gem.R;

/**
 * Created by jacob on 2016/09/19.
 */
public class ImageUploadDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Camera or Gallery?")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Build.VERSION.SDK_INT >= 23)
                        {
                            int hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);

                            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA},0);
                            }

                            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                getActivity().startActivityForResult(takePicture, 0);
                            }
                        }
                        else
                        {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            getActivity().startActivityForResult(takePicture, 0);
                        }
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Build.VERSION.SDK_INT >= 23)
                        {
                            int hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                            }

                            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                                getActivity().startActivityForResult(pickPhoto , 1);
                            }
                        }
                        else
                        {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                            getActivity().startActivityForResult(pickPhoto , 1);
                        }
                    }
                });

        return builder.create();
    }
}
