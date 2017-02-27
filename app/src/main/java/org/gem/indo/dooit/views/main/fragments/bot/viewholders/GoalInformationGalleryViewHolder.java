package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/02/27.
 */

public class GoalInformationGalleryViewHolder extends BaseBotViewHolder<Node> {
    private static final String TAG = GoalInformationGalleryViewHolder.class.getName();

    @BindView(R.id.item_view_bot_carousel_recycler_view)
    RecyclerView recyclerView;

    @Inject
    GoalManager goalManager;

    private BotAdapter botAdapter;

    @Override
    protected void populateModel() {

    }

    public GoalInformationGalleryViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);

        this.botAdapter = botAdapter;
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
    }
}
