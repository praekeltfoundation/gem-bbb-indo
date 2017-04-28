package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jinatonic.confetti.CommonConfetti;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentMainPage;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeDoneFragment extends Fragment {

    private static final String TAG = ChallengeDoneFragment.class.getName();
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge;

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

    @Inject
    Persisted persisted;

    public ChallengeDoneFragment() {
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
    public static ChallengeDoneFragment newInstance(BaseChallenge challenge) {
        ChallengeDoneFragment fragment = new ChallengeDoneFragment();
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
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_done, container, false);
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

    @OnClick(R.id.fragment_challenge_close)
    public void closeQuiz() {
        returnToParent(null);
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
        letItRainConfetti();
    }

    private void letItRainConfetti() {
        final boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        if (isLollipop)
            CommonConfetti.rainingConfetti(((ViewGroup) this.getView().getParent()), new int[]{Color.rgb(57, 21, 99), Color.rgb(185, 209, 84), Color.rgb(1, 191, 227)}).oneShot();
    }

    @OnClick(R.id.card_challenge_share)
    public void shareChallenge() {
        Context context = getContext();
        Bundle args = getArguments();
        if (context != null && args != null
                && args.containsKey(ChallengeActivity.ARG_PARTICIPANT_BADGE)) {
            Badge badge = args.getParcelable(ChallengeActivity.ARG_PARTICIPANT_BADGE);
            try {
                new SocialSharer(context).share(
                        getString(R.string.share_chooser_challenge_title), Uri.parse(badge.getSocialUrl()));
            } catch (NullPointerException e) {
                CrashlyticsHelper.log(TAG, "shareChallenge", String.format("Badge: %s", badge));
                if (badge != null)
                    CrashlyticsHelper.log(TAG, "shareChallenge", String.format("Badge.socialUrl: %s", badge.getSocialUrl()));
                CrashlyticsHelper.logException(e);
            }
        }
    }

    @OnClick(R.id.challenge_done_button)
    public void finishChallenge() {
        returnToParent(null);
    }

    private void returnToParent(ChallengeFragmentMainPage returnPage) {
        Bundle bundleFragment = this.getArguments();
        Badge participantBadge = bundleFragment.getParcelable(ChallengeActivity.ARG_PARTICIPANT_BADGE);
        Bundle bundle = new Bundle();
        Intent intent = new Intent();

        bundle.putParcelable(ChallengeActivity.ARG_CHALLENGE, challenge);
        bundle.putInt(ChallengeActivity.ARG_RETURNPAGE, returnPage != null ? returnPage.ordinal() : -1);

        FragmentActivity activity = getActivity();

        if (participantBadge != null) {
            bundle.putParcelable(ChallengeActivity.ARG_PARTICIPANT_BADGE, participantBadge);
            persisted.saveConvoParticipant(BotType.CHALLENGE_PARTICIPANT_BADGE, participantBadge, challenge);
            intent.putExtras(bundle);
        }

        if (activity.getParent() != null) {
            activity.getParent().setResult(Activity.RESULT_OK, intent);
        } else {
            activity.setResult(Activity.RESULT_OK, intent);
        }
        activity.finish();
    }
}
