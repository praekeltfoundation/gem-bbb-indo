package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class GoalListSummaryViewHolder extends BaseBotViewHolder<Node> {


    @BindView(R.id.item_view_bot_text)
    TextView textView;

    private BotAdapter botAdapter;

    public GoalListSummaryViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_bot_dialogue_bkg));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        Object[] values = dataModel.values.getStringArray(BotParamType.USER_GOALS.getKey());
        textView.setText(TextUtils.join("\n", values));
    }

    @Override
    protected void populateModel() {
        List<Goal> goals = (List<Goal>) botAdapter.getController().getObject(BotObjectType.GOALS);
        String[] values = new String[goals.size()];
        for (int i = 0; i < goals.size(); i++)
            values[i] = String.format("%s – %s", goals.get(i).getName(), goals.get(i).getValueFormatted());
        dataModel.values.put(BotParamType.USER_GOALS.getKey(), values);
    }
}
