package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Challenge;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.ChallengeViewPagerPositions;
import com.nike.dooit.views.main.fragments.ChallengeFragment;
import com.nike.dooit.views.main.fragments.TargetFragment;
import com.nike.dooit.views.main.fragments.TipsFragment;
import com.nike.dooit.views.main.fragments.bot.BotFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import java.util.List;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeQuizPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private Challenge challenge;
    private List<Question> questions;

    public ChallengeQuizPagerAdapter(FragmentManager fm, Context context, Challenge challenge) {
        super(fm);
        this.context = context;
        this.challenge = challenge;
        if (challenge != null) {
            this.questions = challenge.getQuestions();
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ChallengeQuizQuestionFragment.newInstance(questions.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return questions != null ? questions.size() : 0;
    }
}
