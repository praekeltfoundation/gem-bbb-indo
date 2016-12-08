package org.gem.indo.dooit.views.onboarding.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.UserManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PasswordResetUsernameFragment extends Fragment {

    /*************
     * Variables *
     *************/

    @Inject
    UserManager userManager;

    @Nullable
    Unbinder unbinder = null;


    /****************
     * Constructors *
     ****************/

    public PasswordResetUsernameFragment() {
        // Required empty public constructor
    }

    public static PasswordResetUsernameFragment newInstance() {
        PasswordResetUsernameFragment fragment = new PasswordResetUsernameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /************************
     * Life-cycle overrides *
     ************************/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_password_reset_username, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
