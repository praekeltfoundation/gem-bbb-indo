package org.gem.indo.dooit.views.main.fragments.challenge.viewholders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.views.main.fragments.challenge.adapters.ChallengeQuizOptionsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rudolph Jacobs on 2016-11-10.
 */

public class QuizOptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.option_text)
    RadioButton radio;

    @BindView(R.id.option_background)
    AppCompatImageView optionBackground;

    private QuizChallengeOption option;
    private ChallengeQuizOptionsListAdapter adapter;

    public QuizOptionViewHolder(View itemView, ChallengeQuizOptionsListAdapter adapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        this.adapter = adapter;
        Drawable back = ContextCompat.getDrawable(getContext(), R.drawable.quiz_option_box);
        DrawableCompat.setTintList(back, ContextCompat.getColorStateList(getContext(), R.color.quiz_option_tint));
        optionBackground.setImageDrawable(back);
    }

    public void populate(final QuizChallengeOption item) {
        option = item;
        radio.setText(option.getText());
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @Override public void onClick(View view) {
        if (adapter != null) {
            adapter.onOptionSelected(option);
        }
    }

    @Override public String toString() {
            return super.toString() + " '" + option.getText() + "'";
        }

    public boolean isSelected() {
        return itemView.isSelected();
    }

    public void setSelected(boolean selected) {
        itemView.setSelected(selected);
        optionBackground.setSelected(selected);
        radio.setChecked(selected);
    }

    public boolean isEnabled() {
        return itemView.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        itemView.setEnabled(enabled);
    }
}
