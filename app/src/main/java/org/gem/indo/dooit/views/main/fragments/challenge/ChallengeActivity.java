package org.gem.indo.dooit.views.main.fragments.challenge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.FrameLayout;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.PictureChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeNoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengePictureFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by frede on 2017/01/16.
 */

public class ChallengeActivity extends DooitActivity {

    public static final String TAG = "ChallengeMain";
    public static final String ARG_CHALLENGE = "challenge";
    public static final String ARG_PAGE = "challenge_page";
    public static final String ARG_PARTICIPANT = "participant";

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    @BindView(R.id.fragment_challenge_container)
    FrameLayout container;

    BaseChallenge challenge;
    Participant participant;

    public ChallengeActivity(){
        // Empty constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_challenge);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        Bundle args = getIntent().getExtras();
        if(args != null) {
            challenge = args.getParcelable(ARG_CHALLENGE);
            participant = args.getParcelable(ARG_PARTICIPANT);
            startChallenge(participant);
        }
    }

    private Fragment createEmptyFragment(ChallengeFragmentState state) {
        if (state == null) return null;
        switch (state) {
            case FREEFORM:
                return new ChallengeFreeformFragment();
            case PICTURE:
                return new ChallengePictureFragment();
            case QUIZ:
                return new ChallengeQuizFragment();
            case REGISTER:
                return new ChallengeRegisterFragment();
            default:
                return null;
        }
    }

    /*************************
     * State-keeping methods *
     *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            Fragment f = getSupportFragmentManager().getFragment(outState,challenge.toString());
            if (f != null) {
                f.onSaveInstanceState(outState);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    void startChallenge(Participant participant) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

    public static class Builder extends DooitActivityBuilder<ChallengeActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChallengeActivity.Builder create(Context context) {
            ChallengeActivity.Builder builder = new ChallengeActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChallengeActivity.class);
        }

        public Builder setArgs(Bundle bundle){
            intent.putExtras(bundle);
            return this;
        }
    }
}
