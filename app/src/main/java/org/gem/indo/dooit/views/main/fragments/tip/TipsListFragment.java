package org.gem.indo.dooit.views.main.fragments.tip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;


public class TipsListFragment extends Fragment {

    private static final String TAG = TipsListFragment.class.getName();
    private static final String POS = "pos";

    @BindString(org.gem.indo.dooit.R.string.tips_list_filter)
    String filterText;

    @BindView(R.id.fragment_tips_list_filter)
    ViewGroup filterView;

    @BindView(R.id.fragment_tips_list_filter_text_view)
    TextView filterTextView;

    @BindView(R.id.fragment_tips_list_recyclerview)
    RecyclerView recyclerView;

    TipsViewPagerPositions pos;
    TipProvider tipProvider;
    TipsListAdapter adapter;
    OnTipsAvailableListener listener;
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
            List<Tip> tips = tipProvider.loadTips();
            adapter.updateAllTips(tips);
            adapter.resetFiltered();
            notifyTipsLoaded(tips);
        } else {
            retrieveTips();
        }
        hideFiltering();
    }

    @Override
    public void onPause() {
        super.onPause();
        tipProvider.saveTips(adapter.getAllTips());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tipProvider.hasTips()) {
            List<Tip> tips = tipProvider.loadTips();
            adapter.updateAllTips(tips);
            adapter.resetFiltered();
            notifyTipsLoaded(tips);
        } else {
            retrieveTips();
        }
        hideFiltering();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tipProvider.clearTips();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_tips_list, container, false);
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

        adapter = new TipsListAdapter((DooitApplication) getActivity().getApplication());
        recyclerView.setAdapter(adapter);

        retrieveTips();

        return view;
    }

    public void onSearch(String constraint) {
        Log.d(TAG, "On Search " + constraint);
        showFiltering(constraint);
        adapter.getFilter().filter(constraint);
    }

    @OnClick(R.id.fragment_tips_list_filter_image_button)
    public void clearFilter(View v) {
        hideFiltering();
        adapter.resetFiltered();
    }

    public void onPageSelected() {
        Log.d(TAG, "onPageSelected");
    }

    public void onPageDeselected() {
        Log.d(TAG, "onPageDeselected");
    }

    public void setOnTipsLoadedListener(OnTipsAvailableListener listener) {
        this.listener = listener;
    }

    private void notifyTipsLoaded(List<Tip> tips) {
        if (listener != null)
            listener.onTipsAvailable(tips);
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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.updateAllTips(tips);
                                    adapter.resetFiltered();
                                    notifyTipsLoaded(tips);
                                }
                            });
                        }
                    }
                });
    }

    protected void showFiltering(String constraint) {
        filterTextView.setText(String.format(filterText, constraint));
        filterView.setVisibility(View.VISIBLE);
    }

    protected void hideFiltering() {
        filterView.setVisibility(View.GONE);
    }
}
