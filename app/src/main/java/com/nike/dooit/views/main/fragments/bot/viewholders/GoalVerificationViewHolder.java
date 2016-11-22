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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class GoalVerificationViewHolder extends BaseBotViewHolder<Node> {
    @BindView(R.id.item_view_bot_text)
    TextView textView;
    @BindView(R.id.item_view_bot_icon)
    ImageView botIcon;
    @Inject
    Persisted persisted;

    BotAdapter botAdapter;
    Date goalDate;        //goalDate
    double current;     //priorSaveAmount
    Double weekly;      //weeklySaveAmount
    double target;      //goalAmount
    String goalName;
    double goalWeeks;

    public GoalVerificationViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Node model) {
        this.dataModel = model;

        String text = dataModel.getText(getContext());

        for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
            switch (baseBotModel.getName()) {
                case "goalDate":
                    try {
//                        String fmDate = ((Answer) baseBotModel).getValue().split("-", 2)[0];
                        String fmDate = ((Answer) baseBotModel).getValue().substring(0, 10);
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        goalDate = formatter.parse(fmDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "priorSaveAmount":
                    current = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "weeklySaveAmount":
                    weekly = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goalAmount":
                    target = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goalName":
                    goalName = ((Answer) baseBotModel).getValue();
                    break;
            }
        }

        double amountLeft = target - current;
        amountLeft = amountLeft < 0 ? 0 : amountLeft;
        if (goalDate != null) {
            goalWeeks = (int) TimeUnit.MILLISECONDS.toDays(goalDate.getTime() - System.currentTimeMillis()) / 7;
            weekly = amountLeft / goalWeeks;
        } else if (weekly != null) {
            goalWeeks = amountLeft / weekly;
        }

        String[] params = new String[3];
        params[0] = String.valueOf(weekly);
        params[1] = goalName;
        params[2] = String.valueOf((int) goalWeeks);
        textView.setText(String.format(text, params));
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
