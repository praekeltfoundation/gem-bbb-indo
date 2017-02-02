package org.gem.indo.dooit.views.main.fragments.bot;

import android.support.v7.widget.GridLayoutManager;

import org.gem.indo.dooit.views.main.fragments.bot.adapters.QuickAnswerAdapter;

/**
 * Created by Wimpie Victor on 2017/02/01.
 */

public class QuickAnswerSpan extends GridLayoutManager.SpanSizeLookup {

    private QuickAnswerAdapter adapter;

    public QuickAnswerSpan(QuickAnswerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getSpanSize(int position) {
        return 1;
    }
}
