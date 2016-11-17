package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionSelectedListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import com.nike.dooit.views.main.fragments.challenge.viewholders.QuizOptionViewHolder;

import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-11-09.
 */

public class ChallengeQuizOptionsListAdapter extends RecyclerView.Adapter<QuizOptionViewHolder> implements OnOptionSelectedListener {
    private static final String TAG = "QuizOptionsAdapter";
    private QuizChallengeQuestion mQuestion;
    private List<QuizChallengeOption> mOptionList;
    private long selectedId;
    private OnOptionChangeListener optionChangeListener = null;

    public ChallengeQuizOptionsListAdapter(QuizChallengeQuestion question) {
        mQuestion = question;
        if (question != null) {
            mOptionList = question.getOptions();
        }
        selectedId = -1;
        optionChangeListener = null;
        setHasStableIds(true);
    }

    public ChallengeQuizOptionsListAdapter(QuizChallengeQuestion question, long optionId) {
        mQuestion = question;
        if (question != null) {
            mOptionList = question.getOptions();
        }
        selectedId = optionId;
        optionChangeListener = null;
        setHasStableIds(true);
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
    }

    public void setSelectedId(long id) {
        selectedId = (id < 0) ? -1 : id;
    }

    public void setOptionChangeListener(OnOptionChangeListener listener) {
        optionChangeListener = listener;
    }

    @Override
    public void onOptionSelected(QuizChallengeOption option) {
        if (optionChangeListener != null) {
            optionChangeListener.onOptionChange(mQuestion, option);
        }
    }
}
