package com.nike.dooit.views.main.fragments.challenge.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeNoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeNoneFragment extends Fragment {
    public ChallengeNoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChallengeNoneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeNoneFragment newInstance() {
        ChallengeNoneFragment fragment = new ChallengeNoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_none, container, false);
        return view;
    }

}
