package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.GoalGalleryAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Gallery of predefined Goals to use as prototypes for Goal creation.
 * 
 * Created by Wimpie Victor on 2016/11/24.
 */

public class AnswerGoalGalleryViewHolder extends BaseBotViewHolder<Answer> {

    @BindView(R.id.item_view_bot_carousel_recycler_view)
    RecyclerView recyclerView;

    private GoalGalleryAdapter galleryAdapter;
    private BotAdapter botAdapter;
    private HashtagView.TagsClickListener tagsClickListener;

    public AnswerGoalGalleryViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        galleryAdapter = new GoalGalleryAdapter();
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void populate(Answer model) {

    }
}
