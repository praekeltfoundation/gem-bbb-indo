package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class MultiLineTextViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_text)
    TextView textView;
    @BindView(R.id.item_view_bot_text_2)
    TextView textView2;
    @Inject
    Persisted persisted;

    public MultiLineTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
    }

    @Override
    public void populate(BaseBotModel model) {
        this.dataModel = model;
        String[] data = dataModel.getText(getContext()).split(";", 2);
        String[] params = dataModel.getTextParams();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if ("USERNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getUsername();
            } else if ("FIRSTNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getFirstName();
            } else if ("LASTNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getLastName();
            }
        }
        if (data[0].contains("%s"))
            textView.setText(String.format(data[0], params));
        else
            textView.setText(data[0]);
        if (data[1].contains("%s"))
            textView2.setText(String.format(data[1], params));
        else
            textView2.setText(data[1]);
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
