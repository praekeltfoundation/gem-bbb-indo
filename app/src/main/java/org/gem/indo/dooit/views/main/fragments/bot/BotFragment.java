package org.gem.indo.dooit.views.main.fragments.bot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
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
import org.gem.indo.dooit.controllers.challenge.ChallengeParticipantController;
import org.gem.indo.dooit.controllers.challenge.ChallengeWinnerController;
import org.gem.indo.dooit.controllers.goal.GoalAddController;
import org.gem.indo.dooit.controllers.goal.GoalDepositController;
import org.gem.indo.dooit.controllers.goal.GoalEditController;
import org.gem.indo.dooit.controllers.goal.GoalWithdrawController;
import org.gem.indo.dooit.controllers.misc.ReturningUserController;
import org.gem.indo.dooit.controllers.survey.BaselineSurveyController;
import org.gem.indo.dooit.controllers.survey.EAToolSurveyController;
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
import org.gem.indo.dooit.views.main.fragments.bot.adapters.QuickAnswerAdapter;

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
public class BotFragment extends MainFragment implements HashtagView.TagsClickListener,
        QuickAnswerAdapter.OnBotInputListener, BotRunner {

    private static final int ANSWER_SPAN_NARROW = 2;
    private static final int ANSWER_SPAN_WIDE = 3;

    /**
     * The number of `dp` to the right that the quick answer view should peek when it has to
     * scroll horizontally.
     */
    private static final int ANSWER_PEEK_DISTANCE = 48;

    @BindView(R.id.fragment_bot)
    View background;

    @BindView(R.id.fragment_bot_conversation_recycler_view)
    RecyclerView conversationRecyclerView;

    @BindView(R.id.fragment_bot_progress_bar_container)
    View progressBarContainer;

    @BindView(R.id.fragment_bot_answer_quick_answers)
    RecyclerView quickAnswers;

    @Inject
    Persisted persisted;

    @Inject
    GoalManager goalManager;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    TipManager tipManager;

    private BotType type = BotType.DEFAULT;
    private BotController controller;
    private BotFeed<Node> feed;
    private Node currentNode;
    boolean clearState = false;

    /**
     * The number of pixels that the quick answers should peek to the right.
     */
    private int answerPeekDistance = 0;

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

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), ANSWER_SPAN_WIDE,
                OrientationHelper.HORIZONTAL, false);
        quickAnswers.setLayoutManager(layoutManager);
        quickAnswers.setItemAnimator(null);
        quickAnswers.setAdapter(new QuickAnswerAdapter(getContext(), this));

        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        conversationRecyclerView.setAdapter(new BotAdapter(getContext(), this));

        // Get pixels to avoid having to do it on each peek animation
        answerPeekDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) ANSWER_PEEK_DISTANCE, getResources().getDisplayMetrics());

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
                setClearState(true);
                finishConversation();
                setBotType(BotType.SURVEY_EATOOL);
                createFeed();
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
                RequirementResolver.Builder builder = new RequirementResolver.Builder(
                        getContext(), BotType.SURVEY_BASELINE)
                        .require(BotObjectType.SURVEY);

                if (persisted.hasConvoSurvey(type))
                    builder.setSurveyId(persisted.loadConvoSurvey(type).getId());

                builder.build()
                        .resolve(reqCallback);
            }
            break;
            case SURVEY_EATOOL: {
                feed.parse(R.raw.survey_eatool, Node.class);
                RequirementResolver.Builder builder = new RequirementResolver.Builder(
                        getContext(), BotType.SURVEY_EATOOL)
                        .require(BotObjectType.SURVEY);

                if (persisted.hasConvoSurvey(type))
                    builder.setSurveyId(persisted.loadConvoSurvey(type).getId());

                builder.build()
                        .resolve(reqCallback);
            }
            break;
            case CHALLENGE_WINNER:
                feed.parse(R.raw.challenge_winner, Node.class);
                initializeBot();
                break;
            case CHALLENGE_PARTICIPANT_BADGE:
                feed.parse(R.raw.challenge_participant_badge, Node.class);
                initializeBot();
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
                getAndAddNode(null);
                break;
            case SURVEY_EATOOL:
                getAndAddNode(null);
                break;
            case CHALLENGE_WINNER:
                getAndAddNode(null);
                break;
            case CHALLENGE_PARTICIPANT_BADGE:
                getAndAddNode(null);
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
            case SURVEY_EATOOL:
                return new EAToolSurveyController(getActivity(),
                        persisted.loadConvoSurvey(botType));
            case CHALLENGE_WINNER:
                return new ChallengeWinnerController(getActivity(),
                        persisted.loadWinningBadge(botType),
                        persisted.loadWinningChallenge(botType));
            case CHALLENGE_PARTICIPANT_BADGE:
                return new ChallengeParticipantController(getActivity(),
                        persisted.loadParticipantBadge(botType),
                        persisted.loadParticipantChallenge(botType));
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

        if (hasController())
            controller.onAnswer(answer);

        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

        if (answer.hasNext())
            getAndAddNode(answer.getNext());
        else
            clearAnswerView();
    }

    @Override
    public void onAnswer(Answer answer) {
        onItemClicked(answer);
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

    ///////////
    // Nodes //
    ///////////

    private void getAndAddNode(String name) {
        getAndAddNode(name, false);
    }

    private void getAndAddNode(String name, boolean iconHidden) {
        BaseBotModel model;
        if (TextUtils.isEmpty(name)) {
            model = feed.getFirstItem();
            getBotAdapter().clear();
        } else
            model = feed.getItem(name);

        if (model == null) {
            // TODO: Log to Crashlytics
            return;
        }

        /*  We are copying the node since in BaseBotViewHolder the BaseBotModel Object is set to be
         *  immutable after the first time it is used to populate the conversation so that the conversation
         *  cannot be changed when the user navigates away and back to the bot. Making a deep copy of the
         *  BaseBotModel Object and passing it through the process means that the objects in the BotFeed
         *  are not modified directly and should solve this problem.
         */
        currentNode = ((Node) model).copy();
        addNode(currentNode, iconHidden);
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

        // Reached a Node with a call attribute
        if (node.hasCall() && controller != null)
            controller.onCall(node.getCall(), createAnswerLog(getBotAdapter().getDataSet()), node);

        if (shouldAdd(node))
            getBotAdapter().addItem(node);

        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

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

    /////////////
    // Answers //
    /////////////

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
        clearAnswerView();

        if (node.getAnswers().size() > 0) {
            if (TextUtils.isEmpty(node.getAutoAnswer())) {
                List<Answer> answers = node.getAnswers();
                if (hasController())
                    answers = controller.filter(answers);

                for (Answer answer : answers)
                    processText(answer);

                setAnswers(answers);

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
        QuickAnswerAdapter adapter = getAnswerAdapter();
        if (adapter != null)
            adapter.clear();
        scrollToBottom();
    }

    private QuickAnswerAdapter getAnswerAdapter() {
        if (quickAnswers != null)
            return (QuickAnswerAdapter) quickAnswers.getAdapter();
        return null;
    }

    private void setAnswers(List<Answer> answers) {
        GridLayoutManager layout = (GridLayoutManager) quickAnswers.getLayoutManager();

        if (layout != null)
            if (answers.size() <= 4)
                layout.setSpanCount(ANSWER_SPAN_NARROW);
            else
                layout.setSpanCount(ANSWER_SPAN_WIDE);

        QuickAnswerAdapter adapter = getAnswerAdapter();

        if (adapter != null)
            adapter.replace(answers);

        scrollToBottom();
        animateQuickAnswers();
    }

    /**
     * Runs a peek animation to notify users that they can scroll right.
     */
    private void animateQuickAnswers() {
        if (quickAnswers == null)
            return;

        // Scheduled to scroll later, because scrolling the recycler view before it has been
        // calculated causes its items to be incorrectly rendered.
        quickAnswers.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (quickAnswers == null)
                    return;

                if (!quickAnswers.canScrollHorizontally(answerPeekDistance))
                    return;

                GridLayoutManager layout = (GridLayoutManager) quickAnswers.getLayoutManager();
                if (layout == null)
                    return;

                QuickAnswerAdapter adapter = getAnswerAdapter();
                if (adapter == null
                        // Division by zero
                        || layout.getSpanCount() == 0
                        // Don't scroll if there is only 1 column. Casting to double because integer
                        // division rounds down.
                        || Math.ceil((double) adapter.getItemCount() / (double) layout.getSpanCount()) <= 1.0)
                    return;

                quickAnswers.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                quickAnswers.smoothScrollBy(answerPeekDistance, 0);
                quickAnswers.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (quickAnswers != null)
                            quickAnswers.smoothScrollBy(-answerPeekDistance, 0);
                    }
                }, 500);
            }
        });
    }

    /**
     * Schedules the conversation recycler view to scroll to the bottom.
     */
    private void scrollToBottom() {
        quickAnswers.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (quickAnswers == null)
                    return;

                quickAnswers.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (conversationRecyclerView == null || getBotAdapter() == null)
                    return;

                // Conversation must allow space for answers, but scroll indicator must be at the
                // bottom of the screen. The recycler view's bottom is aligned with the parent
                // container rather than the top of the quick answers.
                conversationRecyclerView.setPadding(0, 0, 0, quickAnswers.getHeight());
                conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
            }
        });
    }

    //////////////////
    // Conversation //
    //////////////////

    private void finishConversation() {
        if (controller != null)
            controller.onDone(createAnswerLog(getBotAdapter().getDataSet()));
        clearAnswerView();
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

    //////////////////
    // Progress Bar //
    //////////////////

    private void showProgressBar() {
        conversationRecyclerView.setVisibility(View.GONE);
        quickAnswers.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void showConversation() {
        conversationRecyclerView.setVisibility(View.VISIBLE);
        quickAnswers.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE);
    }
}
