package org.gem.indo.dooit.controllers.misc;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class ReturningUserController extends DooitBotController {

    private BotRunner botRunner;
    private Budget budget;
    private BaseChallenge challenge;
    private List<Goal> goals;
    private Tip tip;

    public ReturningUserController(Context context, BotRunner botRunner,
                                   List<Goal> goals, BaseChallenge challenge, Tip tip,
                                   Budget budget) {
        super(context, BotType.RETURNING_USER);
        this.botRunner = botRunner;
        this.budget = budget;
        this.challenge = challenge;
        this.goals = goals;
        this.tip = tip;
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case CHECK_BUDGET:
                checkBudget();
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();
        switch (paramType) {
            case CHALLENGE_INTRO:
                model.values.put(key, challenge.getIntro());
                break;
            case CHALLENGE_OUTRO:
                model.values.put(key, challenge.getOutro());
                break;
            case TIP_INTRO:
                model.values.put(key, tip.getIntro());
                break;
            default:
                super.resolveParam(model, paramType);
                break;
        }
    }

    @Override
    public boolean filterQuickAnswer(Answer answer) {
        switch (answer.getName()) {
            case "convo_default_return_a_progress_intro_tip":
                return tip != null;
            case "convo_default_return_a_progress_intro_challenge":
                return challenge != null && challenge.isActive();
            default:
                return true;
        }
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        if (model.getName().equals("convo_default_return_q_summary"))
            return goals.isEmpty();
        else
            return super.shouldSkip(model);
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case CHALLENGE:
                return challenge;
            case TIP:
                return tip;
            case GOALS:
                return goals;
            default:
                return super.getObject(objType);
        }
    }

    private List<Goal> getProblemGoals() {
        List<Goal> problemGoals = new LinkedList<>();

        if (goals != null) {
            for (Goal goal : goals) {
                double weeklyTarget = goal.getWeeklyTarget();
                int numWeeks = (int)goal.getWeeksToNow(WeekCalc.Rounding.DOWN);
                if (numWeeks > 4) {
                    boolean savedEnough = false;
                    Map<String, Float> weeklyTotals = goal.getWeeklyTotals();

                    for (int i = numWeeks - 4; i < numWeeks; i++) {
                        if (weeklyTotals.get(Integer.toString(i)) >= weeklyTarget) {
                            savedEnough = true;
                            break;
                        }
                    }

                    if (!savedEnough) {
                        problemGoals.add(goal);
                    }
                }
            }
        }

        return problemGoals;
    }

    private void checkBudget() {
        Node node = new Node();
        node.setName("budget_edit_q_user_expenses");
        node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
        List<Goal> problemGoals = getProblemGoals();

        if (problemGoals.isEmpty()) {
            node.setAutoNext("convo_default_return_q_options");
        } else {
            if (budget == null) {
                node.setAutoNext("convo_default_q_behind_no_budget");
            } else {
                node.setAutoNext("convo_default_q_behind_budget");
            }
        }

        node.finish();

        botRunner.queueNode(node);
    }
}
