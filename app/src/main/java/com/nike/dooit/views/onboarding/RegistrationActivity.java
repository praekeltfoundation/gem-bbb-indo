package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends DooitActivity {

    @BindView(R.id.activity_registration_t_c_text_view)
    TextView textViewTC;

    @BindView(R.id.activity_registration_login_text_view)
    TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        Spannable spanTc = new SpannableString(getString(R.string.reg_t_c));
        Spannable spanLogin = new SpannableString(getString(R.string.already_registered_log_in));

        if (!getLocal().getCountry().equals("in")) {
            spanTc.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanTc.length() - 17, spanTc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanLogin.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanLogin.length() - 6, spanLogin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewTC.setText(spanTc);
        textViewLogin.setText(spanLogin);
    }

    @OnClick(R.id.activity_registration_t_c_text_view)
    public void openTC() {

    }

    @OnClick(R.id.activity_registration_login_text_view)
    public void openLogin() {

    }
}
