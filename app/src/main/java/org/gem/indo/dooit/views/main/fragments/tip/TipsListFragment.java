package org.gem.indo.dooit.views.main.fragments.tip;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.filters.TipsListFilter;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action1;


public class TipsListFragment extends Fragment implements TipsListFilter.OnFilterDoneListener {

    private static final String TAG = TipsListFragment.class.getName();
    private static final String POS = "pos";

    @BindView(R.id.fragment_tips_list_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_tip_list_progress_container)
    RelativeLayout progressContainer;

    @BindString(R.string.error_retrieve_tips)
    String error_retrieving_tips;

    TipsViewPagerPositions pos;
    TipProvider tipProvider;
    TipsListAdapter adapter;
    OnTipsAvailableListener listener;
    GridLayoutManager gridManager;
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
    public void onPause() {
        super.onPause();
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
    }

    public void onActive() {
        if (tipProvider.hasTips()) {
            List<Tip> tips = tipProvider.loadTips();
            adapter.updateAllTips(tips);
            adapter.resetFiltered();
            notifyTipsLoaded(tips);
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

        View listView = inflater.inflate(R.layout.fragment_tips_list, container, false);
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
        temp.setOnFilterDoneListener(this);

        retrieveTips();

        return listView;
    }

    public void onSearch(String constraint) {
        adapter.getFilter().filter(constraint);
    }

    public void clearFilter() {
        adapter.resetFiltered();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public void onPageSelected() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    public void onPageDeselected() {
        if (snackbar != null)
            snackbar.dismiss();
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
        showLoadingProgress();
        tipProvider.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                final Activity activity = getActivity();
                if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, error_retrieving_tips, Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                Activity activity = getActivity();
                if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoadingProgress();
                        }
                    });
            }
        }).subscribe(new Action1<List<Tip>>() {
            @Override
            public void call(final List<Tip> tips) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Tip tip : tips) {
                                if (tip.hasCoverImageUrl()) {
                                    DraweeHelper.cacheImage(Uri.parse(tip.getCoverImageUrl()));
                                }
                            }

                            adapter.updateAllTips(tips);
                            if (tipProvider != null)
                                tipProvider.saveTips(tips);
                            notifyTipsLoaded(tips);
                        }
                    });
                }
            }
        });
    }

    private void hideLoadingProgress() {
        View view = progressContainer;
        if (view != null)
            progressContainer.setVisibility(View.GONE);
    }

    private void showLoadingProgress() {
        View view = progressContainer;
        if (view != null)
            progressContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFilterDone(int filterCount) {
        if (recyclerView != null) {
            if (snackbar == null)
                snackbar = Snackbar.make(recyclerView, R.string.tips_no_tips_on_filter, Snackbar.LENGTH_INDEFINITE);

            if (filterCount == 0)
                snackbar.show();
            else
                snackbar.dismiss();
        }
    }
}
