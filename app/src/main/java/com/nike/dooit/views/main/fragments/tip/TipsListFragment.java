package com.nike.dooit.views.main.fragments.tip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.models.Tip;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;


public class TipsListFragment extends Fragment {

    @BindView(R.id.fragment_tips_list_recyclerview)
    RecyclerView recyclerView;

    @Inject
    TipManager tipManager;

    TipsAdapter adapter;
    StaggeredGridLayoutManager gridManager;

    public TipsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TipsListFragment.
     */
    public static TipsListFragment newInstance() {
        TipsListFragment fragment = new TipsListFragment();
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
    public void onResume() {
        super.onResume();
        // TODO: Look in persist for fragments. API Retrieve otherwise.
        retrieveTips();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: Delete tips from Persist
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tips_list, container, false);
        ButterKnife.bind(this, view);
        gridManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Tip> tips = new ArrayList<>();
        tips.add(new Tip("Tip 1"));
        tips.add(new Tip("Tip 2"));
        tips.add(new Tip("Tip 3"));
        adapter = new TipsAdapter(getContext(), tips);
        recyclerView.setAdapter(adapter);

        retrieveTips();

        return view;
    }

    private void retrieveTips() {
        tipManager.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Error retrieving tips.", Toast.LENGTH_SHORT);
            }
        })
                .subscribe(new Action1<List<Tip>>() {
                    @Override
                    public void call(final List<Tip> tips) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateTips(tips);
                            }
                        });
                    }
                });
    }
}
