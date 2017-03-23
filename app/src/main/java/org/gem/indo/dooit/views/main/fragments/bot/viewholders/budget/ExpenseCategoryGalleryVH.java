package org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.budget.ExpenseCategoryGalleryAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.BaseBotViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2017/03/14.
 */

public class ExpenseCategoryGalleryVH extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_carousel_recycler_view)
    RecyclerView recycler;

    private BotAdapter botAdapter;

    public ExpenseCategoryGalleryVH(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        Context context = itemView.getContext();
        if (context == null)
            return;

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(manager);
    }

    ///////////////////////
    // BaseBotViewHolder //
    ///////////////////////

    @Override
    public void populate(Node model) {
        super.populate(model);

        if (!botAdapter.hasController())
            return;

        BotType botType = botAdapter.getController().getBotType();
        ExpenseCategoryGalleryAdapter adapter = new ExpenseCategoryGalleryAdapter(botType);
        recycler.setAdapter(adapter);
    }

    @Override
    public void reset() {
        super.reset();
    }


    @Override
    protected void populateModel() {
        // TODO: Load
    }
}
