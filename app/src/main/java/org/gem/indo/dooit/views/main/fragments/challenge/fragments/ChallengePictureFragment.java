package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengePictureFragment extends Fragment {
    @Inject
    PermissionsHelper permissionsHelper;

    @BindView(R.id.fragment_challenge_picture_image)
    SimpleDraweeView image;

    @BindView(R.id.fragment_challenge_picture_text)
    TextView subtitle;

    @BindView(R.id.fragment_challenge_picture_submit_button)
    Button submitButton;

    public ChallengePictureFragment() {
        // Required empty public constructor
    }

    public static ChallengePictureFragment newInstance(String param1, String param2) {
        ChallengePictureFragment fragment = new ChallengePictureFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_picture, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.fragment_challenge_picture_image)
    public void selectImage() {
        final CharSequence[] items = {
                getString(R.string.label_take_picture),
                getString(R.string.label_select_gallery),
                getString(R.string.label_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add picture submission!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO: Use Enums or Constants
                switch (item) {
                    case 0:
                        startCamera();
                        break;
                    case 1:
                        startGallery();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    protected void startCamera() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                permissionsHelper.askForPermission(ChallengePictureFragment.this, PermissionsHelper.D_CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
                        }
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(getContext(), "Can't take ic_d_profile image without camera permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(getContext(), "Can't take ic_d_profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void startGallery() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(getContext(), "Can't take ic_d_profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
