package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.QuickAnswerViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/02/01.
 */

public class QuickAnswerAdapter extends RecyclerView.Adapter<QuickAnswerViewHolder> {

    private Context context;
    private OnBotInputListener listener;
    private List<Answer> answers = new ArrayList<>();

    public QuickAnswerAdapter(Context context, OnBotInputListener listener) {
        this.context = context;
        this.listener = listener;
    }

    //////////////////////////
    // RecyclerView.Adapter //
    //////////////////////////

    @Override
    public int getItemCount() {
        return answers.size();
    }

    @Override
    public QuickAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_bot_quick_answer, parent, false);
        return new QuickAnswerViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(QuickAnswerViewHolder holder, int position) {
        holder.populate(answers.get(position), listener);
    }

    //////////////////////////
    // Container Management //
    //////////////////////////

    public Answer getAnswer(int position) {
        return answers.get(position);
    }

    public void clear() {
        answers.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<Answer> answers) {
        this.answers.addAll(answers);
        notifyDataSetChanged();
    }

    public void replace(Collection<Answer> answers) {
        this.answers.clear();
        this.answers.addAll(answers);
        notifyDataSetChanged();
    }

    /////////////////
    // Input event //
    /////////////////

    public interface OnBotInputListener {

        /**
         * Fired when the user clicks on a quick answer, or inputs text into an inline answer.
         *
         * @param answer The answer containing the input value.
         */
        void onAnswer(Answer answer);
    }
}
