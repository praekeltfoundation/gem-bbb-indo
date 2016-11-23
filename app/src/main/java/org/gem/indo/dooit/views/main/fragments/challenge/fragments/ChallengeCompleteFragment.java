package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

/**
 * Created by Rudolph Jacobs on 2016-11-16.
 */

public class ChallengeCompleteFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge = null;
    private OnOptionChangeListener optionChangeListener = null;

//    @BindView(R.id.fragment_challengequizquestion_title) TextView title;
//    @BindView(R.id.option_recycler_view) RecyclerView optionList;

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

    public static ChallengeCompleteFragment newInstance(BaseChallenge challenge) {
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
//        ButterKnife.bind(this, view);
        return view;
    }

    public void setOnOptionChangeListener(OnOptionChangeListener listener) {
        this.optionChangeListener = listener;
    }
}
