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
import android.view.View;
import android.view.ViewGroup;

import com.greenfrvr.hashtagview.HashtagView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.helpers.bot.BotFeed;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.bot.Node;
import com.nike.dooit.models.enums.BotMessageType;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.util.ArrayList;

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
        setHasOptionsMenu(true);
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
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_bot, menu);
    }

    @Override
    public void onActive() {
        super.onActive();
        switch (type) {
            case DEFAULT:
                feed = new BotFeed<>(getContext());
                feed.parse(R.raw.goals, Node.class);
                getAndAddNode(null);
                break;
            case GOAL:
                feed = new BotFeed<>(getContext());
                feed.parse(R.raw.goals, Node.class);
                getAndAddNode("askGoalName");
                break;
        }
    }

    public void setBotType(BotType type) {
        this.type = type;
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
            case GOAL:
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
                persisted.saveConversationState(getBotAdapter().getDataSet());
                if (TextUtils.isEmpty(answer.getType())
                        || BotMessageType.getValueOf(answer.getType()).equals(BotMessageType.ANSWER)) {
                    getAndAddNode(answer.getNext());
                } else {
                    answerView.setData(new ArrayList<>());
                }
                break;
            case SAVINGS:
                break;
        }
        persisted.saveConversationState(getBotAdapter().getDataSet());
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
        node.setIconHidden(iconHidden);
        getBotAdapter().addItem(currentModel);
        persisted.saveConversationState(getBotAdapter().getDataSet());
        answerView.setData(new ArrayList<>());
        if (node.getAnswers().size() > 0) {
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
        } else if (!TextUtils.isEmpty(node.getAutoNext())) {
            getAndAddNode(node.getAutoNext(), true);
        }
    }
}
