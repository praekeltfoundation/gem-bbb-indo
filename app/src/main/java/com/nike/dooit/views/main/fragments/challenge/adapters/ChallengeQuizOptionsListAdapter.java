package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Challenge;
import com.nike.dooit.models.Option;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;

import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-11-09.
 */

public class ChallengeQuizOptionsListAdapter extends RecyclerView.Adapter<ChallengeQuizOptionsListAdapter.ViewHolder> {
    private Question mQuestion;
    private List<Option> mOptionList;

    private final ChallengeQuizQuestionFragment.OnListFragmentInteractionListener mListener;


    public ChallengeQuizOptionsListAdapter(Question question, ChallengeQuizQuestionFragment.OnListFragmentInteractionListener listener) {
        mQuestion = question;
        if (question != null) {
            mOptionList = question.getOptions();
        }
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mOptionList != null ? mOptionList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mOptionList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challengequizquestion_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mOption = mOptionList.get(position);
        holder.mOptionText.setText(holder.mOption.getText());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mOption);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RadioButton mOptionText;
        public Option mOption;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOptionText = (RadioButton) view.findViewById(R.id.option_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOption.getText() + "'";
        }
    }
}
