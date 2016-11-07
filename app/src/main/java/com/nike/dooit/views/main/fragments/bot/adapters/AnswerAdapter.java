package com.nike.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.views.main.fragments.bot.viewholders.AnswerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerViewHolder> {
    Context context;
    ArrayList<Answer> dataSet = new ArrayList<>();

    public AnswerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_answer, parent, false));
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    public Answer getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public synchronized void addItem(Answer item) {
        int pos = dataSet.size();
        dataSet.add(pos, item);
        notifyItemInserted(pos);
    }

    public synchronized void setItems(List<Answer> items) {
        dataSet.clear();
        notifyItemRangeRemoved(0, dataSet.size());
        for (Answer item : items) {
            int pos = dataSet.size();
            dataSet.add(pos, item);
            notifyItemInserted(pos);
        }
    }

    public interface AnswerSelectedListener {
        void onClick(View view, Answer answer);
    }
}
