package org.gem.indo.dooit.dagger;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.DooitManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.RootActivity;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.fragments.bot.BotFragment;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerGoalGalleryViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerImageSelectViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalVerificationViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.TextViewHolder;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.main.fragments.target.callbacks.GoalAddCallback;
import org.gem.indo.dooit.views.main.fragments.target.callbacks.GoalDepositCallback;
import org.gem.indo.dooit.views.main.fragments.tip.TipsFragment;
import org.gem.indo.dooit.views.main.fragments.tip.TipsListFragment;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;
import org.gem.indo.dooit.views.main.fragments.tip.viewholders.TipViewHolder;
import org.gem.indo.dooit.views.onboarding.ChangeNameActivity;
import org.gem.indo.dooit.views.onboarding.ChangePasswordActivity;
import org.gem.indo.dooit.views.onboarding.LoginActivity;
import org.gem.indo.dooit.views.onboarding.ProfileImageActivity;
import org.gem.indo.dooit.views.onboarding.RegistrationActivity;
import org.gem.indo.dooit.views.profile.ProfileActivity;
import org.gem.indo.dooit.views.settings.SettingsActivity;
import org.gem.indo.dooit.views.tip.TipArticleActivity;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by herman on 2016/11/05.
 */
@Singleton
@Component(modules = DooitModule.class)
public interface DooitComponent {

    void inject(AnswerImageSelectViewHolder o);

    void inject(AnswerGoalGalleryViewHolder o);

    void inject(BotFragment o);

    void inject(ChallengeFragment o);

    void inject(ChallengeFreeformFragment o);

    void inject(ChallengeQuizFragment o);

    void inject(ChallengeRegisterFragment o);

    void inject(ChangeNameActivity o);

    void inject(ChangePasswordActivity o);

    void inject(DooitActivity o);

    void inject(DooitApplication o);

    void inject(DooitManager o);

    void inject(GoalAddCallback o);

    void inject(GoalDepositCallback o);

    void inject(GoalVerificationViewHolder o);

    void inject(LoginActivity o);

    void inject(MainActivity o);

    void inject(Persisted o);

    void inject(ProfileActivity o);

    void inject(ProfileImageActivity o);

    void inject(RegistrationActivity o);

    void inject(RootActivity o);

    void inject(SettingsActivity o);

    void inject(TargetFragment o);

    void inject(TextViewHolder o);

    void inject(TipArticleActivity o);

    void inject(TipProvider o);

    void inject(TipViewHolder o);

    void inject(TipsFragment o);

    void inject(TipsListAdapter o);

    void inject(TipsListFragment o);
}
