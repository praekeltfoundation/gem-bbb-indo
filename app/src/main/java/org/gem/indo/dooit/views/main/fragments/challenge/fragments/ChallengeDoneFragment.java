package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.challenge.BaseChallenge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeDoneFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge;

    @BindView(R.id.challenge_done_card)
    CardView doneCard;

    @BindView(R.id.challenge_done_button)
    Button doneButton;

    Unbinder unbinder = null;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_done, container, false);
        unbinder = ButterKnife.bind(this, view);
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
}
