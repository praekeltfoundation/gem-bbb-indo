package org.gem.indo.dooit.helpers;

import android.app.Application;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.helpers.bot.ListParameterizedType;
import org.gem.indo.dooit.helpers.challenge.LongSparseArrayParameterizedType;
import org.gem.indo.dooit.helpers.challenge.MapParameterizedType;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.Token;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.ParticipantAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestionState;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted {
    private static final String TOKEN = "token";
    private static final String USER = "user";
    private static final String CHALLENGE = "challenge";
    private static final String QUIZ_INDEX = "quiz_index";
    private static final String QUIZ_STATE = "quiz_state";
    private static final String QUIZ_ANSWERS = "quiz_answers";
    private static final String BOT = "bot";
    private static final String GOAL = "goal";
    private static final String GOAL_PROTOTYPE = "goal_prototype";
    private static final String BOT_TIP = "bot_tip";
    private static final String TIPS = "tips";
    private static final String FAVOURITES = "favourites";
    private static final String TAG = Persisted.class.getName();

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    public Persisted(Application application) {
        ((DooitApplication) application).component.inject(this);
    }

    /*** Bot ***/

    public ArrayList<BaseBotModel> loadConversationState(BotType type) {
        String conv = dooitSharedPreferences.getString(BOT + type.name(), "");
        if (TextUtils.isEmpty(conv))
            return new ArrayList<>();
        else {
            Gson gson = new Gson();
            ArrayList<LinkedTreeMap> arrayList = gson.fromJson(conv, ArrayList.class);
            ArrayList<BaseBotModel> result = new ArrayList<BaseBotModel>();

            for (LinkedTreeMap val : arrayList) {
                if (Node.class.toString().equals(val.get("classType"))) {
                    String obj = gson.toJson(val);
                    result.add(gson.fromJson(obj, Node.class));
                } else if (Answer.class.toString().equals(val.get("classType"))) {
                    String obj = gson.toJson(val);
                    result.add(gson.fromJson(obj, Answer.class));
                }
            }
            return result;
        }
    }

    public void saveConversationState(BotType type, List<BaseBotModel> conversation) {
        dooitSharedPreferences.setComplex(BOT + type.name(), conversation);
    }

    public boolean hasConversation(BotType type) {
        return dooitSharedPreferences.containsKey(BOT + type.name());
    }

    public void clearConversation() {
        for (BotType botType : BotType.values()) {
            dooitSharedPreferences.remove(BOT + botType.name());
        }
    }

    public void saveConvoGoal(BotType type, Goal goal) {
        dooitSharedPreferences.setComplex(BOT + "_" + GOAL + "_" + type.name(), goal);
    }

    public Goal loadConvoGoal(BotType type) {
        if (hasConvoGoal(type))
            return dooitSharedPreferences.getComplex(BOT + "_" + GOAL + "_" + type.name(), Goal.class);
        return null;
    }

    public boolean hasConvoGoal(BotType type) {
        return dooitSharedPreferences.containsKey(BOT + "_" + GOAL + "_" + type.name());
    }

    public void clearConvoGoals() {
        for (BotType botType : BotType.values())
            dooitSharedPreferences.remove(BOT + "_" + GOAL + "_" + botType.name());
    }

    public void saveGoalProtos(List<GoalPrototype> prototypes) {
        dooitSharedPreferences.setComplex(GOAL_PROTOTYPE, prototypes);
    }

    public List<GoalPrototype> loadGoalProtos() {
        if (hasGoalProtos()) {
             GoalPrototype[] protos = dooitSharedPreferences.getComplex(GOAL_PROTOTYPE, GoalPrototype[].class);
            if (protos != null)
                return new ArrayList<GoalPrototype>(Arrays.asList(protos));
            else
                return new ArrayList<GoalPrototype>();
        }
        return null;
    }

    public boolean hasGoalProtos() {
        return dooitSharedPreferences.containsKey(GOAL_PROTOTYPE);
    }

    public void clearGoalProtos() {
        dooitSharedPreferences.remove(GOAL_PROTOTYPE);
    }

    public void saveConvoTip(Tip tip){
        dooitSharedPreferences.setComplex(BOT_TIP, tip);
    }

    public boolean hasConvoTip() {
        return dooitSharedPreferences.containsKey(BOT_TIP);
    }

    public Tip loadConvoTip() {
        if (hasConvoTip())
            return dooitSharedPreferences.getComplex(BOT_TIP, Tip.class);
        return null;
    }

    public void clearConvoTip() {
        dooitSharedPreferences.remove(BOT_TIP);
    }

    /*** User ***/

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

    /*** Challenge ***/

    public void setActiveChallenge(BaseChallenge activeChallenge) {
        dooitSharedPreferences.setComplex(CHALLENGE, activeChallenge);
    }

    public BaseChallenge getCurrentChallenge() {
        return loadCurrentChallenge();
    }

    private BaseChallenge loadCurrentChallenge() {
        return dooitSharedPreferences.getComplex(CHALLENGE, BaseChallenge.class);
    }

    public boolean hasCurrentChallenge() {
        BaseChallenge challenge = loadCurrentChallenge();
        return challenge != null;
    }

    public void clearCurrentChallenge() {
        dooitSharedPreferences.remove(CHALLENGE);
    }

    public void saveQuizChallengeState(LongSparseArray<QuizChallengeQuestionState> state) {
        dooitSharedPreferences.setComplex(QUIZ_STATE, state);
    }

    public LongSparseArray<QuizChallengeQuestionState> loadQuizChallengeState() {
        LongSparseArrayParameterizedType type = new LongSparseArrayParameterizedType(QuizChallengeQuestionState.class);
        return dooitSharedPreferences.getComplex(QUIZ_STATE, type);
    }

    public void clearQuizChallengeState() {
        dooitSharedPreferences.remove(QUIZ_STATE);
    }

    public void saveQuizChallengeAnswers(List<ParticipantAnswer> answers) {
        dooitSharedPreferences.setComplex(QUIZ_ANSWERS, answers);
    }

    public List<ParticipantAnswer> loadQuizChallengeAnswers() {
        ListParameterizedType type = new ListParameterizedType(ParticipantAnswer.class);
        return dooitSharedPreferences.getComplex(QUIZ_ANSWERS, type);
    }

    public void clearQuizChallengeAnswers() {
        dooitSharedPreferences.remove(QUIZ_ANSWERS);
    }

    /*** Tips ***/

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

