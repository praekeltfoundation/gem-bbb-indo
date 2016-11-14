package com.nike.dooit.helpers;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.models.Tip;
import com.nike.dooit.models.Token;
import com.nike.dooit.models.User;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.challenge.BaseChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted {
    private static final String TOKEN = "token";
    private static final String USER = "user";
    private static final String CHALLENGE = "challenge";
    private static final String BOT = "bot";
    private static final String TIPS = "tips";
    private static final String FAVOURITES = "favourites";
    private static final String TAG = Persisted.class.getName();

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    public Persisted(Application application) {
        ((DooitApplication) application).component.inject(this);
    }

    public ArrayList<BaseBotModel> loadConversationState() {
        return (ArrayList<BaseBotModel>) dooitSharedPreferences.getComplex(BOT, ArrayList.class);
    }

    public void saveConversationState(List<BaseBotModel> conversation) {
        dooitSharedPreferences.setComplex(BOT, conversation);
    }

    public User getCurrentUser() {
        User user = dooitSharedPreferences.getComplex(USER, User.class);
        Log.d(TAG, String.format("Loading: %s", user));
        return user;
    }

    public void setCurrentUser(User user) {
        dooitSharedPreferences.setComplex(USER, user);
        Log.d(TAG, String.format("Saving: %s", user));
    }

    public void clearUser() {
        dooitSharedPreferences.remove(USER);
    }

    //    @Override
    public boolean hasToken() {
        Token token = loadToken();
        return token != null && !TextUtils.isEmpty(token.getToken());
    }

    private Token loadToken() {
        Token token = dooitSharedPreferences.getComplex(TOKEN, Token.class);
        Log.d(TAG, String.format("Loadings: %s", token));
        return token;
    }

    public String getToken() {
        return loadToken().getToken();
    }

    public void saveToken(Token token) {
        dooitSharedPreferences.setComplex(TOKEN, token);
        Log.d(TAG, String.format("Saving: %s", token));
    }

    public void clearToken() {
        dooitSharedPreferences.remove(TOKEN);
    }

    public void setActiveChallenge(BaseChallenge activeChallenge) {
        dooitSharedPreferences.setComplex(CHALLENGE, activeChallenge);
    }

    public Object getCurrentChallenge() {
        return loadCurrentChallenge();
    }

    private BaseChallenge loadCurrentChallenge() {
        return dooitSharedPreferences.getComplex(TOKEN, BaseChallenge.class);
    }

    public boolean hasCurrentChallenge() {
        BaseChallenge challenge = loadCurrentChallenge();
        return challenge != null;
    }

    public List<Tip> getTips() {
        return loadTips(TIPS);
    }

    public void setTips(List<Tip> tips) {
        saveTips(TIPS, tips);
    }

    public boolean hasTips() {
        return !loadTips(TIPS).isEmpty();
    }

    public void clearTips() {
        dooitSharedPreferences.remove(TIPS);
    }

    public List<Tip> getFavouriteTips() {
        return loadTips(FAVOURITES);
    }

    public void setFavourites(List<Tip> tips) {
        saveTips(FAVOURITES, tips);
    }

    public boolean hasFavourites() {
        return !loadTips(FAVOURITES).isEmpty();
    }

    public void clearFavourites() {
        dooitSharedPreferences.remove(FAVOURITES);
    }

    private List<Tip> loadTips(String prefKey) {
        Tip[] tips = dooitSharedPreferences.getComplex(prefKey, Tip[].class);
        if (tips != null) {
            return new ArrayList<Tip>(Arrays.asList(tips));
        } else {
            return new ArrayList<Tip>();
        }
    }

    private void saveTips(String prefKey, List<Tip> tips) {
        dooitSharedPreferences.setComplex(prefKey, tips.toArray());
    }
}

