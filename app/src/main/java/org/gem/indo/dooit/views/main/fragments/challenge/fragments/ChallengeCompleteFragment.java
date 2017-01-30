package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rudolph Jacobs on 2016-11-16.
 */

public class ChallengeCompleteFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge = null;
    private OnOptionChangeListener optionChangeListener = null;

    @BindView(R.id.card_challenge_title)
    TextView title;

    @BindView(R.id.card_challenge_image)
    SimpleDraweeView image;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengeCompleteFragment() {
        // Required empty constructor
    }

    @SuppressWarnings("unused") public static ChallengeCompleteFragment newInstance() {
        ChallengeCompleteFragment fragment = new ChallengeCompleteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ChallengeCompleteFragment newInstance(@NonNull BaseChallenge challenge) {
        ChallengeCompleteFragment fragment = new ChallengeCompleteFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.card_challenge_end, container, false);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, view);
        ButterKnife.bind(this, view);
        if (challenge != null) {
            image.setImageURI(challenge.getImageURL());
            title.setText(challenge.getName());
        }
        if (TextUtils.isEmpty(title.getText())) {
            title.setVisibility(View.GONE);
        }
        return view;
    }

    public void setOnOptionChangeListener(OnOptionChangeListener listener) {
        this.optionChangeListener = listener;
    }
}
