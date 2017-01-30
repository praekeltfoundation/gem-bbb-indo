package org.gem.indo.dooit.controllers.challenge;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by frede on 2017/01/26.
 */

public class ChallengeWinnerController extends DooitBotController {

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    private Badge badge;
    private BaseChallenge challenge;
    private Context context;

    public ChallengeWinnerController(Context context, Badge badge, BaseChallenge challenge) {
        super(context, BotType.CHALLENGE_WINNER);
        this.context = context;
        this.badge = badge;
        this.challenge = challenge;
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        switch(paramType) {
            case BADGE_INTRO:
                if (hasBadge())
                    model.values.put(paramType.getKey(), badge.getIntro());
                break;
        }
        super.resolveParam(model, paramType);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch(key) {
            case WINNING_BADGE:
                showConfetti();
                break;
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case CONFIRM_NOTIFY:
                confirmNotified(listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
        }
    }

    private void showConfetti(){
        if (context instanceof MainActivity)
            ((MainActivity) context).showConfetti();
    }

    private void confirmNotified(final OnAsyncListener listener) {
        if(challenge != null) {
            challengeManager.confirmChallengeWinnerNotification(challenge.getId(), new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).doAfterTerminate(new Action0() {
                @Override
                public void call() {
                    persisted.clearConvoWinner();
                    notifyDone(listener);
                }
            }).subscribe(new Action1<Response<Void>>() {
                @Override
                public void call(final Response<Void> response) {

                }
            });
        }
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case BADGE:
                return badge;
            default:
                return super.getObject(objType);
        }
    }

    private boolean hasBadge() {
        return badge != null;
    }

    private boolean hasChallenge() {
        return challenge != null;
    }
}
