package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
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
    }

    @Override
    public void populate(Node model) {
        this.dataModel = model;

        // Iterates over the conversation up until now and picks out the values needed
        for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
            String fieldName = model.getFieldName(baseBotModel.getName());
            switch (fieldName) {
                case "goalGallery":
                    goalName = ((Answer) baseBotModel).get("name");
                    goalImageUrl = ((Answer) baseBotModel).get("image_url");
                    break;
                case "goalDate":
                    try {
                        String fmDate = ((Answer) baseBotModel).getValue().substring(0, 10);
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        goalDate = formatter.parse(fmDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "goalValue":
                    goalValue = Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goalTarget":
                    goalTarget = ((Answer) baseBotModel).getValue() == null ? 0 : Double.parseDouble(((Answer) baseBotModel).getValue());
                    break;
                case "goalName":
                    goalName = ((Answer) baseBotModel).getValue();
                    break;
                case "goalImageUrl":
                    goalImageUrl = ((Answer) baseBotModel).getValue();
                    break;
            }
        }

        titleTextView.setText(goalName);
        arcProgressBar.setProgress((int) ((goalValue / goalTarget) * 100));
        image.setImageURI(goalImageUrl);
        currentTextView.setText(String.format("%s %.2f", CurrencyHelper.getCurrencySymbol(), goalValue));
        totalTextView.setText(getContext().getString(R.string.of_target_amount, CurrencyHelper.getCurrencySymbol(), goalTarget));
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
