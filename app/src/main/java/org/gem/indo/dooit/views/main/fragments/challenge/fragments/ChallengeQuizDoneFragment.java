package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentMainPage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeQuizDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeQuizDoneFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge;

    @BindView(R.id.fragment_challenge_quiz_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.challenge_done_card)
    CardView doneCard;

    @BindView(R.id.card_challenge_image)
    SimpleDraweeView challengeImage;

    @BindView(R.id.card_challenge_complete_text)
    TextView subtitle;

    @BindView(R.id.card_challenge_title)
    TextView title;

    @BindView(R.id.challenge_done_button)
    Button doneButton;

    @BindView(R.id.fragment_challenge_close)
    ImageButton close;

    Unbinder unbinder = null;

    public ChallengeQuizDoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param challenge The challenge that was completed.
     * @return A new instance of fragment ChallengeDoneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeQuizDoneFragment newInstance(BaseChallenge challenge) {
        ChallengeQuizDoneFragment fragment = new ChallengeQuizDoneFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_quiz_done, container, false);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, view);
        unbinder = ButterKnife.bind(this, view);
        title.setText(challenge.getName());
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        challengeImage.setImageURI(challenge.getImageURL());
        if (mProgressBar != null){
            mProgressBar.setProgress(100);
        }
    }

    @OnClick(R.id.fragment_challenge_close)
    public void closeQuiz(){
        returnToParent(null);
    }

    @OnClick(R.id.challenge_done_button)
    public void finishChallenge() {
        returnToParent(null);
    }

    private void returnToParent(ChallengeFragmentMainPage returnPage) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChallengeActivity.ARG_CHALLENGE,challenge);
        bundle.putInt(ChallengeActivity.ARG_RETURNPAGE, returnPage != null ? returnPage.ordinal() : -1);
        intent.putExtras(bundle);

        if (activity.getParent() == null) {
            activity.setResult(Activity.RESULT_OK, intent);
        } else {
            activity.getParent().setResult(Activity.RESULT_OK, intent);
        }
        activity.finish();
    }
}
