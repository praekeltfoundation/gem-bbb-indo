package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestionState;
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
    @BindView(R.id.fragment_challengequizquestion_title)
    TextView title;
    @BindView(R.id.option_recycler_view)
    RecyclerView optionList;
    private QuizChallengeQuestion mQuestion = null;
    private long optionId = -1;
    private boolean completed = false;
    private OnOptionChangeListener optionChangeListener = null;
    private ChallengeQuizOptionsListAdapter adapter = null;
    private ChallengeQuizFragment controller = null;
    private Unbinder unbinder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengeQuizQuestionFragment() {
        // Required empty constructor
    }

    @SuppressWarnings("unused")
    public static ChallengeQuizQuestionFragment newInstance() {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuestion = getArguments().getParcelable(ARG_QUESTION);
            optionId = getArguments().getLong(ARG_OPTION_ID);
            completed = getArguments().getBoolean(ARG_COMPLETED);
        }

        Fragment challengeFragment = getParentFragment();
        if (challengeFragment instanceof ChallengeQuizFragment) {
            controller = ((ChallengeQuizFragment) challengeFragment);
            controller.addOptionChangeListener(this);
            if (mQuestion != null) {
                QuizChallengeQuestionState state = controller.getQuestionState(mQuestion.getId());
                if (state != null) {
                    optionId = state.optionId;
                    completed = state.completed;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challengequizquestion, container, false);
        unbinder = ButterKnife.bind(this, view);
        title.setText(mQuestion.getText());

        if (adapter == null) {
            adapter = new ChallengeQuizOptionsListAdapter(controller, mQuestion, optionId);

            if (controller != null) {
                controller.addQuestionCompletedListener(adapter);
            }

            optionList.setAdapter(adapter);
            optionList.setLayoutManager(new LinearLayoutManager(getContext()));
            optionList.setEnabled(!completed);
            optionList.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    final int gapHeight = 8;
                    int pos = parent.getChildAdapterPosition(view);
                    if (pos == 0) {
                        int totalHeight = parent.getHeight();
                        RecyclerView.LayoutManager layMan = parent.getLayoutManager();
                        int numVisible = parent.getLayoutManager().getChildCount();
                        int visibleChildHeight = gapHeight * (numVisible - 1);
                        for (int i = 0; i < numVisible; i++) {
                            View child = layMan.getChildAt(i);
                            child.measure(
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            visibleChildHeight += child.getMeasuredHeight();
                        }
                        int offset = gapHeight;
                        if (visibleChildHeight < totalHeight) {
                            offset = (totalHeight - visibleChildHeight) / 2 - gapHeight;
                        }
                        outRect.set(0, offset, 0, gapHeight);
                    } else {
                        outRect.set(0, gapHeight, 0, gapHeight);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        selectItem(optionId);
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
        if (adapter != null) {
            adapter.setSelectedId(id);
        }

        long oldId = optionId;
        optionId = id;

        if (optionList != null) {
            RecyclerView.ViewHolder old = optionList.findViewHolderForItemId(oldId);
            if (old instanceof QuizOptionViewHolder) {
                ((QuizOptionViewHolder) old).setSelected(false);
            }

            RecyclerView.ViewHolder holder = optionList.findViewHolderForItemId(id);
            if (holder instanceof QuizOptionViewHolder) {
                ((QuizOptionViewHolder) holder).setSelected(true);
            }
        }
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
