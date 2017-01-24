package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
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

        textView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_d_answer_dialogue_bkg));
    }

    @Override
    public void populate(Answer model) {
        super.populate(model);
        textView.setText(CurrencyHelper.format(dataModel.getValue()));
        CrashlyticsHelper.log(this.getClass().getSimpleName(),"populate (TextCurrencyEdit): ", "Amount: " + dataModel.getValue());
    }

    @Override
    protected void populateModel() {

    }
}
