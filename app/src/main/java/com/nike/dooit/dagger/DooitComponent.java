package com.nike.dooit.dagger;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.DooitManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.RootActivity;
import com.nike.dooit.views.main.fragments.bot.BotFragment;
import com.nike.dooit.views.main.fragments.ChallengeFragment;
import com.nike.dooit.views.main.fragments.TargetFragment;
import com.nike.dooit.views.main.fragments.TipsFragment;
import com.nike.dooit.views.main.fragments.tip.TipViewHolder;
import com.nike.dooit.views.main.fragments.tip.TipsListFragment;
import com.nike.dooit.views.main.fragments.tip.adapters.TipsAdapter;
import com.nike.dooit.views.main.fragments.tip.providers.TipProvider;
import com.nike.dooit.views.onboarding.LoginActivity;
import com.nike.dooit.views.onboarding.ProfileImageActivity;
import com.nike.dooit.views.onboarding.RegistrationActivity;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.settings.SettingsActivity;

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

    void inject(ProfileActivity o);

    void inject(LoginActivity o);

    void inject(SettingsActivity o);

    void inject(RegistrationActivity o);

    void inject(ProfileImageActivity o);

    void inject(Persisted o);

    void inject(ChallengeFragment o);

    void inject(BotFragment o);

    void inject(TargetFragment o);

    void inject(TipsFragment o);

    void inject(TipsListFragment o);

    void inject(TipProvider o);

    void inject(TipsAdapter o);

    void inject(TipViewHolder o);
}
