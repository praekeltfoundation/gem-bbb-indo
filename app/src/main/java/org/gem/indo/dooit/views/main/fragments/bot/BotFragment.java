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

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.bot.BotFeed;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.BotCallback;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.target.callbacks.GoalAddCallback;
import org.gem.indo.dooit.views.main.fragments.target.callbacks.GoalDepositCallback;
import org.gem.indo.dooit.views.main.fragments.target.callbacks.GoalWithdrawCallback;

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

    @BindView(R.id.fragment_bot_conversation_recycler_view)
    RecyclerView conversationRecyclerView;

    @BindView(R.id.fragment_bot_answer_hash_view)
    HashtagView answerView;

    @Inject
    Persisted persisted;

    @Inject
    GoalManager goalManager;

    BotType type = BotType.DEFAULT;
    BotCallback callback;
    BotFeed feed;
    BaseBotModel currentModel;

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
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, conversationRecyclerView);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActive() {
        super.onActive();
        createFeed();
    }

    private void createFeed() {
        feed = new BotFeed<>(getContext());
        switch (type) {
            case DEFAULT:
            case GOAL_ADD:
                feed.parse(R.raw.goal_add, Node.class);
                getGoalAddResources();
                break;
            case GOAL_DEPOSIT:
                feed.parse(R.raw.goal_deposit, Node.class);
                initializeBot();
                break;
            case GOAL_WITHDRAW:
                feed.parse(R.raw.goal_withdraw, Node.class);
                initializeBot();
                break;
            case TIP_INTRO:
                feed.parse(R.raw.tip_intro, Node.class);
                initializeBot();
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
            //ShowLoader
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

    private void initializeBot() {
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

        callback = createBotCallback(type);

        // Jump to first node
        switch (type) {
            case DEFAULT:
                getAndAddNode(null);
                break;
            case GOAL_ADD:
                getAndAddNode("goal_add_carousel_test");
                break;
            case GOAL_DEPOSIT:
                getAndAddNode("goal_deposit_intro");
                break;
            case GOAL_WITHDRAW:
                getAndAddNode("goal_withdraw_intro");
                break;
            case TIP_INTRO:
                getAndAddNode("tip_intro_inline_link");
                break;
        }
    }

    public void setBotType(BotType type) {
        this.type = type;
    }

    private BotCallback createBotCallback(BotType botType) {
        switch (botType) {
            case DEFAULT:
            case GOAL_ADD:
                return new GoalAddCallback((DooitApplication) getActivity().getApplication());
            case GOAL_DEPOSIT:
                Goal g1 = persisted.loadConvoGoal(BotType.GOAL_DEPOSIT);
                if (g1 == null)
                    throw new RuntimeException("No Goal was persisted for Goal Deposit conversation.");
                return new GoalDepositCallback((DooitApplication) getActivity().getApplication(), g1);
            case GOAL_WITHDRAW:
                Goal g2 = persisted.loadConvoGoal(BotType.GOAL_WITHDRAW);
                if (g2 == null)
                    throw new RuntimeException("No Goal was persisted for Goal Withdraw conversation.");
                return new GoalWithdrawCallback((DooitApplication) getActivity().getApplication(), g2);
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

        // For replacing existing nodes in the converstation
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
        getBotAdapter().addItem(answer);
        conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
        persisted.saveConversationState(type, getBotAdapter().getDataSet());

        if (!TextUtils.isEmpty(answer.getNext())) {
            getAndAddNode(answer.getNext());
            if (BotMessageType.getValueOf(currentModel.getType()) == BotMessageType.END) {
                // Reached explicit end of current converstation
                finishConverstation();
            }
        } else {
            answerView.setData(new ArrayList<>());
        }

        persisted.saveConversationState(type, getBotAdapter().getDataSet());
    }

    private void finishConverstation() {
        if (callback != null) {
            callback.onDone(createAnswerLog(getBotAdapter().getDataSet()));
        }
        persisted.clearConversation();
        persisted.clearConvoGoals();
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
        Node node = (Node) currentModel;
        if (node != null) {
            node.setIconHidden(iconHidden);

            // Don't add views for BLANK and STARTCONVO
            if (!BotMessageType.BLANK.equals(BotMessageType.getValueOf(currentModel.getType())) &&
                    !BotMessageType.STARTCONVO.equals(BotMessageType.getValueOf(currentModel.getType()))) {
                getBotAdapter().addItem(currentModel);
            }

            conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
            persisted.saveConversationState(type, getBotAdapter().getDataSet());
            addAnswerOptions(node);
        }
    }

    private void addAnswerOptions(Node node) {
        answerView.setData(new ArrayList<>());
        if (node.getAnswers().size() > 0) {
            if (TextUtils.isEmpty(node.getAutoAnswer())) {
                answerView.setData(node.getAnswers(), new HashtagView.DataStateTransform<Answer>() {
                    @Override
                    public CharSequence prepareSelected(Answer item) {
                        return item.getText(getContext());
                    }

                    @Override
                    public CharSequence prepare(Answer item) {
                        return item.getText(getContext());
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
                // Auto load next converstation
                finishConverstation();
                BotType botType = BotType.valueOf(node.getAutoNext().toUpperCase());
                setBotType(botType);
                createFeed();
            } else {
                // Auto load next node in current converstation
                getAndAddNode(node.getAutoNext(), true);
            }
        }
    }
}
