package com.nike.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.enums.BotMessageType;
import com.nike.dooit.views.main.fragments.bot.viewholders.AnswerViewHolder;
import com.nike.dooit.views.main.fragments.bot.viewholders.BaseBotViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class BotAdapter extends RecyclerView.Adapter<BaseBotViewHolder> {
    Context context;
    ArrayList<BaseBotModel> dataSet = new ArrayList<>();

    public BotAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BaseBotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (BotMessageType.getValueOf(viewType)) {
            case TEXT:
            case MULTILINETEXT:
            case GOALSELECTION:
            case ANSWER:
                return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_bot_answer, parent, false));
            case UNDEFINED:
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseBotViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        BaseBotModel model = getItem(position);
        return BotMessageType.getValueOf(model.getType()).getValue();
    }

    public BaseBotModel getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public synchronized void setItem(BaseBotModel item) {
        int size = dataSet.size();
        dataSet.clear();
        notifyItemRangeRemoved(0, size);

        int pos = dataSet.size();
        dataSet.add(pos, item);
        notifyItemInserted(pos);
    }

    public synchronized void addItems(List<BaseBotModel> items) {
        int size = dataSet.size();
        dataSet.clear();
        notifyItemRangeRemoved(0, size);
        for (BaseBotModel item : items) {
            int pos = dataSet.size();
            dataSet.add(pos, item);
            notifyItemInserted(pos);
        }
    }

    public ArrayList<BaseBotModel> getDataSet() {
        return dataSet;
    }

    public void removeItem(BaseBotModel model) {
        int index = dataSet.indexOf(model);
        if (index != -1) {
            dataSet.remove(model);
            notifyItemRemoved(index);
        }
    }
}
