package com.nike.dooit.views.main.fragments.bot;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.helpers.bot.BotFeed;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.bot.BotCallback;
import com.nike.dooit.models.bot.Node;
import com.nike.dooit.models.enums.BotMessageType;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;
import com.nike.dooit.views.main.fragments.target.callbacks.GoalAddCallback;
import com.nike.dooit.views.main.fragments.target.callbacks.GoalDepositCallback;

import java.lang.reflect.Type;
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
public class BotFragment extends MainFragment implements HashtagView.TagsClickListener {

    @BindView(R.id.fragment_bot_conversation_recycler_view)
    RecyclerView conversationRecyclerView;

    @BindView(R.id.fragment_bot_answer_hash_view)
    HashtagView answerView;

    @Inject
    Persisted persisted;

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
        View view = inflater.inflate(R.layout.fragment_bot, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        answerView.addOnTagClickListener(this);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        conversationRecyclerView.setAdapter(new BotAdapter(getContext(), this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_bot, menu);
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
        switch (type) {
            case DEFAULT:
            case GOAL_ADD:
                feed = new BotFeed<>(getContext());
                feed.parse(R.raw.goal_add, Node.class);
                break;
            case GOAL_DEPOSIT:
                feed = new BotFeed<>(getContext());
                feed.parse(R.raw.goal_deposit, Node.class);
                break;
        }

        if (persisted.hasConversation(type)) {
            ArrayList<BaseBotModel> data = persisted.loadConversationState(type);
            if (data.size() > 0) {
                getBotAdapter().clear();
                getBotAdapter().addItems(data);
                conversationRecyclerView.scrollToPosition(getBotAdapter().getItemCount() - 1);
                BaseBotModel m = data.get(data.size() - 1);
                if (m instanceof Node)
                    addAnswerOptions((Node) data.get(data.size() - 1));
                else if (m instanceof Answer) {
                    getBotAdapter().removeItem(m);
                    onItemClicked(m);
                }
                return;
            }

            // TODO: Load persisted
            callback = createBotCallback(type);
        } else {
            callback = createBotCallback(type);
        }

        switch (type) {
            case DEFAULT:
                getAndAddNode(null);
                break;
            case GOAL_ADD:
                getAndAddNode("askGoalName");
                break;
            case GOAL_DEPOSIT:
                getAndAddNode("depositIntroduction");
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
                Goal goal = persisted.loadConvoGoal(BotType.GOAL_DEPOSIT);
                if (goal == null)
                    throw new RuntimeException("No Goal was persisted for Goal Deposit conversation.");
                return new GoalDepositCallback((DooitApplication) getActivity().getApplication(), goal);
            default:
                return null;
        }
    }

    private Type getBotCallbackClass(BotType botType) {
        switch (botType) {
            case DEFAULT:
            case GOAL_ADD:
                return GoalAddCallback.class;
            case GOAL_DEPOSIT:
                return GoalDepositCallback.class;
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
        switch (type) {
            case DEFAULT:
            case GOAL_ADD:
            case GOAL_DEPOSIT:
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
                    if (BotMessageType.getValueOf(currentModel.getType()) == BotMessageType.END)
                        if (callback != null) {
                            callback.onDone(createAnswerLog(getBotAdapter().getDataSet()));
                            persisted.clearConversation();
                            persisted.clearConvoGoals();
                        }
                } else {
                    answerView.setData(new ArrayList<>());
                }
                break;
            case SAVINGS:
                break;
        }
        persisted.saveConversationState(type, getBotAdapter().getDataSet());
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
            if (!BotMessageType.BLANK.equals(BotMessageType.getValueOf(currentModel.getType()))) {
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
            getAndAddNode(node.getAutoNext(), true);
        }
    }
}
