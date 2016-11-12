package com.nike.dooit.views.main.fragments.challenge.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.nike.dooit.R;
import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizOptionsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rudolph Jacobs on 2016-11-10.
 */

public class QuizOptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private QuizChallengeOption option;
    private ChallengeQuizOptionsListAdapter adapter;

    @BindView(R.id.option_text) RadioButton radio;
    @BindView(R.id.option_background) LinearLayout optionBackground;

    public QuizOptionViewHolder(View itemView, ChallengeQuizOptionsListAdapter adapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        this.adapter = adapter;
    }

    public void populate(final QuizChallengeOption item) {
        option = item;
        radio.setText(option.getText());
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @Override public void onClick(View view) {
        adapter.optionSelectedListener.onSelected(getAdapterPosition());
    }

    @Override public String toString() {
            return super.toString() + " '" + option.getText() + "'";
        }

    public void setSelected(boolean selected) {
        itemView.setSelected(selected);
        radio.setChecked(selected);
    }
}
