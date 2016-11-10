package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.Option;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.viewholders.QuizOptionViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Rudolph Jacobs on 2016-11-09.
 */

public class ChallengeQuizOptionsListAdapter extends RecyclerView.Adapter<QuizOptionViewHolder> {
    private Question mQuestion;
    private List<Option> mOptionList;

    @BindView(R.id.option_recycler_view) RecyclerView recycler;

    public ChallengeQuizOptionsListAdapter(Question question) {
        mQuestion = question;
        if (question != null) {
            mOptionList = question.getOptions();
        }
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
        return new QuizOptionViewHolder(view);
    }

    @Override public void onBindViewHolder(final QuizOptionViewHolder holder, int position) {
        final Option item = mOptionList.get(position);
        holder.populate(item);
    }
}
