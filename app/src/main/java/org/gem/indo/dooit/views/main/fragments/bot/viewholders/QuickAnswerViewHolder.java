package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.QuickAnswerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The ViewHolder for the multiple choice, quick answers at the bottom of the Bot screen.
 * <p>
 * Created by Wimpie Victor on 2017/02/01.
 */

public class QuickAnswerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_quick_answer)
    ViewGroup containerView;

    @BindView(R.id.item_view_bot_quick_answer_text)
    TextView textView;

    private Answer answer;
    private QuickAnswerAdapter.OnBotInputListener listener;

    public QuickAnswerViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // Background set in code as a workaround to vector crashes on lower API levels. Setting
        // background on TextView because it's containing layout is enlarged to fill the GridLayout
        // cells.
//        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_d_answer_dialogue_bkg_blue));
//        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_d_bot_dialogue_bkg));
        containerView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_d_answer_dialogue_bkg_blue));
    }

    ///////////////////////
    // View holder state //
    ///////////////////////

    public void populate(Answer answer, QuickAnswerAdapter.OnBotInputListener listener) {
        reset();

        this.answer = answer;
        this.listener = listener;

        if (answer.hasProcessedText())
            textView.setText(answer.getProcessedText());
    }

    private void reset() {
        answer = null;
        textView.setText(null);
        listener = null;
    }

    /////////////////
    // Input Event //
    /////////////////

    @OnClick(R.id.item_view_bot_quick_answer)
    void onBackgroundClick(View view) {
        // Both background and text is clickable for when the text does not fill the rectangle
        // completely
        notifyInput(answer);
    }

    @OnClick(R.id.item_view_bot_quick_answer_text)
    void OnTextClick(View view) {
        notifyInput(answer);
    }

    public void setInputListener(QuickAnswerAdapter.OnBotInputListener listener) {
        this.listener = listener;
    }

    private void notifyInput(Answer answer) {
        if (listener != null && answer != null)
            listener.onAnswer(answer);
    }
}
