package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    Double weeklyTarget;      //weeklySaveAmount
    double target;      //goalAmount
    String goalName;
    double goalWeeks;

    public GoalVerificationViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);

        textView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_d_bot_dialogue_bkg));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        String text = dataModel.getText(getContext());

        for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
            switch (baseBotModel.getName()) {
                case "goalDate":
                    try {
//                        String fmDate = ((Answer) baseBotModel).getValue().split("-", 2)[0];
                        String fmDate = ((Answer) baseBotModel).getValue().substring(0, 10);
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        goalDate = formatter.parse(fmDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "priorSaveAmount":
                    current = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "weeklySaveAmount":
                    weeklyTarget = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goal_amount":
                    target = ((Answer) baseBotModel).getValue() == null ? 0 : Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goalName":
                    goalName = ((Answer) baseBotModel).getValue();
                    break;
                case "askGoalName":
                    if (baseBotModel instanceof Answer) {
                        goalName = ((Answer) baseBotModel).get("name");
                    }
                    break;
            }
        }

        double amountLeft = target - current;
        amountLeft = amountLeft < 0 ? 0 : amountLeft;
        if (goalDate != null) {

            goalWeeks = Utils.weekDiff(goalDate.getTime(), Utils.ROUNDWEEK.UP);
            weeklyTarget = amountLeft / goalWeeks;
        } else if (weeklyTarget != null) {
            goalWeeks = Math.round(amountLeft / weeklyTarget);
        }

        Object[] params = new String[]{
                String.valueOf((int) Math.ceil(weeklyTarget)),
                goalName,
                String.valueOf((int) goalWeeks),
                CurrencyHelper.getCurrencySymbol()
        };
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
