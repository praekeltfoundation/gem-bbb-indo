package com.rr.rgem.gem.controllers.common;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.OnBoardingActivity;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.answers.ChallengesAnswers;
import com.rr.rgem.gem.answers.OnBoardingAnswers;
import com.rr.rgem.gem.views.CoachConversation;

/**
 * Created by chris on 9/26/2016.
 */
public class Factory {

    public static JSONController createCoach(OnBoardingActivity context, CoachConversation conversation){
        OnBoardingAnswers answers = new OnBoardingAnswers();
        JSONState state = new JSONState();
        JSONController controller = new JSONController(context, R.raw.goals,answers);
        state.setConversationView(conversation);
        state.setController(controller);
        answers.setState(state);
        answers.setActivity(context);
        answers.load();
        answers.start();

        return controller;
    }

    public static JSONController createChallenges(ChallengeActivity context, CoachConversation conversation){
        ChallengesAnswers answers = new ChallengesAnswers();
        JSONState state = new JSONState();
        JSONController controller = new JSONController(context, R.raw.challenges,answers);
        state.setConversationView(conversation);
        state.setController(controller);
        answers.setState(state);
        answers.setActivity(context);
        answers.load();
        answers.start();

        return controller;
    }

}
