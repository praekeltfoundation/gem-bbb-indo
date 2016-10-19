package com.rr.rgem.gem.service;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Ensure callbacks are run on the UI thread.
 *
 * Created by Wimpie Victor on 2016/10/19.
 */
public class MainThreadExecutor implements Executor {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }

    @Override
    public String toString() {
        return "MainThreadExecutor";
    }
}
