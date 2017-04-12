package org.gem.indo.dooit.controllers.challenge;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
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

/**
 * Created by frede on 2017/01/27.
 */

public class ChallengeParticipantController extends DooitBotController {

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    private Badge badge;
    private BaseChallenge challenge;
    protected BotRunner botRunner;
    private Context context;

    public ChallengeParticipantController(Context context, BotRunner botRunner,
                                          Badge badge, BaseChallenge challenge) {
        super(context, BotType.CHALLENGE_PARTICIPANT_BADGE);
        this.badge = badge;
        this.botRunner = botRunner;
        this.challenge = challenge;
        this.context = context;
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch(key) {
            case PARTICIPANT_BADGE:
                showConfetti();
                break;
            case SET_TIP_QUERY:
                tipQuery();
                break;
        }
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

    private boolean hasBadge() {
        return badge != null;
    }

    private void showConfetti(){
        if (context instanceof MainActivity)
            ((MainActivity) context).showConfetti();
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

    private void tipQuery() {
        if (context == null)
            return;

        MainActivity activity = (MainActivity) context;
        activity.setTipQuery(activity.getString(R.string.budget_create_qry_tip_income));
    }
}
