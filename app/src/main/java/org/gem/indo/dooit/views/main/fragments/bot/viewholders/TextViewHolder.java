package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
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
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.Locale;

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

            switch (param) {
                case "USERNAME":
                    params[i] = persisted.getCurrentUser().getUsername();
                    break;
                case "FIRSTNAME":
                    params[i] = persisted.getCurrentUser().getFirstName();
                    break;
                case "LASTNAME":
                    params[i] = persisted.getCurrentUser().getLastName();
                    break;
                case "LOCAL_CURRENCY":
                    params[i] = CurrencyHelper.getCurrencySymbol();
                    break;
                case "GOAL_DEPOSIT_TARGET":
                    params[i] = String.format(Locale.getDefault(), "%s", (int) persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getTarget());
                    break;
                case "GOAL_DEPOSIT_WEEKLY_TARGET":
                    params[i] = String.format(Locale.getDefault(), "%s", (int) Math.ceil(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getWeeklyTarget()));
                    break;
                case "GOAL_DEPOSIT_END_DATE":
                    params[i] = Utils.formatDate(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate());
                    break;
                case "GOAL_DEPOSIT_WEEKS_LEFT":
                    params[i] = String.valueOf(Utils.weekDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime(), Utils.ROUNDWEEK.DOWN));
                    break;
                case "GOAL_DEPOSIT_DAYS_LEFT":
                    int days = Utils.dayDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime());
                    params[i] = String.valueOf(days - (Utils.weekDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime(), Utils.ROUNDWEEK.DOWN) * 7));
                    break;
                case "TIP_INTRO":
                    params[i] = persisted.loadConvoTip().getIntro();
                    break;
                default:
                    for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
                        if (baseBotModel.getClassType().equals(Answer.class.toString())
                                && param.equals(baseBotModel.getName())) {
                            params[i] = ((Answer) baseBotModel).getValue();
                        }
                    }
            }
        }

        if (text.contains("%"))
            textView.setText(String.format(text, (Object[]) params));
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
