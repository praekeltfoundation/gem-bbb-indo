package com.nike.dooit.models.bot;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class BaseBotModel {
    private String text;
    private String name;

    String getResourceString(Context context, String jsonResourceName) {
        if (TextUtils.isEmpty(jsonResourceName))
            return jsonResourceName;
        String resourceString = jsonResourceName.replace("$(", "").replace(")", "").replaceAll(" +", "");
        return context.getString(context.getResources().getIdentifier(resourceString, "string", context.getPackageName()));
    }

    public String getText(Context context) {
        return getResourceString(context, text);
    }

    public String getName() {
        return name;
    }
}