package org.gem.indo.dooit.views.profile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class BudgetFragment extends Fragment {

    public BudgetFragment() {

    }

    public static Fragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_budget, container, false);

        return view;
    }
}
