package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.GoalGalleryAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Gallery of predefined Goals to use as prototypes for Goal creation.
 * <p>
 * Created by Wimpie Victor on 2016/11/24.
 */

public class AnswerGoalGalleryViewHolder extends BaseBotViewHolder<Answer> {

    private static final String TAG = AnswerGoalGalleryViewHolder.class.getName();

    @BindView(R.id.item_view_bot_carousel_recycler_view)
    RecyclerView recyclerView;

    @Inject
    Persisted persisted;

    @Inject
    GoalManager goalManager;

    private Handler handler;
    private GoalGalleryAdapter galleryAdapter;
    private BotAdapter botAdapter;
    private HashtagView.TagsClickListener listener;

    public AnswerGoalGalleryViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.handler = new Handler(Looper.getMainLooper());
        this.botAdapter = botAdapter;
        this.listener = tagsClickListener;

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        galleryAdapter = new GoalGalleryAdapter(persisted.loadGoalProtos(), listener);
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void populate(Answer model) {
        dataModel = model;
        galleryAdapter.setDataModel(model);
//        galleryAdapter.notifyDataSetChanged();
    }
}