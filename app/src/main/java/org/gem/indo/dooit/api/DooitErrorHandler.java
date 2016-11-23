package org.gem.indo.dooit.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import org.gem.indo.dooit.api.responses.ErrorResponse;

import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

/**
 * Created by Bernhard Müller on 2016/05/18.
 */
public abstract class DooitErrorHandler implements Action1<Throwable> {
    public Context context;

    @Override
    public final void call(Throwable throwable) {
        DooitAPIError apiError = new DooitAPIError(throwable);
        try {
            if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                ErrorResponse errorResponse = new Gson().fromJson(new String(exception.response().errorBody().bytes(), "UTF-8"), ErrorResponse.class);
                apiError = new DooitAPIError(exception, errorResponse);
            }
        } catch (Throwable ex) {
            Log.w(DooitErrorHandler.class.toString(), "Unable to decode api error", ex);
        }
        onError(apiError);
    }

    public abstract void onError(DooitAPIError error);

    public void attachContext(Context context) {
        this.context = context.getApplicationContext();
    }
}
