package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerTextCurrencyViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_answer_text)
    TextView textView;

    BotAdapter botAdapter;

    public AnswerTextCurrencyViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        this.dataModel = model;
        textView.setText("Rp " + dataModel.getText(getContext()));
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
