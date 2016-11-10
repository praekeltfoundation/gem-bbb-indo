package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizOptionsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChallengeQuizQuestionFragment extends Fragment {
    private static final String ARG_QUESTION = "question";

    private Question mQuestion = null;

    @BindView(R.id.fragment_challengequizquestion_title) TextView title;
    @BindView(R.id.option_recycler_view) RecyclerView optionList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengeQuizQuestionFragment() {
        // Required empty constructor
    }

    @SuppressWarnings("unused") public static ChallengeQuizQuestionFragment newInstance() {
        ChallengeQuizQuestionFragment fragment = new ChallengeQuizQuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ChallengeQuizQuestionFragment newInstance(Question question) {
        ChallengeQuizQuestionFragment fragment = new ChallengeQuizQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuestion = getArguments().getParcelable(ARG_QUESTION);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challengequizquestion, container, false);
        ButterKnife.bind(this, view);
        title.setText(mQuestion.getText());
        RecyclerView.Adapter adapter = new ChallengeQuizOptionsListAdapter(mQuestion, optionList);
        optionList.setAdapter(adapter);
        optionList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}
