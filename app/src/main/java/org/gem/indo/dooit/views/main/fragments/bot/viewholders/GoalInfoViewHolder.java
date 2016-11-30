package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.custom.ArcProgressBar;
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

public class GoalInfoViewHolder extends BaseBotViewHolder<Node> {
    @BindView(R.id.item_view_bot_goal_info_current)
    TextView currentTextView;
    @BindView(R.id.item_view_bot_goal_info_title)
    TextView titleTextView;
    @BindView(R.id.item_view_bot_goal_info_total)
    TextView totalTextView;
    @BindView(R.id.progress_image)
    SimpleDraweeView image;
    @BindView(R.id.progress_arc)
    ArcProgressBar arcProgressBar;
    @Inject
    Persisted persisted;

    BotAdapter botAdapter;
    Date goalDate;        //goalDate
    double current;     //priorSaveAmount
    Double weeklyTarget;      //weeklySaveAmount
    double target;      //goalAmount
    String goalName;
    String goalImage;

    public GoalInfoViewHolder(View itemView, BotAdapter botAdapter) {
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
                        goalImage = ((Answer) baseBotModel).getValue();
                    }
                    break;
                case "Capture":
                    goalImage = ((Answer) baseBotModel).getValue();
                    break;
                case "Gallery":
                    goalImage = ((Answer) baseBotModel).getValue();
                    break;
            }
        }

        titleTextView.setText(goalName);
        arcProgressBar.setProgress((int) current);
        arcProgressBar.setMax((int) target);
        image.setImageURI(goalImage);
        currentTextView.setText(String.format("%s %.2f", CurrencyHelper.getCurrencySymbol(), current));
        totalTextView.setText(getContext().getString(R.string.of_target_amount, CurrencyHelper.getCurrencySymbol(), target));
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
