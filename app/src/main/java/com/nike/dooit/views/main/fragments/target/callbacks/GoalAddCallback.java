package com.nike.dooit.views.main.fragments.target.callbacks;

import android.util.Log;

import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BotCallback;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public class GoalAddCallback implements BotCallback {

    private static final String TAG = GoalAddCallback.class.getName();

    public GoalAddCallback() {
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {
        Log.d(TAG, "Bot Done");
    }
}
