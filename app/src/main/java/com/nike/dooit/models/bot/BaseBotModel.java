package com.nike.dooit.models.bot;

import android.content.Context;
import android.text.TextUtils;

import com.nike.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class BaseBotModel {
    protected String text;
    protected String name;
    protected String type;
    protected String inlineEditHint;
    protected String[] textParams;

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

    public String getType() {
        return type;
    }

    public String[] getTextParams() {
        return textParams;
    }

    public String getInlineEditHint(Context context) {
        return getResourceString(context, inlineEditHint);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(BotMessageType type) {
        this.type = type.name();
    }
}
