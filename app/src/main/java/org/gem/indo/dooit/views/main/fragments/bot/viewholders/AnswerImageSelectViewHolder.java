package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultCallback;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerImageSelectViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_image)
    SimpleDraweeView selectView;
    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;
    @Inject
    ActivityForResultHelper activityForResultHelper;
    @Inject
    Persisted persisted;
    @Inject
    FileUploadManager fileUploadManager;
    @Inject
    PermissionsHelper permissionsHelper;
    Uri imageUri;

    public AnswerImageSelectViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        this.dataModel = model;
        selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (BotMessageType.getValueOf(dataModel.getType())) {
                    case GALLERYUPLOAD:
                        activityForResultHelper.startActivityForResult(getContext(), new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), new ActivityForResultCallback() {
                            @Override
                            public void onActivityResultOK(Intent data) {
                                uploadImage(data);
                            }

                            @Override
                            public void onActivityResultCanceled(Intent data) {
                            }
                        });
                        break;
                    case CAMERAUPLOAD:
                        activityForResultHelper.startActivityForResult(getContext(), new Intent(MediaStore.ACTION_IMAGE_CAPTURE), new ActivityForResultCallback() {
                            @Override
                            public void onActivityResultOK(Intent data) {
                                uploadImage(data);
                            }

                            @Override
                            public void onActivityResultCanceled(Intent data) {
                            }
                        });
                        break;
                }
            }
        });
        if (botAdapter.getItemCount() - 1 == getAdapterPosition()) {
            permissionsHelper.askForPermission((Activity) getContext(), new String[] {PermissionsHelper.D_READ_EXTERNAL_STORAGE, PermissionsHelper.D_CAMERA}, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    selectView.performClick();
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }

    @Override
    protected void populateModel() {

    }

    private void uploadImage(Intent data) {
        imageUri = data.getData();
        if (imageUri == null) {
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), (Bitmap) data.getExtras().get("data"), "", ""));
            } catch (Throwable ex) {

            }
        }
        Uri pathUri = getRealPathFromURI(imageUri);
        selectView.setImageURI(imageUri);
        File file = new File(pathUri.getPath());
        if (file.length() > 0) {
            dismissDialog();
            Answer answer = new Answer();
            answer.setName(dataModel.getName());
            answer.setType(BotMessageType.IMAGE);
            answer.setValue(imageUri.toString());
            answer.setNext(dataModel.getNextOnFinish());
            answer.setRemoveOnSelect(dataModel.getName());
            answer.setText(null);
            tagsClickListener.onItemClicked(answer);
        }
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
