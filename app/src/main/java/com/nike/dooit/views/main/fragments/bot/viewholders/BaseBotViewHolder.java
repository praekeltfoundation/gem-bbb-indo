package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nike.dooit.models.bot.BaseBotModel;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public abstract class BaseBotViewHolder<T extends BaseBotModel> extends RecyclerView.ViewHolder {
    T dataModel;

    public BaseBotViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void populate(T model);

    public Context getContext() {
        return itemView.getContext();
    }

    public T getModel() {
        return (T) dataModel;
    }

    protected void showKeyboard(final View view) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//                ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        }, 100);
    }

    protected void dismissKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
