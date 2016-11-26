package org.gem.indo.dooit.views.helpers.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.gem.indo.dooit.views.DooitActivity;

/**
 * Created by wsche on 2016/11/05.
 */

public abstract class DooitActivityBuilder<T extends DooitActivityBuilder> {
    protected Intent intent;
    protected Context context;

    protected DooitActivityBuilder(Context context) {
        this.context = context;
        intent = createIntent(context);
    }

    private Activity getActivity() {
        return (Activity) context;
    }

    protected abstract Intent createIntent(Context context);

    public T addBundle(Bundle bundle) {
        intent.getExtras().putAll(bundle);
        return (T) this;
    }

    public T setFlags(int flags) {
        intent.setFlags(flags);
        return (T) this;
    }

    public T addFlags(int flags) {
        intent.addFlags(flags);
        return (T) this;
    }

    public Intent build() {
        return intent;
    }

    protected boolean checkIntentCompleteness() {
        return true;
    }

    public void startActivityClearTop() throws IncompleteBuilderException {
        if (checkIntentCompleteness()) {
            if (context instanceof DooitActivity)
                ((DooitActivity) context).dismissDialog();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else
            throw new IncompleteBuilderException("Not all required intent values provided");

        ((Activity) context).finish();
    }

    public void startActivity() throws IncompleteBuilderException {
        if (checkIntentCompleteness()) {
            if (context instanceof DooitActivity)
                ((DooitActivity) context).dismissDialog();
            context.startActivity(intent);
        } else
            throw new IncompleteBuilderException("Not all required intent values provided");
    }

    public void startActivity(String uri) throws IncompleteBuilderException {
        if (checkIntentCompleteness()) {
            intent.setData(Uri.parse(uri));
            startActivity();
        } else
            throw new IncompleteBuilderException("Not all required intent values provided");
    }

    public void startActivityForResult(/*@RequestCodes.Requests*/ int requestCode) throws IncompleteBuilderException {
        if (checkIntentCompleteness())
            getActivity().startActivityForResult(intent, requestCode);
        else
            throw new IncompleteBuilderException("Not all required intent values provided");
    }


}
