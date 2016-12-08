package org.gem.indo.dooit.views.onboarding.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.views.custom.WigglyEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class PasswordResetUsernameFragment extends Fragment {
    /*************
     * Variables *
     *************/

    Subscription dataSubscription = null;


    /************
     * Bindings *
     ************/

    @Inject
    UserManager userManager;

    @BindView(R.id.activity_onboarding_reset_textbox)
    WigglyEditText usernameBox;

    @BindView(R.id.activity_onboarding_reset_button)
    Button submitButton;

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
    public void onStop() {
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }


    /***************
     * Interaction *
     ***************/

    @OnClick(R.id.activity_onboarding_reset_button)
    protected void submit() {
        final String username = usernameBox.getEditText();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getContext(), "Must enter a username.", Toast.LENGTH_SHORT).show();
            return;
        }

        userManager.getSecurityQuestion(usernameBox.getEditText(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No such username exists.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if (TextUtils.isEmpty(s)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "No security question.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = PasswordResetPasswordFragment.newInstance(username, s);
                    ft.replace(R.id.activity_container, f, "password_reset_fragment");
                    ft.commit();
                }
            }
        });
    }
}
