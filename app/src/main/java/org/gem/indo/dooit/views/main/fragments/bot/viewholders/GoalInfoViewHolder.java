package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
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

    private static final String TAG = GoalInfoViewHolder.class.getName();

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

    private BotAdapter botAdapter;
    private String goalName;
    private Date goalDate;        //goalDate
    private double goalValue;     //priorSaveAmount
    private double goalTarget;      //goalAmount
    private String goalImageUrl;

    public GoalInfoViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_bot_dialogue_bkg));

        if (!botAdapter.hasCallback())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
        
        Goal goal = (Goal) botAdapter.getCallback().getObject();

        titleTextView.setText(goal.getName());
        arcProgressBar.setProgress((int) ((goal.getValue() / goal.getTarget()) * 100));
        image.setImageURI(goal.getImageUrl());
        currentTextView.setText(String.format("%s %.2f", CurrencyHelper.getCurrencySymbol(), goal.getValue()));
        totalTextView.setText(getContext().getString(R.string.of_target_amount, CurrencyHelper.getCurrencySymbol(), goal.getTarget()));
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
