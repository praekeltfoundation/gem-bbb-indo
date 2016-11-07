package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.R;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_answer_text)
    TextView textView;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(BaseBotModel model) {
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
