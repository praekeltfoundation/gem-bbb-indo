package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/01/23.
 */

public class GoalUsersWithSimilarGoalsViewHolder extends BaseBotViewHolder<Node>  {
    @BindView(R.id.item_view_bot_text)
    TextView textView;

    @BindView(R.id.item_view_bot_icon)
    ImageView botIcon;

    private BotAdapter botAdapter;
    private int numUsersWithSimilarGoals;

    public GoalUsersWithSimilarGoalsViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_bot_dialogue_bkg));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

    }

    @Override
    protected void populateModel() {

    }
}
