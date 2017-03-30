package org.gem.indo.dooit.helpers;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.helpers.bot.ListParameterizedType;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.Token;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.UserUUID;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestionState;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted {
    private static final String CURRENT_BOT = "current_bot_type";
    private static final String TOKEN = "token";
    private static final String UserUUID = "user_uuid";
    private static final String USER = "user";
    private static final String CHALLENGE = "challenge";
    private static final String QUIZ_INDEX = "quiz_index";
    private static final String QUIZ_STATE = "quiz_state";
    private static final String QUIZ_ANSWERS = "quiz_answers";
    private static final String NEW_BOT_USER = "new_user";
    private static final String BOT = "bot";
    private static final String GOAL = "goal";
    private static final String GOAL_BUILDER = "goal_builder";
    private static final String GOALS = "goals";
    private static final String GOAL_PROTOTYPE = "goal_prototype";
    private static final String BOT_TIP = "bot_tip";
    private static final String TIPS = "tips";
    private static final String FAVOURITES = "favourites";
    private static final String NOTIFICATION = "notification";
    private static final String TAG = Persisted.class.getName();
    private static final String SURVEY_ID = "survey_id";
    private static final String SURVEY = "survey";
    private static final String CHALLENGE_PARTICIPANT_BADGE = "challenge_participant_badge";
    private static final String PARTICIPANT_CHALLENGE = "participant_challenge";
    private static final String WINNING_BADGE = "winning_badge";
    private static final String WINNING_CHALLENGE = "winning_challenge";
    private static final String PARTICIPANT = "participant";
    private static final String TEMPORARY = "temporary";

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    public Persisted(Application application) {
        ((DooitApplication) application).component.inject(this);
    }

    /*******
     * Bot *
     *******/

    public void saveCurrentBotType(BotType type) {
        dooitSharedPreferences.setInteger(CURRENT_BOT, type.getId());
    }

    @NonNull
    public BotType loadCurrentBotType() {
        return BotType.byId(dooitSharedPreferences.getInteger(CURRENT_BOT, BotType.DEFAULT.getId()));
    }

    public void clearCurrentBotType() {
        if (dooitSharedPreferences.containsKey(CURRENT_BOT))
            dooitSharedPreferences.remove(CURRENT_BOT);
    }

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

    public void saveOldConvoGoal(BotType type, Goal goal) {
        dooitSharedPreferences.setComplex(BOT + "_" + GOAL + "_" + type.name() + "_" + TEMPORARY, goal);
    }

    public Goal loadOldConvoGoal(BotType type) {
        if (hasConvoGoal(type))
            return dooitSharedPreferences.getComplex(BOT + "_" + GOAL + "_" + type.name() + "_" + TEMPORARY, Goal.class);
        return null;
    }

    public void saveConvoGoals(BotType type, List<Goal> goals) {
        dooitSharedPreferences.setComplex(BOT + "_" + GOALS + "_" + type.name(), goals);
    }

    public List<Goal> loadConvoGoals(BotType type) {
        Goal[] goals = dooitSharedPreferences.getComplex(BOT + "_" + GOALS + "_" + type.name(), Goal[].class);
        if (goals != null)
            return new ArrayList<Goal>(Arrays.asList(goals));
        else
            return new ArrayList<Goal>();
    }

    public boolean hasConvoGoals(BotType type) {
        return dooitSharedPreferences.containsKey(BOT + "_" + GOALS + "_" + type.name());
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

    public void saveConvoTip(Tip tip) {
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

    public void saveConvoChallenge(BotType botType, BaseChallenge challenge) {
        dooitSharedPreferences.setComplex(BOT + "_" + CHALLENGE + "_" + botType.name(), challenge);
    }

    public BaseChallenge loadConvoChallenge(BotType botType) {
        if (hasConvoChallenge(botType))
            return dooitSharedPreferences.getComplex(BOT + "_" + CHALLENGE + "_" + botType.name(), BaseChallenge.class);
        return null;
    }

    public boolean hasConvoChallenge(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + CHALLENGE + "_" + botType.name());
    }

    public void saveConvoSurveyId(BotType botType, long id) {
        dooitSharedPreferences.setLong(BOT + "_" + SURVEY_ID + "_" + botType.name(), id);
    }

    public long loadConvoSurveyId(BotType botType) {
        return dooitSharedPreferences.getLong(BOT + "_" + SURVEY_ID + "_" + botType.name(), 0L);
    }

    public boolean hasConvoSurveyId(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + SURVEY_ID + "_" + botType.name());
    }

    public void saveConvoSurvey(BotType botType, CoachSurvey survey) {
        dooitSharedPreferences.setComplex(BOT + "_" + SURVEY + "_" + botType.name(), survey);
    }

    public CoachSurvey loadConvoSurvey(BotType botType) {
        if (hasConvoSurvey(botType))
            return dooitSharedPreferences.getComplex(BOT + "_" + SURVEY + "_" + botType.name(), CoachSurvey.class);
        return null;
    }

    public boolean hasConvoSurvey(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + SURVEY + "_" + botType.name());
    }

    public void clearConvoSurvey(BotType botType) {
        dooitSharedPreferences.remove(BOT + "_" + SURVEY + "_" + botType.name());
    }

    public boolean isNewBotUser() {
        return dooitSharedPreferences.getBoolean(NEW_BOT_USER, true);
    }

    public void setNewBotUser(boolean value) {
        dooitSharedPreferences.setBoolean(NEW_BOT_USER, value);
    }

    public void saveConvoWinner(BotType botType, Badge badge, BaseChallenge challenge) {
        dooitSharedPreferences.setComplex(BOT + "_" + WINNING_BADGE + "_" + botType.name(), badge);
        dooitSharedPreferences.setComplex(BOT + "_" + WINNING_CHALLENGE + "_" + botType.name(), challenge);
    }

    public Badge loadWinningBadge(BotType botType) {
        return dooitSharedPreferences.getComplex(BOT + "_" + WINNING_BADGE + "_" + botType.name(), Badge.class);
    }

    public BaseChallenge loadWinningChallenge(BotType botType) {
        return dooitSharedPreferences.getComplex(BOT + "_" + WINNING_CHALLENGE + "_" + botType.name(), BaseChallenge.class);
    }

    public boolean hasConvoWinner(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + WINNING_BADGE + "_" + botType.name())
                && dooitSharedPreferences.containsKey(BOT + "_" + WINNING_CHALLENGE + "_" + botType.name());
    }

    public void clearConvoWinner() {
        dooitSharedPreferences.remove(BOT + "_" + WINNING_BADGE + "_" + BotType.CHALLENGE_WINNER);
    }

    public void clearConvoWinnerChallenge() {
        dooitSharedPreferences.remove(BOT + "_" + WINNING_CHALLENGE + "_" + BotType.CHALLENGE_WINNER);
    }

    public boolean hasConvoWinnerChallenge(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + WINNING_CHALLENGE + "_" + botType.name());
    }

    public void saveConvoParticipant(BotType botType, Badge badge, BaseChallenge challenge) {
        dooitSharedPreferences.setComplex(BOT + "_" + CHALLENGE_PARTICIPANT_BADGE + "_" + botType.name(), badge);
        dooitSharedPreferences.setComplex(BOT + "_" + PARTICIPANT_CHALLENGE + "_" + botType.name(), challenge);
    }

    public Badge loadParticipantBadge(BotType botType) {
        return dooitSharedPreferences.getComplex(BOT + "_" + CHALLENGE_PARTICIPANT_BADGE + "_" + botType.name(), Badge.class);
    }

    public BaseChallenge loadParticipantChallenge(BotType botType) {
        return dooitSharedPreferences.getComplex(BOT + "_" + PARTICIPANT_CHALLENGE + "_" + botType.name(), BaseChallenge.class);
    }

    public boolean hasConvoParticipant(BotType botType) {
        return dooitSharedPreferences.containsKey(BOT + "_" + CHALLENGE_PARTICIPANT_BADGE + "_" + botType.name());
    }

    public void clearConvoParticipant() {
        dooitSharedPreferences.remove(BOT + "_" + CHALLENGE_PARTICIPANT_BADGE + "_" + BotType.CHALLENGE_PARTICIPANT_BADGE);
        dooitSharedPreferences.remove(BOT + "_" + PARTICIPANT_CHALLENGE + "_" + BotType.CHALLENGE_PARTICIPANT_BADGE);
    }

    /********
     * User *
     ********/

    @Nullable
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

    public boolean hasUserUUID() {
        UserUUID gaid = loadUserUUID();
        return gaid != null && !TextUtils.isEmpty(gaid.getGaid());
    }

    private UserUUID loadUserUUID() {
        UserUUID gaid = dooitSharedPreferences.getComplex(UserUUID, UserUUID.class);
        Log.d(TAG, String.format("Loadings: %s", gaid));
        return gaid;
    }

    public String getUserUUID() {
        return loadUserUUID().getGaid();
    }

    public void saveUserUUID(UserUUID gaid) {
        dooitSharedPreferences.setComplex(UserUUID, gaid);
        Log.d(TAG, String.format("Saving: %s", gaid));
    }

    public void clearToken() {
        dooitSharedPreferences.remove(TOKEN);
    }

    public void clearUserUUID() {
        dooitSharedPreferences.remove(UserUUID);
    }

    /*************
     * Challenge *
     *************/

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
        String stored = dooitSharedPreferences.getString(QUIZ_STATE, "");
        if (stored == null || stored.isEmpty()) return null;
        Gson gson = new Gson();
        JsonObject obj = new JsonParser().parse(stored).getAsJsonObject();
        if (!obj.has("mKeys") || !obj.has("mValues")) return null;
        JsonArray keys = obj.get("mKeys").getAsJsonArray();
        JsonArray vals = obj.get("mValues").getAsJsonArray();
        LongSparseArray<QuizChallengeQuestionState> state = new LongSparseArray<>();
        int i = 0;
        while (i < vals.size() && vals.get(i) != null) {
            state.put(keys.get(i).getAsLong(), gson.fromJson(vals.get(i), QuizChallengeQuestionState.class));
            i++;
        }
        return state;
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

    public Participant getParticipant() {
//        ListParameterizedType type = new ListParameterizedType(Participant.class);
        return dooitSharedPreferences.getComplex(PARTICIPANT, Participant.class);
    }

    public void setParticipant(Participant participant) {
        dooitSharedPreferences.setComplex(PARTICIPANT, participant);
    }

    /********
     * Tips *
     ********/

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

    /*****************
     * Notifications *
     *****************/

    public boolean shouldNotify(NotificationType notifyType) {
        return dooitSharedPreferences.getBoolean(NOTIFICATION + notifyType.name(), true);
    }

    public void setNotify(NotificationType notifyType, boolean value) {
        dooitSharedPreferences.setBoolean(NOTIFICATION + notifyType.name(), value);
    }
}

