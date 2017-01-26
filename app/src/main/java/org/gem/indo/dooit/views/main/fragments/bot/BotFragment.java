package org.gem.indo.dooit.views.main.fragments.bot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.controllers.RequirementResolver;
import org.gem.indo.dooit.controllers.goal.GoalAddController;
import org.gem.indo.dooit.controllers.goal.GoalDepositController;
import org.gem.indo.dooit.controllers.goal.GoalEditController;
import org.gem.indo.dooit.controllers.goal.GoalWithdrawController;
import org.gem.indo.dooit.controllers.misc.ReturningUserController;
import org.gem.indo.dooit.controllers.survey.BaselineSurveyController;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.bot.BotFeed;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BotFragment extends MainFragment implements HashtagView.TagsClickListener, BotRunner {

    @BindView(R.id.fragment_bot)
    View background;

    @BindView(R.id.fragment_bot_conversation_recycler_view)
    RecyclerView conversationRecyclerView;

    @BindView(R.id.fragment_bot_progress_bar_container)
    View progressBarContainer;

    @BindView(R.id.fragment_bot_answer_hash_view)
    HashtagView answerView;

    @Inject
    Persisted persisted;

    @Inject
    GoalManager goalManager;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    TipManager tipManager;

    BotType type = BotType.DEFAULT;
    BotController controller;
    BotFeed<Node> feed;
    BaseBotModel currentModel;
    boolean clearState = false;

    public BotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BotFragment.
     */
    public static BotFragment newInstance() {
        BotFragment fragment = new BotFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bot, container, false);
        ButterKnife.bind(this, view);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, background);
        answerView.addOnTagClickListener(this);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        conversationRecyclerView.setAdapter(new BotAdapter(getContext(), this));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (Constants.DEBUG) {
            getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
            getActivity().getMenuInflater().inflate(R.menu.menu_main_bot, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_bot_baseline_survey:
                setClearState(true);
                finishConversation();
                setBotType(BotType.SURVEY_BASELINE);
                createFeed();
                return true;
            case R.id.menu_main_bot_eatool_survey:
                return true;
            case R.id.menu_main_bot_clear:
                persisted.clearConversation();
                persisted.clearConvoGoals();
                persisted.clearConvoTip();
                type = BotType.DEFAULT;
                createFeed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActive() {
        super.onActive();

        // Choose a default conversation for returning users
        if (!persisted.isNewBotUser() && type == BotType.DEFAULT)
            setBotType(BotType.RETURNING_USER);

        createFeed();
    }

    private void createFeed() {
        if (clearState) {
            persisted.clearConversation();
            getBotAdapter().clear();
            // Don't clear Goals or Tips as they are the current argument passing mechanism
            clearState = false;
        }

        RequirementResolver.Callback reqCallback = new RequirementResolver.Callback() {
            @Override
            public void onStart() {
                showProgressBar();
            }

            @Override
            public void onDone() {
                showConversation();
                initializeBot();
            }
        };

        feed = new BotFeed<>(getContext());
        switch (type) {
            case RETURNING_USER:
                feed.parse(R.raw.returning_user, Node.class);
                new RequirementResolver.Builder(getContext(), BotType.RETURNING_USER)
                        .require(BotObjectType.GOALS)
                        .require(BotObjectType.CHALLENGE)
                        .require(BotObjectType.TIP)
                        .build()
                        .resolve(reqCallback);
                break;
            default:
            case DEFAULT:
            case GOAL_ADD:
                feed.parse(R.raw.goal_add, Node.class);
                new RequirementResolver.Builder(getContext(), BotType.GOAL_ADD)
                        .require(BotObjectType.GOAL_PROTOTYPES)
                        .require(BotObjectType.CHALLENGE)
                        .require(BotObjectType.TIP)
                        .build()
                        .resolve(reqCallback);
                break;
            case GOAL_DEPOSIT:
                feed.parse(R.raw.goal_deposit, Node.class);
                new RequirementResolver.Builder(getContext(), BotType.GOAL_DEPOSIT)
                        .require(BotObjectType.CHALLENGE)
                        .require(BotObjectType.TIP)
                        .build()
                        .resolve(reqCallback);
                break;
            case GOAL_WITHDRAW:
                feed.parse(R.raw.goal_withdraw, Node.class);
                new RequirementResolver.Builder(getContext(), BotType.GOAL_WITHDRAW)
                        .require(BotObjectType.CHALLENGE)
                        .require(BotObjectType.TIP)
                        .build()
                        .resolve(reqCallback);
                break;
            case GOAL_EDIT:
                feed.parse(R.raw.goal_edit, Node.class);
                new RequirementResolver.Builder(getContext(), BotType.GOAL_EDIT)
                        .require(BotObjectType.CHALLENGE)
                        .require(BotObjectType.TIP)
                        .build()
                        .resolve(reqCallback);
                break;
            case TIP_INTRO:
                feed.parse(R.raw.tip_intro, Node.class);
                initializeBot();
                break;
            case SURVEY_BASELINE: {
                feed.parse(R.raw.survey_baseline, Node.class);
                RequirementResolver.Builder builder = new RequirementResolver.Builder(getContext(), BotType.SURVEY_BASELINE)
                        .require(BotObjectType.SURVEY);

                if (persisted.hasConvoSurvey(type))
                    builder.setSurveyId(persisted.loadConvoSurveyId(type));

                builder.build()
                        .resolve(reqCallback);
            }
            break;
        }
    }

    private void initializeBot() {
        if (getActivity() == null)
            // Activity destroyed, fragment still in memory. Requirement resolver is async.
            return;

        controller = createBotController(type);
        getBotAdapter().setController(controller);

        // Load existing
        if (persisted.hasConversation(type)) {
            ArrayList<BaseBotModel> data = persisted.loadConversationState(type);
            if (data.size() > 0) {
                getBotAdapter().clear();
                getBotAdapter().addItems(data);
                conversationRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
                    }
                }, 100);
                BaseBotModel m = data.get(data.size() - 1);
                if (m instanceof Node)
                    addAnswerOptions((Node) data.get(data.size() - 1));
                else if (m instanceof Answer) {
                    getBotAdapter().removeItem(m);
                    onItemClicked(m);
                }
                return;
            }
        }

        // Jump to first node
        switch (type) {
            default:
            case RETURNING_USER:
            case DEFAULT:
                getAndAddNode(null);
                break;
            case GOAL_ADD:
                getAndAddNode("goal_add_q_ask_goal");
                break;
            case GOAL_DEPOSIT:
                getAndAddNode("goal_deposit_intro");
                break;
            case GOAL_WITHDRAW:
                getAndAddNode("goal_withdraw_intro");
                break;
            case GOAL_EDIT:
                getAndAddNode("goal_edit_intro");
                break;
            case TIP_INTRO:
                getAndAddNode("tip_intro_inline_link");
                break;
            case SURVEY_BASELINE:
                getAndAddNode("survey_baseline_intro");
                break;
        }
    }

    public void setBotType(BotType type) {
        this.type = type;
    }

    private BotController createBotController(BotType botType) {
        switch (botType) {
            case RETURNING_USER:
                return new ReturningUserController(getActivity(), this,
                        persisted.loadConvoGoals(botType),
                        persisted.loadConvoChallenge(botType),
                        persisted.loadConvoTip());
            case DEFAULT:
            case GOAL_ADD:
                return new GoalAddController(getActivity(), this,
                        persisted.loadGoalProtos(),
                        persisted.loadConvoGoal(botType),
                        persisted.loadConvoChallenge(botType),
                        persisted.loadConvoTip());
            case GOAL_DEPOSIT:
                return new GoalDepositController(getActivity(), this,
                        persisted.loadConvoGoal(botType),
                        persisted.loadConvoChallenge(botType),
                        persisted.loadConvoTip());
            case GOAL_WITHDRAW:
                return new GoalWithdrawController(getActivity(), this,
                        persisted.loadConvoGoal(botType),
                        persisted.loadConvoChallenge(botType),
                        persisted.loadConvoTip());
            case GOAL_EDIT:
                return new GoalEditController(getActivity(), this,
                        persisted.loadConvoGoal(botType),
                        persisted.loadConvoChallenge(botType),
                        persisted.loadConvoTip());
            case SURVEY_BASELINE:
                return new BaselineSurveyController(getActivity(),
                        persisted.loadConvoSurvey(botType));
            default:
                return null;
        }
    }

    public BotAdapter getBotAdapter() {
        if (conversationRecyclerView != null) {
            return (BotAdapter) conversationRecyclerView.getAdapter();
        }
        return null;
    }

    @Override
    public void onItemClicked(Object item) {
        Answer answer = (Answer) item;

        // For replacing existing nodes in the conversation
        if (!TextUtils.isEmpty(answer.getRemoveOnSelect())) {
            for (BaseBotModel model : new ArrayList<>(getBotAdapter().getDataSet())) {
                if (answer.getRemoveOnSelect().equals(model.getName())) {
                    getBotAdapter().removeItem(model);
                }
            }
        }

        if (answer.getChangeOnSelect() != null) {
            BaseBotModel model = feed.getItem(answer.getChangeOnSelect().first);
            model.setText(answer.getChangeOnSelect().second);
            model.setType(BotMessageType.TEXT);
            getBotAdapter().removeItem(model);
            getBotAdapter().addItem(model);
            CrashlyticsHelper.log(this.getClass().getSimpleName(), "onItemClicked: ", "model changed: " + model.toString());
        }

        if (shouldAdd(answer)) {
            getBotAdapter().addItem(answer);
            CrashlyticsHelper.log(this.getClass().getSimpleName(), "onItemClicked: ", "adding to conversation: " + answer.toString());
        }

        if (answer.hasInputKey() && hasController())
            controller.onAnswerInput(answer.getInputKey(), answer);

        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

        if (answer.hasNext())
            getAndAddNode(answer.getNext());
        else
            answerView.setData(new ArrayList<>());
    }

    private Map<String, Answer> createAnswerLog(List<BaseBotModel> conversation) {
        Map<String, Answer> answerLog = new LinkedHashMap<>();
        for (int i = 0; i < conversation.size(); i++) {
            BaseBotModel model = conversation.get(i);
            if (model instanceof Answer) {
                Answer answer = (Answer) model;
                answerLog.put(answer.getName(), answer);
                if (answer.hasParentName())
                    answerLog.put(answer.getParentName(), answer);
            }
        }
        return answerLog;
    }

    private void getAndAddNode(String name) {
        getAndAddNode(name, false);
    }

    private void getAndAddNode(String name, boolean iconHidden) {
        if (TextUtils.isEmpty(name)) {
            currentModel = feed.getFirstItem();
            getBotAdapter().clear();
        } else
            currentModel = feed.getItem(name);

        final Node node = (Node) currentModel;
        if (node != null)
            addNode(node, iconHidden);
    }

    @Override
    public void addNode(Node node) {
        addNode(node, false);
    }

    public void addNode(final Node node, boolean iconHidden) {
        node.setIconHidden(iconHidden);

        // Nodes can be skipped completely. They will not be added to the adapter, and thus not
        // persisted. The `shouldSkip` method on the controller will not be called again when the
        // conversation is loaded.
        if (hasController() && controller.shouldSkip(node) && node.hasAnyNext()) {
            if (node.hasNext())
                getAndAddNode(node.getNext());
            else if (node.hasAutoNext())
                getAndAddNode(node.getAutoNext());
            else if (node.hasAutoNextNode())
                addNode(node.getAutoNextNode());
            return;
        }

        if (shouldAdd(node))
            getBotAdapter().addItem(node);

        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

        // Reached a Node with a call attribute
        if (node.hasCall() && controller != null)
            controller.onCall(node.getCall(), createAnswerLog(getBotAdapter().getDataSet()), node);

        // Reached a Node with an async attribute
        if (node.hasAsyncCall() && controller != null) {
            // Show loader
            clearAnswerView();
            controller.onAsyncCall(
                    node.getAsyncCall(),
                    createAnswerLog(getBotAdapter().getDataSet()),
                    node,
                    new BotController.OnAsyncListener() {
                        @Override
                        public void onDone() {
                            checkEndOrAddAnswers(node);
                        }
                    }
            );
        } else {
            // Continue synchronously
            checkEndOrAddAnswers(node);
        }
    }

    private void checkEndOrAddAnswers(Node node) {
        // Reached explicit end of current conversation
        if (BotMessageType.getValueOf(node.getType()) == BotMessageType.END) {
            if (node.hasNextScreen()) {
                // Conversation wants to open another fragment
                MainViewPagerPositions pos = MainViewPagerPositions.valueOf(node.getNextScreen());
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).startPage(pos);
            }
            finishConversation();
        } else {
            addAnswerOptions(node);
            persisted.saveConversationState(type, getBotAdapter().getDataSet());
            CrashlyticsHelper.log(this.getClass().getSimpleName(), "checkEndOrAddAnswers: ", "data set: " + getBotAdapter().getDataSet());
        }
    }

    private void addAnswerOptions(Node node) {
        answerView.setData(new ArrayList<>());
        answerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Scroll the recycler after the answer view is layed out
                answerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
            }
        });

        if (node.getAnswers().size() > 0) {
            if (TextUtils.isEmpty(node.getAutoAnswer())) {
                List<Answer> answers = node.getAnswers();
                if (hasController())
                    answers = controller.filter(answers);

                for (Answer answer : answers)
                    processText(answer);

                answerView.setData(answers, new HashtagView.DataStateTransform<Answer>() {
                    @Override
                    public CharSequence prepareSelected(Answer item) {
                        return item.getProcessedText();
                    }

                    @Override
                    public CharSequence prepare(Answer item) {
                        return item.getProcessedText();
                    }
                });
            } else {
                for (Answer answer : node.getAnswers()) {
                    if (node.getAutoAnswer().equals(answer.getName())) {
                        onItemClicked(answer);
                        return;
                    }
                }
            }
        } else if (node.hasAutoNext()) {
            // Auto next set from JSON
            if (BotMessageType.getValueOf(node.getType()) == BotMessageType.STARTCONVO) {
                // Auto load next conversation
                finishConversation();
                BotType botType = BotType.valueOf(node.getAutoNext().toUpperCase());
                setBotType(botType);
                createFeed();
            } else {
                // Auto load next node in current conversation
                getAndAddNode(node.getAutoNext(), true);
            }
        } else if (node.hasAutoNextNode()) {
            // Auto next set from Java code
            addNode(node.getAutoNextNode());
        }
    }

    private void processText(Answer answer) {
        // When answers are added after an async call, the activity may no longer be valid.
        if (getContext() == null)
            return;

        ParamMatch args = ParamParser.parse(answer.getText(getContext()));
        if (!args.isEmpty() && getBotAdapter().hasController()) {
            BotController cb = getBotAdapter().getController();
            for (ParamArg arg : args.getArgs())
                cb.resolveParam(answer, BotParamType.byKey(arg.getKey()));
        }
        answer.setProcessedText(args.process(answer.values.getRawMap()));
        CrashlyticsHelper.log(this.getClass().getSimpleName(), "processText: ", "Processed answer : " + answer.toString());
    }

    private void clearAnswerView() {
        answerView.setData(new ArrayList<>());
    }

    private void finishConversation() {
        if (controller != null)
            controller.onDone(createAnswerLog(getBotAdapter().getDataSet()));
        // Clear answers
        answerView.setData(new ArrayList<>());
        persisted.clearConversation();
        persisted.clearConvoGoals();
        controller = null;
        // FIXME: Clearing the controller happens after the data has been added and before the view holder is instantiated. Viewholder will get null controller.
        // getBotAdapter().setController(null);
        setBotType(BotType.DEFAULT);
    }

    /**
     * Helper to indicate whether a Node should be added to the conversation's view.
     */
    public static boolean shouldAdd(BaseBotModel model) {
        switch (BotMessageType.getValueOf(model.getType())) {
            case BLANK:
            case STARTCONVO:
            case END:
            case BLANKANSWER:
                return false;
            default:
                return true;
        }
    }

    protected boolean hasController() {
        // Check both until bot is more robust
        return controller != null && getBotAdapter().hasController();
    }

    public void setClearState(boolean value) {
        clearState = value;
    }

    private void showProgressBar() {
        conversationRecyclerView.setVisibility(View.GONE);
        answerView.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void showConversation() {
        conversationRecyclerView.setVisibility(View.VISIBLE);
        answerView.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE);
    }
}
