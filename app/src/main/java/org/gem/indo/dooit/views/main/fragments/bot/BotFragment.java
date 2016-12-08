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

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.helpers.bot.BotFeed;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.target.controllers.GoalAddController;
import org.gem.indo.dooit.views.main.fragments.target.controllers.GoalDepositController;
import org.gem.indo.dooit.views.main.fragments.target.controllers.GoalEditController;
import org.gem.indo.dooit.views.main.fragments.target.controllers.GoalWithdrawController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BotFragment extends MainFragment implements HashtagView.TagsClickListener {

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
    TipManager tipManager;

    BotType type = BotType.DEFAULT;
    BotController controller;
    BotFeed feed;
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
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_bot, container, false);
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
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main_bot, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_bot_clear) {
            persisted.clearConversation();
            persisted.clearConvoGoals();
            type = BotType.DEFAULT;
            createFeed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActive() {
        super.onActive();

        // Choose which conversation to instantiate
        if (persisted.isNewBotUser()) {
            setBotType(BotType.DEFAULT);
            persisted.setNewBotUser(false);
        }

        createFeed();
    }

    private void createFeed() {
        if (clearState) {
            persisted.clearConversation();
            getBotAdapter().clear();
            // Don't clear Goals or Tips as they are the current argument passing mechanism
            clearState = false;
        }

        feed = new BotFeed<>(getContext());
        switch (type) {
            case DEFAULT:
            case GOAL_ADD:
                feed.parse(R.raw.goal_add, Node.class);
                getGoalAddResources();
                break;
            case GOAL_DEPOSIT:
                feed.parse(R.raw.goal_deposit, Node.class);
                getGoalWithdrawResources();
                break;
            case GOAL_WITHDRAW:
                feed.parse(R.raw.goal_withdraw, Node.class);
                getGoalWithdrawResources();
                break;
            case GOAL_EDIT:
                feed.parse(R.raw.goal_edit, Node.class);
                getGoalWithdrawResources();
                break;
            case TIP_INTRO:
                feed.parse(R.raw.tip_intro, Node.class);
                getGoalWithdrawResources();
                break;
        }
    }

    private void getGoalAddResources() {
        // TODO: Refactor into own class
        ArrayList<Observable> reqSync = new ArrayList<>();
        Observable goalProtos = goalManager.retrieveGoalPrototypes(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasGoalProtos())
            goalProtos.subscribe(new Action1<List<GoalPrototype>>() {
                @Override
                public void call(List<GoalPrototype> goalPrototypes) {
                    persisted.saveGoalProtos(goalPrototypes);
                }
            });
        else
            reqSync.add(goalProtos);

        if (reqSync.size() > 0) {
            showProgressBar();
            Observable.from(reqSync).flatMap(new Func1<Observable, Observable<?>>() {
                @Override
                public Observable<?> call(Observable observable) {
                    return observable;
                }
            }).doOnCompleted(new Action0() {
                @Override
                public void call() {
                    if (getContext() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConversation();
                                initializeBot();
                            }
                        });
                    }
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if (o instanceof List) {
                        if (((List) o).size() == 0) return;
                        if (((List) o).get(0) instanceof GoalPrototype)
                            persisted.saveGoalProtos((List<GoalPrototype>) o);
                    }
                }
            });
        } else
            initializeBot();
    }

    private void getGoalWithdrawResources() {
        // TODO: Refactor into own class
        ArrayList<Observable> reqSync = new ArrayList<>();
        Observable tips = tipManager.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasConvoTip())
            tips.subscribe(new Action1<List<Tip>>() {
                @Override
                public void call(List<Tip> newTips) {
                    if (!newTips.isEmpty())
                        persisted.saveConvoTip(newTips.get(0));
                }
            });
        else
            reqSync.add(tips);

        if (reqSync.size() > 0) {
            showProgressBar();
            Observable.from(reqSync).flatMap(new Func1<Observable, Observable<?>>() {
                @Override
                public Observable<?> call(Observable observable) {
                    return observable;
                }
            }).doOnCompleted(new Action0() {
                @Override
                public void call() {
                    if (getContext() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConversation();
                                initializeBot();
                            }
                        });
                    }
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if (o instanceof List) {
                        if (((List) o).size() == 0) return;
                        if (((List) o).get(0) instanceof Tip)
                            persisted.saveConvoTip((Tip) ((List) o).get(0));
                    }
                }
            });
        } else
            initializeBot();
    }

    private void initializeBot() {
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
            case DEFAULT:
                getAndAddNode(null);
                break;
            case GOAL_ADD:
                getAndAddNode("goal_add_ask_goal");
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
        }
    }

    public void setBotType(BotType type) {
        this.type = type;
    }

    private BotController createBotController(BotType botType) {
        switch (botType) {
            case DEFAULT:
            case GOAL_ADD:
                Goal g1 = persisted.loadConvoGoal(BotType.GOAL_ADD);
                if (g1 == null)
                    g1 = new Goal();
                return new GoalAddController(getActivity(), getBotAdapter(), g1, persisted.loadConvoTip());
            case GOAL_DEPOSIT:
                Goal g2 = persisted.loadConvoGoal(BotType.GOAL_DEPOSIT);
                if (g2 == null)
                    throw new RuntimeException("No Goal was persisted for Goal Deposit conversation.");
                return new GoalDepositController(getActivity(), g2, persisted.loadConvoTip());
            case GOAL_WITHDRAW:
                Goal g3 = persisted.loadConvoGoal(BotType.GOAL_WITHDRAW);
                if (g3 == null)
                    throw new RuntimeException("No Goal was persisted for Goal Withdraw conversation.");
                return new GoalWithdrawController(getActivity(), g3, persisted.loadConvoTip());
            case GOAL_EDIT:
                Goal g4 = persisted.loadConvoGoal(BotType.GOAL_EDIT);
                if (g4 == null)
                    throw new RuntimeException("No Goal was persisted for Goal Edit converstation");
                return new GoalEditController(getActivity(), g4, persisted.loadConvoTip());
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
        }

        if (shouldAdd(answer))
            getBotAdapter().addItem(answer);
        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

        if (!TextUtils.isEmpty(answer.getNext())) {
            getAndAddNode(answer.getNext());
        } else {
            answerView.setData(new ArrayList<>());
        }
    }

    private Map<String, Answer> createAnswerLog(List<BaseBotModel> converstation) {
        Map<String, Answer> answerLog = new LinkedHashMap<>();
        for (BaseBotModel model : converstation)
            if (model instanceof Answer)
                answerLog.put(model.getName(), (Answer) model);
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

        if (node != null) {
            node.setIconHidden(iconHidden);

            if (shouldAdd(currentModel))
                getBotAdapter().addItem(currentModel);

            conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
            persisted.saveConversationState(type, getBotAdapter().getDataSet());

            // Reached a controller Node
            if (node.hasCallback() && controller != null)
                controller.onCall(node.getCallback(), createAnswerLog(getBotAdapter().getDataSet()), node);

            // Reached an async controller Node
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
        }
    }

    private void addAnswerOptions(Node node) {
        answerView.setData(new ArrayList<>());
        answerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                answerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
            }
        });

        if (node.getAnswers().size() > 0) {
            if (TextUtils.isEmpty(node.getAutoAnswer())) {
                answerView.setData(node.getAnswers(), new HashtagView.DataStateTransform<Answer>() {
                    @Override
                    public CharSequence prepareSelected(Answer item) {
                        return Utils.populateFromPersisted(persisted, getBotAdapter(), item.getText(getContext()), item.getTextParams());
                    }

                    @Override
                    public CharSequence prepare(Answer item) {
                        return Utils.populateFromPersisted(persisted, getBotAdapter(), item.getText(getContext()), item.getTextParams());
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
        } else if (!TextUtils.isEmpty(node.getAutoNext())) {
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
        }
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
