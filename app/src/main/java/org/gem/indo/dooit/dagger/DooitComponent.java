package org.gem.indo.dooit.dagger;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.CustomNotificationManager;
import org.gem.indo.dooit.api.managers.DooitManager;
import org.gem.indo.dooit.api.managers.FeedbackManager;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.controllers.RequirementResolver;
import org.gem.indo.dooit.controllers.budget.BudgetCreateController;
import org.gem.indo.dooit.controllers.budget.BudgetEditController;
import org.gem.indo.dooit.controllers.challenge.ChallengeWinnerController;
import org.gem.indo.dooit.controllers.goal.GoalAddController;
import org.gem.indo.dooit.controllers.goal.GoalBotController;
import org.gem.indo.dooit.controllers.goal.GoalDepositController;
import org.gem.indo.dooit.controllers.goal.GoalEditController;
import org.gem.indo.dooit.controllers.goal.GoalWithdrawController;
import org.gem.indo.dooit.controllers.survey.SurveyController;
import org.gem.indo.dooit.helpers.DooitParamBuilder;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.prefetching.PrefetchAlarmReceiver;
import org.gem.indo.dooit.services.NotificationService;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.RootActivity;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.fragments.ChallengeLightboxFragment;
import org.gem.indo.dooit.views.main.fragments.bot.BotFragment;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerImageSelectViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.BadgeViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalGalleryViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalInfoViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalInformationGalleryItemViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalInformationGalleryViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalVerificationViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalWeeklyTargetListSummaryViewholder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.TextViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.TipBotViewHolder;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeDoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengePictureFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizDoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.main.fragments.tip.TipsFragment;
import org.gem.indo.dooit.views.main.fragments.tip.TipsListFragment;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;
import org.gem.indo.dooit.views.main.fragments.tip.viewholders.TipViewHolder;
import org.gem.indo.dooit.views.onboarding.ChangeEmailAddressActivity;
import org.gem.indo.dooit.views.onboarding.ChangeNameActivity;
import org.gem.indo.dooit.views.onboarding.ChangePasswordActivity;
import org.gem.indo.dooit.views.onboarding.ChangeSecurityQuestionActivity;
import org.gem.indo.dooit.views.onboarding.LoginActivity;
import org.gem.indo.dooit.views.onboarding.PasswordResetActivity;
import org.gem.indo.dooit.views.onboarding.ProfileImageActivity;
import org.gem.indo.dooit.views.onboarding.RegistrationActivity;
import org.gem.indo.dooit.views.onboarding.fragments.PasswordResetPasswordFragment;
import org.gem.indo.dooit.views.onboarding.fragments.PasswordResetUsernameFragment;
import org.gem.indo.dooit.views.profile.ProfileActivity;
import org.gem.indo.dooit.views.profile.fragments.AchievementFragment;
import org.gem.indo.dooit.views.profile.fragments.BudgetFragment;
import org.gem.indo.dooit.views.settings.FeedbackActivity;
import org.gem.indo.dooit.views.settings.SettingsActivity;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by herman on 2016/11/05.
 */
@Singleton
@Component(modules = DooitModule.class)
public interface DooitComponent {

    void inject(AchievementFragment o);

    void inject(AnswerImageSelectViewHolder o);

    void inject(GoalGalleryViewHolder o);

    void inject(BotFragment o);

    void inject(BadgeViewHolder o);

    void inject(BudgetCreateController o);

    void inject(BudgetEditController o);

    void inject(BudgetFragment o);

    void inject(ChallengeActivity o);

    void inject(ChallengeFragment o);

    void inject(ChallengeFreeformFragment o);

    void inject(ChallengePictureFragment o);

    void inject(ChallengeQuizFragment o);

    void inject(ChallengeRegisterFragment o);

    void inject(ChallengeDoneFragment o);

    void inject(ChallengeQuizDoneFragment o);

    void inject(ChangeNameActivity o);

    void inject(ChangeEmailAddressActivity o);

    void inject(ChangePasswordActivity o);

    void inject(ChangeSecurityQuestionActivity o);

    void inject(ChallengeWinnerController o);

    void inject(CustomNotificationManager o);

    void inject(DooitActivity o);

    void inject(DooitApplication o);

    void inject(DooitBotController o);

    void inject(DooitManager o);

    void inject(DooitParamBuilder o);

    void inject(FeedbackActivity o);

    void inject(FeedbackManager o);

    void inject(GoalAddController o);

    void inject(GoalBotController o);

    void inject(GoalDepositController o);

    void inject(GoalWithdrawController o);

    void inject(GoalEditController o);

    void inject(GoalVerificationViewHolder o);

    void inject(GoalInfoViewHolder o);

    void inject(LoginActivity o);

    void inject(MainActivity o);

    void inject(MinimalWebViewActivity o);

    void inject(NotificationService o);

    void inject(PasswordResetActivity o);

    void inject(PasswordResetPasswordFragment o);

    void inject(PasswordResetUsernameFragment o);

    void inject(Persisted o);

    void inject(ProfileActivity o);

    void inject(ProfileImageActivity o);

    void inject(RegistrationActivity o);

    void inject(RequirementResolver o);

    void inject(RootActivity o);

    void inject(SettingsActivity o);

    void inject(SurveyController o);

    void inject(SurveyManager o);

    void inject(TargetFragment o);

    void inject(TextViewHolder o);

    void inject(TipBotViewHolder o);

    void inject(TipProvider o);

    void inject(TipViewHolder o);

    void inject(TipsFragment o);

    void inject(TipsListAdapter o);

    void inject(TipsListFragment o);

    void inject(PrefetchAlarmReceiver o);

    void inject(ChallengeLightboxFragment o);

    void inject(GoalInformationGalleryViewHolder o);

    void inject(GoalInformationGalleryItemViewHolder o);

    void inject(GoalWeeklyTargetListSummaryViewholder o);
}
