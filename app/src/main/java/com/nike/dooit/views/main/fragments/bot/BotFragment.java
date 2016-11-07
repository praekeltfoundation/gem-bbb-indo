package com.nike.dooit.views.main.fragments.bot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.bot.BotFeed;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BaseBotModel;
import com.nike.dooit.models.bot.Goal;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.bot.adapters.AnswerAdapter;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BotFragment extends MainFragment implements AnswerAdapter.AnswerSelectedListener {
    @BindView(R.id.fragment_bot_conversation_recycler_view)
    RecyclerView conversationRecyclerView;
    @BindView(R.id.fragment_bot_answer_recycler_view)
    RecyclerView answerRecyclerView;

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
        answerRecyclerView.setHasFixedSize(true);
        answerRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.VERTICAL));
        answerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        answerRecyclerView.setAdapter(new AnswerAdapter(getContext()));
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
                getAnswersAdapter().setItems(goal.getAnswers());
                break;
        }
    }

    public void setBotType(BotType type) {
        this.type = type;
    }

    public AnswerAdapter getAnswersAdapter() {
        if (answerRecyclerView != null) {
            return (AnswerAdapter) answerRecyclerView.getAdapter();
        }
        return null;
    }

    public BotAdapter getBotAdapter() {
        if (answerRecyclerView != null) {
            return (BotAdapter) conversationRecyclerView.getAdapter();
        }
        return null;
    }

    @Override
    public void onClick(View view, Answer answer) {
        switch (type) {
            case GOAL:
                getBotAdapter().addItem(answer);
                currentModel = feed.getItem(answer.getNext());
                getAnswersAdapter().setItems(((Goal) currentModel).getAnswers());
                break;
        }
    }
}
