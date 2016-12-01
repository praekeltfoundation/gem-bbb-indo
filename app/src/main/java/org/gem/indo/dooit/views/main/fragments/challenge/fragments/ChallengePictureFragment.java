package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.PictureChallenge;
import org.gem.indo.dooit.models.challenge.PictureChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentState;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.HasChallengeFragmentState;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengePictureFragment extends Fragment {

    /*************
     * Variables *
     *************/

    public static final String TAG = "PictureChallenge";
    public static final String ARG_IMGURI = "challenge_picture_uri";
    private static final ChallengeFragmentState FRAGMENT_STATE = ChallengeFragmentState.PICTURE;

    @Inject
    PermissionsHelper permissionsHelper;

    @Inject
    FileUploadManager fileUploadManager;

    @BindView(R.id.fragment_challenge_picture_image)
    SimpleDraweeView image;

    @BindView(R.id.fragment_challenge_picture_text)
    TextView subtitle;

    @BindView(R.id.fragment_challenge_picture_submit_button)
    Button submitButton;

    private Participant participant = null;
    private PictureChallenge challenge = null;
    private String imagePath = null;
    private Uri imageUri = null;
    private Unbinder unbinder = null;


    /****************
     * Constructors *
     ****************/

    public ChallengePictureFragment() {
        // Required empty public constructor
    }

    public static ChallengePictureFragment newInstance(Participant participant, PictureChallenge challenge) {
        ChallengePictureFragment fragment = new ChallengePictureFragment();
        Bundle args = new Bundle();
        args.putParcelable(ChallengeFragment.ARG_PARTICIPANT, participant);
        args.putParcelable(ChallengeFragment.ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }


    /************************
     * Life-cycle overrides *
     ************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            participant = getArguments().getParcelable(ChallengeFragment.ARG_PARTICIPANT);
            challenge = getArguments().getParcelable(ChallengeFragment.ARG_CHALLENGE);
        }
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_picture, container, false);
        unbinder = ButterKnife.bind(this, view);
        PictureChallengeQuestion question = challenge.getQuestion();
        if (savedInstanceState != null) {
            String uriString = savedInstanceState.getString(ARG_IMGURI);
            if (uriString != null && !uriString.isEmpty()) {
                image.setImageURI(Uri.parse(uriString));
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
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


    /***************************
     * Image selection helpers *
     ***************************/

    protected void startCamera() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                permissionsHelper.askForPermission(ChallengePictureFragment.this, PermissionsHelper.D_CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.RESPONSE_CAMERA_REQUEST_CHALLENGE_IMAGE);
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
                startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCodes.RESPONSE_GALLERY_REQUEST_CHALLENGE_IMAGE);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(getContext(), "Can't take ic_d_profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.fragment_challenge_picture_submit_button)
    public void submitImage() {
        if (imagePath == null || imagePath.isEmpty()) {
            Log.d(TAG, "Attempted to submit empty image");
            Toast.makeText(getContext(), "Must select a picture to submit", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Uploading image");
        ContentResolver cR = getActivity().getContentResolver();
        fileUploadManager.uploadParticipantPicture(participant.getId(), cR.getType(imageUri), new File(imagePath), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not upload image");
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                Log.d(TAG, "Uploaded image");
                returnToParent();
            }
        });
    }


    /***********
     * Cleanup *
     ***********/

    private void returnToParent() {
        Fragment f = getParentFragment();
        if (f != null && f instanceof ChallengeFragment) {
            ((ChallengeFragment) f).loadChallenge();
        }
    }


    /*************************
     * State-keeping methods *
     *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putSerializable(ChallengeFragment.ARG_PAGE, FRAGMENT_STATE);
            outState.putParcelable(ChallengeFragment.ARG_PARTICIPANT, participant);
            outState.putParcelable(ChallengeFragment.ARG_CHALLENGE, challenge);
            outState.putString(ARG_IMGURI, imageUri == null ? null : imageUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RESPONSE_GALLERY_REQUEST_CHALLENGE_IMAGE ||
                requestCode == RequestCodes.RESPONSE_CAMERA_REQUEST_CHALLENGE_IMAGE) {
            if (resultCode == RESULT_OK) {
                imagePath = MediaUriHelper.getPath(getContext(), data.getData());
                imageUri = data.getData();
                if (image != null) {
                    image.setImageURI(imageUri);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
