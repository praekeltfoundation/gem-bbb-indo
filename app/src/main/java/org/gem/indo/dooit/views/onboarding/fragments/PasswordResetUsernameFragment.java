package org.gem.indo.dooit.views.onboarding.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action0;
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
            usernameBox.setMessageText(getString(R.string.activity_password_reset_username_required));
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.label_loading));
        dialog.show();

        dataSubscription = userManager.getSecurityQuestion(usernameBox.getEditText(), new DooitErrorHandler() {
            @Override
            public void onError(final DooitAPIError error) {
                if (error == null || error.getErrorResponse() == null) {
                    Snackbar.make(usernameBox, R.string.fragment_reset_could_not_fetch_security_q, Snackbar.LENGTH_SHORT).show();
                } else {
                    usernameBox.post(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, List<String>> errorMap = error.getErrorResponse().getFieldErrors();
                            if (errorMap.containsKey("username") && errorMap.get("username") != null) {
                                usernameBox.setMessageText(TextUtils.join("\n", errorMap.get("username")));
                            } else {
                                usernameBox.setMessageText("");
                            }
                        }
                    });
                }
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = PasswordResetPasswordFragment.newInstance(username, s);
                ft.replace(R.id.activity_container, f, "password_reset_fragment");
                ft.commit();
            }
        });
    }
}
