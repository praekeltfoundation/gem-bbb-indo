package org.gem.indo.dooit.views.onboarding.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
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

public class PasswordResetPasswordFragment extends Fragment {
    /*************
     * Constants *
     *************/

    public static final String ARG_USERNAME = "password_reset_username";
    public static final String ARG_QUESTION = "password_reset_question";


    /*************
     * Variables *
     *************/

    Subscription dataSubscription = null;
    String username;
    String question;


    /************
     * Bindings *
     ************/

    @Inject
    UserManager userManager;

    @BindView(R.id.activity_onboarding_reset_info)
    TextView infoView;

    @BindView(R.id.activity_onboarding_reset_answer)
    WigglyEditText answerBox;

    @BindView(R.id.activity_onboarding_reset_new_password)
    WigglyEditText passwordBox;

    @BindView(R.id.activity_onboarding_reset_button)
    Button submitButton;

    @Nullable
    Unbinder unbinder = null;


    /****************
     * Constructors *
     ****************/

    public PasswordResetPasswordFragment() {
        // Required empty public constructor
    }

    public static PasswordResetPasswordFragment newInstance(@NonNull String username,
                                                            @NonNull String question) {
        PasswordResetPasswordFragment fragment = new PasswordResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_QUESTION, question);
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
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            question = getArguments().getString(ARG_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_password_reset, container, false);
        unbinder = ButterKnife.bind(this, view);
        infoView.setText(question);
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
        boolean valid = true;
        String answer = answerBox.getEditText();
        String password = passwordBox.getEditText();

        if (TextUtils.isEmpty(answer)) {
            answerBox.setMessageText("Required.");
            valid = false;
        } else {
            answerBox.setMessageText("");
        }

        if (TextUtils.isEmpty(password)) {
            passwordBox.setMessageText("Required.");
            valid = false;
        } else {
            passwordBox.setMessageText("");
        }

        if (!valid) {
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.label_loading));
        dialog.show();

        userManager.submitSecurityQuestionResponse(username, answer, password, new DooitErrorHandler() {
            @Override
            public void onError(final DooitAPIError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, List<String>> errorMap = error.getErrorResponse().getFieldErrors();
                        if (errorMap.containsKey("answer")) {
                            answerBox.setMessageText(TextUtils.join("\n", errorMap.get("answer")));
                        } else {
                            answerBox.setMessageText("");
                        }

                        if (errorMap.containsKey("new_password")) {
                            passwordBox.setMessageText(TextUtils.join("\n", errorMap.get("new_password")));
                        } else {
                            passwordBox.setMessageText("");
                        }
                    }
                });            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse response) {
                getActivity().finish();
            }
        });
    }
}
