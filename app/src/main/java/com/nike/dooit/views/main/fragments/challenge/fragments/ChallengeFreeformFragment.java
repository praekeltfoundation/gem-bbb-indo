package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.challenge.FreeformChallenge;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeFreeformFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFreeformFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private FreeformChallenge challenge;

    @BindView(R.id.fragment_challenge_freeform_title) TextView title;
    @BindView(R.id.fragment_challenge_freeform_submission) EditText submissionBox;
    @BindView(R.id.fragment_challenge_freeform_submitbutton) Button submitButton;

    public ChallengeFreeformFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param challenge Freeform type challenge.
     * @return A new instance of fragment ChallengeFreeformFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeFreeformFragment newInstance(FreeformChallenge challenge) {
        ChallengeFreeformFragment fragment = new ChallengeFreeformFragment();
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
        View view = inflater.inflate(R.layout.fragment_challenge_freeform, container, false);
        ButterKnife.bind(this, view);
        title.setText(challenge.getName());
        return view;
    }
}
