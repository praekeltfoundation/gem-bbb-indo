package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_answer_text)
    TextView textView;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // Must assign programmatically for Support Library to wrap before API 21
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_answer_dialogue_bkg));
    }

    @Override
    public void populate(Answer model) {
        super.populate(model);
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
