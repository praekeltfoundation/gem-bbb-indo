package com.nike.dooit.views.main.fragments.challenge.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nike.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeNoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeNoneFragment extends Fragment {
    public static final String ARG_MESSAGE = "message";

    private String message = null;
    private Unbinder unbinder = null;

    @BindView(R.id.fragment_challenge_none_text)
    TextView messageText;

    public ChallengeNoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChallengeNoneFragment.
     */
    public static ChallengeNoneFragment newInstance() {
        ChallengeNoneFragment fragment = new ChallengeNoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ChallengeNoneFragment newInstance(String message) {
        ChallengeNoneFragment fragment = new ChallengeNoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_none, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (message != null) {
            messageText.setText(message);
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
}