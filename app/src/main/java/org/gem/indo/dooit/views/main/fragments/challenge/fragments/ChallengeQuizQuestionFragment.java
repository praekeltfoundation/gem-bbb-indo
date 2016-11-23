package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.adapters.ChallengeQuizOptionsListAdapter;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import org.gem.indo.dooit.views.main.fragments.challenge.viewholders.QuizOptionViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChallengeQuizQuestionFragment extends Fragment implements OnOptionChangeListener {
    private static final String TAG = "QuizQuestionFragment";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_OPTION_ID = "option_id";
    private static final String ARG_COMPLETED = "completed";

    private QuizChallengeQuestion mQuestion = null;
    private long optionId = -1;
    private boolean completed = false;
    private OnOptionChangeListener optionChangeListener = null;
    private ChallengeQuizOptionsListAdapter adapter = null;
    private ChallengeQuizFragment controller = null;
    private Unbinder unbinder;

    @BindView(org.gem.indo.dooit.R.id.fragment_challengequizquestion_title)
    TextView title;
    @BindView(org.gem.indo.dooit.R.id.option_recycler_view)
    RecyclerView optionList;

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

    public static ChallengeQuizQuestionFragment newInstance(QuizChallengeQuestion question) {
        return newInstance(question, -1);
    }

    public static ChallengeQuizQuestionFragment newInstance(QuizChallengeQuestion question, long optionId) {
        return ChallengeQuizQuestionFragment.newInstance(question, optionId, false);
    }

    public static ChallengeQuizQuestionFragment newInstance(QuizChallengeQuestion question, long optionId, boolean completed) {
        ChallengeQuizQuestionFragment fragment = new ChallengeQuizQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QUESTION, question);
        args.putLong(ARG_OPTION_ID, optionId);
        args.putBoolean(ARG_COMPLETED, completed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuestion = getArguments().getParcelable(ARG_QUESTION);
            optionId = getArguments().getLong(ARG_OPTION_ID);
            completed = getArguments().getBoolean(ARG_COMPLETED);
        }

        Fragment challengeFragment = getActivity().getSupportFragmentManager().findFragmentByTag("fragment_challenge");
        if (challengeFragment instanceof ChallengeQuizFragment) {
            controller = ((ChallengeQuizFragment) challengeFragment);
            controller.addOptionChangeListener(this);
//            controller.addQuestionCompletedListener(this);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challengequizquestion, container, false);
        unbinder = ButterKnife.bind(this, view);
        title.setText(mQuestion.getText());

        adapter = new ChallengeQuizOptionsListAdapter(controller, mQuestion, optionId);
        if (controller != null) {
            controller.addQuestionCompletedListener(adapter);
        }

        optionList.setAdapter(adapter);
        optionList.setLayoutManager(new LinearLayoutManager(getContext()));
        selectItem(optionId);
        optionList.setEnabled(completed);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (controller != null) {
            controller.removeQuestionCompletedListener(adapter);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (controller != null) {
            controller.removeOptionChangeListener(this);
        }
        super.onDestroy();
    }

    public void selectItem(long id) {
        if (optionList != null) {
            RecyclerView.ViewHolder old = optionList.findViewHolderForItemId(optionId);
            if (old != null && old instanceof QuizOptionViewHolder) {
                ((QuizOptionViewHolder) old).setSelected(false);
            }

            RecyclerView.ViewHolder holder = optionList.findViewHolderForItemId(id);
            if (holder != null && holder instanceof QuizOptionViewHolder) {
                ((QuizOptionViewHolder) holder).setSelected(true);
            }
        }

        if (adapter != null) {
            adapter.setSelectedId(id);
        }

        optionId = id;
    }

    public void setOnOptionChangeListener(OnOptionChangeListener listener) {
        this.optionChangeListener = listener;
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
        if (this.optionChangeListener != null) {
            optionChangeListener.onOptionChange(question, option);
        }

        selectItem(option.getId());
    }
}
