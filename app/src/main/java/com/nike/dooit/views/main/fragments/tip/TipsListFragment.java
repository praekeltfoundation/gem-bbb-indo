package com.nike.dooit.views.main.fragments.tip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.models.Tip;
import com.nike.dooit.views.main.fragments.tip.adapters.TipsAdapter;
import com.nike.dooit.views.main.fragments.tip.providers.TipProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;


public class TipsListFragment extends Fragment {

    private static final String POS = "pos";

    @BindView(R.id.fragment_tips_list_recyclerview)
    RecyclerView recyclerView;

    TipsViewPagerPositions pos;
    TipProvider tipProvider;
    TipsAdapter adapter;
    GridLayoutManager gridManager;

    public TipsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TipsListFragment.
     */
    public static TipsListFragment newInstance(int pos) {
        TipsListFragment fragment = new TipsListFragment();
        Bundle args = new Bundle();
        args.putInt(POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        if (getArguments() != null) {
            pos = TipsViewPagerPositions.getValueOf(getArguments().getInt(POS));
            tipProvider = pos.newProvider((DooitApplication) getActivity().getApplication());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (tipProvider.hasTips()) {
            adapter.updateTips(tipProvider.loadTips());
        } else {
            retrieveTips();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        tipProvider.saveTips(adapter.getTips());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tipProvider.hasTips()) {
            adapter.updateTips(tipProvider.loadTips());
        } else {
            retrieveTips();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tipProvider.clearTips();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tips_list, container, false);
        ButterKnife.bind(this, view);

        gridManager = new GridLayoutManager(getContext(), 2);
        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1;
            }
        });

        recyclerView.setLayoutManager(gridManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TipsAdapter((DooitApplication) getActivity().getApplication());
        recyclerView.setAdapter(adapter);

        retrieveTips();

        return view;
    }

    private void retrieveTips() {
        tipProvider.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Error retrieving tips.", Toast.LENGTH_SHORT);
            }
        })
                .subscribe(new Action1<List<Tip>>() {
                    @Override
                    public void call(final List<Tip> tips) {
                        if (getActivity() != null)
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
