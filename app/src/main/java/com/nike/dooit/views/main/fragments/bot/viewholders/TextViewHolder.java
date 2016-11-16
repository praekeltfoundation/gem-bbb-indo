package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.bot.Node;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class TextViewHolder extends BaseBotViewHolder<Node> {
    @BindView(R.id.item_view_bot_text)
    TextView textView;
    @BindView(R.id.item_view_bot_icon)
    ImageView botIcon;
    @Inject
    Persisted persisted;

    BotAdapter botAdapter;

    public TextViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Node model) {
        this.dataModel = model;

        String text = dataModel.getText(getContext());
        String[] params = dataModel.getTextParams();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if ("USERNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getUsername();
            } else if ("FIRSTNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getFirstName();
            } else if ("LASTNAME".equals(param)) {
                params[i] = persisted.getCurrentUser().getLastName();
            } else {
                for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
                    if (baseBotModel.getClassType().equals(Answer.class.toString())
                            && param.equals(baseBotModel.getName())) {
                        params[i] = ((Answer) baseBotModel).getValue();
                    }
                }
            }
        }
        if (text.contains("%s"))
            textView.setText(String.format(text, params));
        else
            textView.setText(text);
        RelativeLayout.LayoutParams lp = ((RelativeLayout.LayoutParams) textView.getLayoutParams());
        if (dataModel.isIconHidden()) {
            botIcon.setVisibility(View.GONE);
            lp.setMargins(lp.leftMargin, 0, lp.rightMargin, lp.bottomMargin);
        } else {
            lp.setMargins(lp.leftMargin, lp.rightMargin, lp.rightMargin, lp.bottomMargin);
        }
        textView.setLayoutParams(lp);
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
