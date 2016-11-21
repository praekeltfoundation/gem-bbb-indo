package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.managers.UserManager;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chris on 2016-11-21.
 */

public class ChangeNameActivity extends DooitActivity {
    private static final String NAME_PATTERN = "[a-zA-Z0-9@\\.\\=\\-\\_]+";
    private static final int MAX_NAME_LENGTH = 150;

    @BindView(R.id.activity_change_name_text_edit)
    EditText name;

    @BindView(R.id.activity_change_name_example_text_edit)
    TextView nameHint;

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omboarding_change_name);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.activity_change_name_button)
    public void changeName() {
        if(!isNameValid())
            return;
        //userManager.
    }
    public boolean isNameValid() {
        boolean valid = true;
        String nameText = name.getText().toString();
        if (TextUtils.isEmpty(nameText)) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_1);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.contains(" ")) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_2);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (!Pattern.compile(NAME_PATTERN).matcher(nameText).matches()) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_3);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.length() > MAX_NAME_LENGTH) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_4);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }

    public static class Builder extends DooitActivityBuilder<ChangeNameActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangeNameActivity.Builder create(Context context) {
            ChangeNameActivity.Builder builder = new ChangeNameActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangeNameActivity.class);
        }
    }
}
