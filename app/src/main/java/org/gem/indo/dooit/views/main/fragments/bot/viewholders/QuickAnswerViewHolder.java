package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.gem.indo.dooit.models.bot.Answer;

import butterknife.ButterKnife;

/**
 * The ViewHolder for the multiple choice, quick answers at the bottom of the Bot screen.
 *
 * Created by Wimpie Victor on 2017/02/01.
 */

public class QuickAnswerViewHolder extends RecyclerView.ViewHolder {



    public QuickAnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Answer answer) {
        reset();


    }

    private void reset() {

    }
}
