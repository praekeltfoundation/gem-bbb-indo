package com.rr.rgem.gem.controllers.common;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.GoalActivity;
import com.rr.rgem.gem.GoalsActivity;
import com.rr.rgem.gem.OnBoardingActivity;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.answers.ChallengesAnswers;
import com.rr.rgem.gem.answers.GoalAnswers;
import com.rr.rgem.gem.answers.GoalsAnswers;
import com.rr.rgem.gem.answers.OnBoardingAnswers;
import com.rr.rgem.gem.views.LeftRightConversation;


import java.util.Map;

/**
 * Created by chris on 9/26/2016.
 */
public class Factory {

    public static OnBoardingAnswers createCoach(OnBoardingActivity context, LeftRightConversation conversation){
        OnBoardingAnswers answers = new OnBoardingAnswers();
        JSONState state = new JSONState();
        JSONController controller = new JSONController(context, R.raw.onboarding,answers);
        state.setConversationView(conversation);
        state.setController(controller);
        controller.setState(state);
        answers.setState(state);
        answers.setActivity(context);
        answers.load(context);
        answers.start();

        return answers;
    }

    public static ChallengesAnswers createChallenges(ChallengeActivity context, LeftRightConversation conversation){
        ChallengesAnswers answers = new ChallengesAnswers();
        JSONState state = new JSONState();
        JSONController controller = new JSONController(context, R.raw.challenges,answers);

        Map<String, String> vars = controller.getVars();
        vars.put("toes", context.getResources().getString(R.string.toes));
        vars.put("night",context.getResources().getString(R.string.night));

        state.setConversationView(conversation);
        state.setController(controller);
        controller.setState(state);
        answers.setState(state);
        answers.setActivity(context);
        answers.load(context);
        answers.start();

        return answers;
    }
    public static GoalsAnswers createGoalsCarousel(GoalsActivity context){
        GoalsAnswers answers = new GoalsAnswers();
        answers.load(context);
        context.setGoals(answers.getGoals());
        answers.setActivity(context);
        return answers;
    }

    public static GoalAnswers createGoal(GoalActivity context, LeftRightConversation conversation){
        GoalAnswers answers = new GoalAnswers();
        JSONState state = new JSONState();
        JSONController controller = new JSONController(context, R.raw.goals,answers);

        //Questions
        Map<String, String> vars = controller.getVars();

        vars.put("askGoalName", context.getResources().getString(R.string.askGoalName));
        vars.put("askGoalImage",context.getResources().getString(R.string.askGoalImage));
        vars.put("askGoalImageTake", context.getResources().getString(R.string.askGoalImageTake));
        vars.put("askKnowGoalAmount",context.getResources().getString(R.string.askKnowGoalAmount));
        vars.put("askGoalAmount",context.getResources().getString(R.string.askGoalAmount));
        vars.put("infoDontKnowAmount",context.getResources().getString(R.string.infoDontKnowAmount));
        vars.put("askKnowGoalDate",context.getResources().getString(R.string.askKnowGoalDate));
        vars.put("askGoalDate",context.getResources().getString(R.string.askGoalDate));
        vars.put("askGoalWeeklySaveAmount",context.getResources().getString(R.string.askGoalWeeklySaveAmount));
        vars.put("askGoalSaveAmountOther",context.getResources().getString(R.string.askGoalSaveAmountOther));
        vars.put("askGoalPriorSave",context.getResources().getString(R.string.askGoalPriorSave));
        vars.put("askPriorSavedAmount",context.getResources().getString(R.string.askPriorSavedAmount));
        vars.put("verifyGoalAmount",context.getResources().getString(R.string.verifyGoalAmount));
        vars.put("validationFail",context.getResources().getString(R.string.validationFail));
        vars.put("validationFailDate",context.getResources().getString(R.string.validationFailDate));
        vars.put("validationFailSaveAmountOther",context.getResources().getString(R.string.validationFailSaveAmountOther));
        vars.put("validationFailPriorSavedAmount",context.getResources().getString(R.string.validationFailPriorSavedAmount));
        vars.put("infoGoalEnd",context.getResources().getString(R.string.infoGoalEnd));
        //Buttons
        vars.put("Skip", context.getResources().getString(R.string.Skip));
        vars.put("Capture", context.getResources().getString(R.string.Capture));
        vars.put("knowAmountY", context.getResources().getString(R.string.knowAmountY));
        vars.put("knowAmountN", context.getResources().getString(R.string.knowAmountN));
        vars.put("knowDateY", context.getResources().getString(R.string.knowDateY));
        vars.put("knowDateN", context.getResources().getString(R.string.knowAmountN));
        vars.put("hasSavedY", context.getResources().getString(R.string.hasSavedY));
        vars.put("hasSavedN", context.getResources().getString(R.string.hasSavedN));
        vars.put("otherSaveAmount", context.getResources().getString(R.string.otherSaveAmount));
        vars.put("goalCheckRT", context.getResources().getString(R.string.goalCheckRT));
        vars.put("goalCheckRA", context.getResources().getString(R.string.goalCheckRA));
        vars.put("goalCheckOk", context.getResources().getString(R.string.goalCheckOk));
        vars.put("five", context.getResources().getString(R.string.five));
        vars.put("thousand", context.getResources().getString(R.string.thousand));
        vars.put("thousandFive", context.getResources().getString(R.string.thousandFive));
        vars.put("twoThousand", context.getResources().getString(R.string.twoThousand));



        state.setConversationView(conversation);
        state.setController(controller);
        controller.setState(state);
        answers.setState(state);
        answers.setActivity(context);
        answers.load(context);
        answers.start();

        return answers;
    }
}
