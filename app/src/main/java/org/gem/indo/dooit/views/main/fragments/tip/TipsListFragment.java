package org.gem.indo.dooit.views.main.fragments.tip;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.helpers.interfaces.VariableChangeListener;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.filters.TipsListFilter;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;


public class TipsListFragment extends Fragment implements VariableChangeListener {

    private static final String TAG = TipsListFragment.class.getName();
    private static final String POS = "pos";

    @BindView(R.id.fragment_tips_list_recyclerview)
    RecyclerView recyclerView;

    TipsViewPagerPositions pos;
    TipProvider tipProvider;
    TipsListAdapter adapter;
    OnTipsAvailableListener listener;
    GridLayoutManager gridManager;
    View listView;
    Snackbar snackbar;

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

        View listView = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_tips_list, container, false);
        ButterKnife.bind(this, listView);

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
        TipsListFilter temp = (TipsListFilter) adapter.getFilter();
        temp.setVariableChangeListener(this);

        retrieveTips();

        return listView;
    }

    public void onSearch(String constraint) {
        adapter.getFilter().filter(constraint);
    }

    public void clearFilter(View v) {
        hideFiltering();
        adapter.resetFiltered();
        if(snackbar != null) {
            snackbar.dismiss();
        }
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
        if (listener != null) {
            listener.onTipsAvailable(tips);
        }
    }

    private void retrieveTips() {
        tipProvider.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Error retrieving tips.", Toast.LENGTH_SHORT);
            }
        }).subscribe(new Action1<List<Tip>>() {
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

    protected void hideFiltering() {
        if (listener != null)
            listener.hideFiltering();
        else
            System.out.println("filtering not set");
    }

    @Override
    public void onVariableChanged(Object variableThatHasChanged) {
        Log.d("onVariableChanged", "onVariableChanged function called!!!!!!!!!");

        if (recyclerView != null) {
            int numFilteredTips = (int) variableThatHasChanged;

            if (snackbar == null) {
                snackbar = Snackbar.make(recyclerView, R.string.tips_no_tips_on_filter, Snackbar.LENGTH_INDEFINITE);
            }

            if (numFilteredTips == 0) {
                snackbar.show();
            } else {
                snackbar.dismiss();
            }
        }
    }
}
