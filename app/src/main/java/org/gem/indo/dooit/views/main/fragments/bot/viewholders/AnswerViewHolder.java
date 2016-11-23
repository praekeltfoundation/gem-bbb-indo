package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.models.bot.Answer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(org.gem.indo.dooit.R.id.item_view_bot_answer_text)
    TextView textView;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        this.dataModel = model;
        textView.setText(dataModel.getText(getContext()));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getModel().getNext(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
