package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nike.dooit.models.bot.BaseBotModel;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public abstract class BaseBotViewHolder<T extends BaseBotModel> extends RecyclerView.ViewHolder {
    BaseBotModel dataModel;

    public BaseBotViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void populate(BaseBotModel model);

    public Context getContext() {
        return itemView.getContext();
    }

    public T getModel() {
        return (T) dataModel;
    }
}
