package com.nike.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;
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
        return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_answer, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseBotViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    public BaseBotModel getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public synchronized void addItem(BaseBotModel item) {
        int pos = dataSet.size();
        dataSet.add(pos, item);
        notifyItemInserted(pos);
    }

    public synchronized void addItems(List<BaseBotModel> items) {
        for (BaseBotModel item : items) {
            int pos = dataSet.size();
            dataSet.add(pos, item);
            notifyItemInserted(pos);
        }
    }

    public ArrayList<BaseBotModel> getDataSet() {
        return dataSet;
    }
}
