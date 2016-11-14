package com.nike.dooit.views.main.fragments.bot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.nike.dooit.models.bot.Goal;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  BotFragment extends MainFragment implements HashtagView.TagsClickListener {
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
        conversationRecyclerView.setAdapter(new BotAdapter(getContext()));

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
            case GOAL:
                feed = new BotFeed<>(getContext());
                feed.parse(R.raw.goals, Goal.class);
                Goal goal = (Goal) feed.getFirstItem();
                currentModel = goal;
                getBotAdapter().addItem(goal);
                persisted.saveConversationState(getBotAdapter().getDataSet());
                answerView.setData(goal.getAnswers(), new HashtagView.DataStateTransform<Answer>() {
                    @Override
                    public CharSequence prepareSelected(Answer item) {
                        return item.getText(getContext());
                    }

                    @Override
                    public CharSequence prepare(Answer item) {
                        return item.getText(getContext());
                    }
                });
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
            case GOAL:
                getBotAdapter().addItem(answer);
                persisted.saveConversationState(getBotAdapter().getDataSet());
                currentModel = feed.getItem(answer.getNext());
                answerView.setData(((Goal) currentModel).getAnswers(), new HashtagView.DataStateTransform<Answer>() {
                    @Override
                    public CharSequence prepareSelected(Answer item) {
                        return item.getText(getContext());
                    }

                    @Override
                    public CharSequence prepare(Answer item) {
                        return item.getText(getContext());
                    }
                });
                break;
            case SAVINGS:
                break;
        }
    }
}
