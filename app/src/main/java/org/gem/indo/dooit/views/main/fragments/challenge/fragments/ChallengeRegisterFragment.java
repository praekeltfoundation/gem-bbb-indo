package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.TextSpannableHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.PictureChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentState;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.HasChallengeFragmentState;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeRegisterFragment extends Fragment implements HasChallengeFragmentState {

    private static final String TAG = "ChallengeRegister";
    private static final String ARG_HASACTIVE = "has_active";
    private static final ChallengeFragmentState FRAGMENT_STATE = ChallengeFragmentState.REGISTER;
    private static final String SCREEN_NAME_TERMS = "Challenge Terms & Conditions";

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persist;

    @BindView(R.id.fragment_challenge_register_image)
    SimpleDraweeView topImage;

    @BindView(R.id.fragment_challenge_sub_title_text_view)
    TextView title;

    @BindView(R.id.fragment_challenge_name_text_view)
    TextView name;

    @BindView(R.id.fragment_challenge_expire_date_text_view)
    TextView date;

    @BindView(R.id.fragment_challenge_instruction_text_vew)
    TextView instruction;

    @BindView(R.id.fragment_challenge_t_c_text_view)
    TextView tc;

    @BindView(R.id.fragment_challenge_register_button)
    Button register;

    @BindString(R.string.challenge_deadline_message)
    String deadlineMessage;

    private boolean hasActive = false;
    private BaseChallenge challenge;
    private Subscription participantSubscription = null;
    private Unbinder unbinder = null;

    public ChallengeRegisterFragment() {
        // Required empty public constructor
    }

    public static ChallengeRegisterFragment newInstance(BaseChallenge challenge) {
        ChallengeRegisterFragment fragment = new ChallengeRegisterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ChallengeActivity.ARG_CHALLENGE, challenge);
        args.putBoolean(ARG_HASACTIVE, false);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChallengeRegisterFragment newInstance(BaseChallenge challenge, boolean hasActive) {
        ChallengeRegisterFragment fragment = new ChallengeRegisterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ChallengeActivity.ARG_CHALLENGE, challenge);
        args.putBoolean(ARG_HASACTIVE, hasActive);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ChallengeActivity.ARG_CHALLENGE);
            hasActive = getArguments().getBoolean(ARG_HASACTIVE);
        }
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (hasActive) {
            register.setText(getText(R.string.label_continue));
        }

        String tcString = getResources().getString(R.string.challenge_t_c);
        TextSpannableHelper spanHelper = new TextSpannableHelper();
        tc.setText(spanHelper.styleText(getContext(), R.style.AppTheme_TextView_Bold_Small_Accented, tcString));

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(challenge.getName());
        date.setText(deadlineMessage + " " + challenge.getDeactivationDate().toLocalDateTime().toString("yyyy-MM-dd HH:mm"));
        instruction.setText(challenge.getInstruction());

        if (TextUtils.isEmpty(instruction.getText())) {
            instruction.setVisibility(View.GONE);
        } else {
            instruction.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(title.getText())) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
        }

        String imgUrl = challenge.getImageURL();
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(challenge.getImageURL()))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(topImage.getController())
                    .build();
            topImage.setController(controller);
        }
    }

    @Override
    public void onStop() {
        if (participantSubscription != null) {
            participantSubscription.unsubscribe();
        }
        super.onStop();
    }

    private Fragment startQuizChallenge(Participant participant, QuizChallenge quizChallenge) {
        if (quizChallenge.getQuestions() == null || quizChallenge.getQuestions().size() <= 0) {
            return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
        }

        for (QuizChallengeQuestion q : quizChallenge.getQuestions()) {
            // check for empty question or empty list of options for question
            if (TextUtils.isEmpty(q.getText())) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
            } else if (q.getOptions() == null || q.getOptions().size() <= 0) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
            }

            // check whether any options are empty or none of the question's options are correct
            boolean hasCorrect = false;
            for (QuizChallengeOption o : q.getOptions()) {
                if (TextUtils.isEmpty(o.getText())) {
                    return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_empty_option));
                }
                hasCorrect |= o.getCorrect();
            }
            if (!hasCorrect) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_correct_answer));
            }
        }
        return ChallengeQuizFragment.newInstance(participant, quizChallenge);
    }

    private Fragment startFreeformChallenge(Participant participant, FreeformChallenge freeformChallenge) {
        if (freeformChallenge.getQuestion() == null) {
            return ChallengeNoneFragment.newInstance("Freeform challenge has no question.");
        } else if (freeformChallenge.getQuestion().getText() == null ||
                freeformChallenge.getQuestion().getText().isEmpty()) {
            return ChallengeNoneFragment.newInstance("Freeform challenge question is empty.");
        }
        return ChallengeFreeformFragment.newInstance(participant, freeformChallenge);
    }

    private Fragment startPictureChallenge(Participant participant, PictureChallenge pictureChallenge) {
//        if (pictureChallenge.getQuestion() == null) {
//            return ChallengeNoneFragment.newInstance("Picture challenge has no question.");
//        } else if (pictureChallenge.getQuestion().getText() == null ||
//                pictureChallenge.getQuestion().getText().isEmpty()) {
//            return ChallengeNoneFragment.newInstance("Picture challenge question is empty.");
//        }
        return ChallengePictureFragment.newInstance(participant, pictureChallenge);
    }

    @OnClick(R.id.fragment_challenge_t_c_text_view)
    void termsClick(View view) {
        MinimalWebViewActivity.Builder.create(getContext())
                .setUrl(challenge.getTermsUrl())
                .setScreenName(SCREEN_NAME_TERMS)
                .startActivity();
    }

    @OnClick(R.id.fragment_challenge_register_button)
    void registerClick() {
        Participant participant = new Participant();
        participant.setChallenge(challenge.getId());
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.label_loading));
        dialog.show();

        if (persist.hasCurrentChallenge()) {
            try {
                challenge = persist.getCurrentChallenge();
                if (challenge.getDeactivationDate().isAfterNow()) {
                    persist.setActiveChallenge(challenge);
                    startChallenge(participant);
                    dialog.dismiss();
                } else {
                    //this means the persisted challenge has expired
                    //make call to server
                    persist.clearCurrentChallenge();
                    Snackbar.make(getView(), R.string.challenge_persisted_challenge_thrown_out, Snackbar.LENGTH_LONG);
                    participantSubscription = challengeManager.registerParticipant(
                            participant,
                            new DooitErrorHandler() {
                                @Override
                                public void onError(DooitAPIError error) {
                                    Snackbar.make(getView(), R.string.challenge_could_not_confirm_registration, Snackbar.LENGTH_LONG).show();
                                }
                            }
                    ).doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            dialog.dismiss();
                        }
                    }).subscribe(new Action1<Participant>() {
                        @Override
                        public void call(Participant participant1) {
                            persist.setActiveChallenge(challenge);
                            startChallenge(participant1);
                        }
                    });
                }
            } catch (Exception e) {
                Log.d(TAG, "Could not load persisted challenge");
                persist.clearCurrentChallenge();
                Snackbar.make(getView(), R.string.challenge_persisted_challenge_thrown_out, Snackbar.LENGTH_LONG);
                //make call to server
                participantSubscription = challengeManager.registerParticipant(
                        participant,
                        new DooitErrorHandler() {
                            @Override
                            public void onError(DooitAPIError error) {
                                Snackbar.make(getView(), R.string.challenge_could_not_confirm_registration, Snackbar.LENGTH_LONG).show();
                            }
                        }
                ).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        dialog.dismiss();
                    }
                }).subscribe(new Action1<Participant>() {
                    @Override
                    public void call(Participant participant1) {
                        persist.setActiveChallenge(challenge);
                        startChallenge(participant1);
                    }
                });
            }
        } else {
            //make call to server
            participantSubscription = challengeManager.registerParticipant(
                    participant,
                    new DooitErrorHandler() {
                        @Override
                        public void onError(DooitAPIError error) {
                            Snackbar.make(getView(), R.string.challenge_could_not_confirm_registration, Snackbar.LENGTH_LONG).show();
                        }
                    }
            ).doAfterTerminate(new Action0() {
                @Override
                public void call() {
                    dialog.dismiss();
                }
            }).subscribe(new Action1<Participant>() {
                @Override
                public void call(Participant participant1) {
                    persist.setActiveChallenge(challenge);
                    startChallenge(participant1);
                }
            });
        }
    }

    void startChallenge(Participant participant) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (challenge.getType()) {
            case QUIZ:
                if (challenge instanceof QuizChallenge) {
                    fragment = startQuizChallenge(participant, (QuizChallenge) challenge);
                }
                break;
            case FREEFORM:
                if (challenge instanceof FreeformChallenge) {
                    fragment = startFreeformChallenge(participant, (FreeformChallenge) challenge);
                }
                break;
            case PICTURE:
                if (challenge instanceof PictureChallenge) {
                    fragment = startPictureChallenge(participant, (PictureChallenge) challenge);
                }
                break;
            default:
                throw new RuntimeException("Invalid challenge type provided");
        }

        if (fragment != null) {
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
            ft.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.RESPONSE_GALLERY_REQUEST_CHALLENGE_IMAGE:
            case RequestCodes.RESPONSE_CAMERA_REQUEST_CHALLENGE_IMAGE:
                if (getActivity() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment fragment = new ChallengePictureFragment();
                    ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
                    ft.commit();
                }
                break;
        }
    }

    @Override
    public ChallengeFragmentState getFragmentState() {
        return FRAGMENT_STATE;
    }
}
