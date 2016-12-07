package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.views.DooitActivity;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public abstract class BaseBotViewHolder<T extends BaseBotModel> extends RecyclerView.ViewHolder {
    T dataModel;

    public BaseBotViewHolder(View itemView) {
        super(itemView);
    }


    @CallSuper
    public void populate(T model) {
        dataModel = model;
        reset();
        if (!dataModel.isImmutable()) {
            populateModel();
            dataModel.finish();
        }
    }

    /**
     * Called when the bot model is mutable, used to process model with data from controller.
     */
    protected abstract void populateModel();

    public void reset() {}

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


    public Uri getRealPathFromURI(Uri contentUri) {
        try {
            Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri;
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return Uri.parse(cursor.getString(index));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return contentUri;
    }


    public ProgressDialog showProgressDialog(@StringRes int messageRes) {
        return ((DooitActivity) getContext()).showProgressDialog(messageRes);
    }

    public void dismissDialog() {
        ((DooitActivity) getContext()).dismissDialog();
    }

    /**
     * Runs the specified action on the UI thread. If the goalValue thread is the UI
     * thread, then the action is executed immediately. If the goalValue thread is
     * not the UI thread, the action is posted to the event queue of the UI thread.
     *
     * @param action the action to run on the UI thread
     */
    public final void runOnUiThread(Runnable action) {
        ((Activity) getContext()).runOnUiThread(action);
    }
}
