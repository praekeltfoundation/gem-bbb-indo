package org.gem.indo.dooit.controllers.challenge;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Locale;
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
            case GET_END_OPTIONS:
                getEndOptions(botRunner);
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

    private void getEndOptions(BotRunner botRunner) {
        Node node = new Node();
        node.setName("budget_edit_q_user_expenses");
        node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload

        Answer answer;

        // Done
        answer = new Answer();
        answer.setName("challenge_participant_a_badge_done");
        answer.setProcessedText(getContext().getString(R.string.bot_general_end_done));
        answer.setNext("challenge_participant_end");
        node.addAnswer(answer);

        // Tips
        answer = new Answer();
        answer.setName("challenge_participant_a_badge_tips");
        answer.setProcessedText(getContext().getString(R.string.bot_general_end_tips));
        answer.setNext("challenge_participant_end_show_tips");
        node.addAnswer(answer);

        // Challenge
        answer = new Answer();
        answer.setName("challenge_participant_a_badge_challenge");
        answer.setProcessedText(getContext().getString(R.string.bot_general_end_challenge));
        answer.setNext("challenge_participant_end_challenge");
        node.addAnswer(answer);

        // Budget
        if (new BudgetDAO().hasBudget()) {
            answer = new Answer();
            answer.setName("challenge_participant_a_badge_budget_edit");
            answer.setProcessedText(getContext().getString(R.string.bot_general_end_budget_edit));
            answer.setNext("challenge_participant_start_edit");
            node.addAnswer(answer);
        } else {
            answer = new Answer();
            answer.setName("challenge_participant_a_badge_budget_create");
            answer.setProcessedText(getContext().getString(R.string.bot_general_end_budget_create));
            answer.setNext("challenge_participant_start_create");
            node.addAnswer(answer);
        }

        node.finish();

        botRunner.queueNode(node);

    }
}
