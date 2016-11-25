package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.gem.indo.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Rudolph Jacobs on 2016-11-18.
 */

public class ChallengeSuccessLightboxFragment extends DialogFragment {
    static final String ARG_SUCCESS = "success";
    static final String ARG_HINT = "hint";
    boolean success = false;
    String hint = null;
    Unbinder unbinder = null;
    DialogInterface.OnDismissListener dismissListener = null;

    @Nullable
    @BindView(R.id.fragment_challenge_wrong_hint)
    TextView hintText;

    @BindView(R.id.fragment_challenge_continue_button)
    Button nextButton;

    static ChallengeSuccessLightboxFragment newInstance() {
        return newInstance(false);
    }

    static ChallengeSuccessLightboxFragment newInstance(boolean success) {
        return newInstance(success, null);
    }

    static ChallengeSuccessLightboxFragment newInstance(boolean success, String hint) {
        ChallengeSuccessLightboxFragment fragment = new ChallengeSuccessLightboxFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SUCCESS, success);
        args.putString(ARG_HINT, hint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.success = getArguments().getBoolean(ARG_SUCCESS);
            this.hint = getArguments().getString(ARG_HINT);
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, org.gem.indo.dooit.R.style.AppTheme_PopupOverlay_Semitransparent_Dark);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                success ? org.gem.indo.dooit.R.layout.fragment_challenge_right : org.gem.indo.dooit.R.layout.fragment_challenge_wrong,
                container,
                false
        );
        unbinder = ButterKnife.bind(this, view);

        if (hintText != null) {
            if (hint != null && !hint.isEmpty()) {
                hintText.setText(String.format(getString(R.string.challenge_hint_template), hint));
            } else {
                hintText.setText(R.string.challenge_hint_none);
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dismissListener != null) {
            dismissListener.onDismiss(this.getDialog());
        }
        super.onDismiss(dialog);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @OnClick(R.id.fragment_challenge_continue_button)
    public void onClick() {
        dismiss();
    }
}
