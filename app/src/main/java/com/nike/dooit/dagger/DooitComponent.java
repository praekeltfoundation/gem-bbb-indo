package com.nike.dooit.dagger;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.DooitManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.RootActivity;
import com.nike.dooit.views.main.MainActivity;
import com.nike.dooit.views.main.fragments.ChallengeFragment;
import com.nike.dooit.views.main.fragments.TipsFragment;
import com.nike.dooit.views.main.fragments.bot.BotFragment;
import com.nike.dooit.views.main.fragments.bot.viewholders.AnswerImageSelectViewHolder;
import com.nike.dooit.views.main.fragments.bot.viewholders.GoalVerificationViewHolder;
import com.nike.dooit.views.main.fragments.bot.viewholders.TextViewHolder;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import com.nike.dooit.views.main.fragments.target.TargetFragment;
import com.nike.dooit.views.main.fragments.tip.TipsListFragment;
import com.nike.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import com.nike.dooit.views.main.fragments.tip.providers.TipProvider;
import com.nike.dooit.views.main.fragments.tip.viewholders.TipViewHolder;
import com.nike.dooit.views.onboarding.LoginActivity;
import com.nike.dooit.views.onboarding.ProfileImageActivity;
import com.nike.dooit.views.onboarding.RegistrationActivity;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.settings.SettingsActivity;
import com.nike.dooit.views.tip.TipArticleActivity;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by herman on 2016/11/05.
 */
@Singleton
@Component(modules = DooitModule.class)
public interface DooitComponent {

    void inject(DooitManager o);

    void inject(RootActivity o);

    void inject(DooitApplication o);

    void inject(DooitActivity o);

    void inject(MainActivity o);

    void inject(ProfileActivity o);

    void inject(LoginActivity o);

    void inject(SettingsActivity o);

    void inject(RegistrationActivity o);

    void inject(ProfileImageActivity o);

    void inject(Persisted o);

    void inject(ChallengeFragment o);

    void inject(ChallengeFreeformFragment o);

    void inject(ChallengeQuizFragment o);

    void inject(BotFragment o);

    void inject(AnswerImageSelectViewHolder o);

    void inject(GoalVerificationViewHolder o);

    void inject(TargetFragment o);

    void inject(TipsFragment o);

    void inject(TextViewHolder o);

    void inject(TipsListFragment o);

    void inject(TipProvider o);

    void inject(TipsListAdapter o);

    void inject(TipViewHolder o);

    void inject(TipArticleActivity o);
}
