package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionSelectedListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnQuestionCompletedListener;
import com.nike.dooit.views.main.fragments.challenge.viewholders.QuizOptionViewHolder;

import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-11-09.
 */

public class ChallengeQuizOptionsListAdapter extends RecyclerView.Adapter<QuizOptionViewHolder> implements OnOptionChangeListener, OnOptionSelectedListener, OnQuestionCompletedListener {
    private static final String TAG = "QuizOptionsAdapter";
    private QuizChallengeQuestion mQuestion;
    private List<QuizChallengeOption> mOptionList;
    private long selectedId;
    private boolean completed = false;
    private ChallengeQuizFragment controller = null;

    public ChallengeQuizOptionsListAdapter(ChallengeQuizFragment challengeFragment, QuizChallengeQuestion question) {
        this(challengeFragment, question, -1);
    }

    public ChallengeQuizOptionsListAdapter(ChallengeQuizFragment challengeFragment, QuizChallengeQuestion question, long optionId) {
        controller = challengeFragment;
        mQuestion = question;
        selectedId = optionId;
        setHasStableIds(true);

        mOptionList = question != null ? question.getOptions() : null;
        completed = question != null && controller != null && controller.isCompleted(question.getId());
    }

    @Override public int getItemCount() {
        return mOptionList != null ? mOptionList.size() : 0;
    }

    @Override public long getItemId(int position) {
        return mOptionList.get(position).getId();
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }

    @Override public QuizOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challengequizquestion_option, parent, false);
        return new QuizOptionViewHolder(view, this);
    }

    @Override public void onBindViewHolder(final QuizOptionViewHolder holder, int position) {
        final QuizChallengeOption item = mOptionList.get(position);
        holder.populate(item);
        holder.setSelected(item.getId() == selectedId);
        holder.setEnabled(!completed);
    }

    public void setSelectedId(long id) {
        selectedId = (id < 0) ? -1 : id;
    }

    @Override
    public void onOptionSelected(QuizChallengeOption option) {
        if (controller != null) {
            controller.onOptionChange(mQuestion, option);
        }
    }

    @Override
    public void onQuestionCompleted(long id) {
        if (id == mQuestion.getId()) {
            completed = true;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
        return;
    }
}
