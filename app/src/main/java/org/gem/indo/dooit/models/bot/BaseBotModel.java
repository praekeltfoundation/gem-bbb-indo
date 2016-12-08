package org.gem.indo.dooit.models.bot;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.gem.indo.dooit.helpers.ValueMap;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public abstract class BaseBotModel {

    private static final String TAG = BaseBotModel.class.getName();

    protected final String classType;
    protected String text;
    protected String processedText; // After param parsing
    protected String name;
    protected String type;
    private String next;
    protected String callback;
    protected String asyncCall;
    protected String[] textParams = new String[0];
    protected boolean immutable = false;

    public final ValueMap values = new ValueMap();

    public BaseBotModel(String classType) {
        this.classType = classType;
    }

    String getResourceString(Context context, String jsonResourceName) {
        if (TextUtils.isEmpty(jsonResourceName))
            return jsonResourceName;
        try {
            String resourceString = jsonResourceName.replace("$(", "").replace(")", "").replaceAll(" +", "");
            return context.getString(context.getResources().getIdentifier(resourceString, "string", context.getPackageName()));
        } catch (Resources.NotFoundException ex) {
            Log.d(TAG, jsonResourceName);
            throw ex;
        }
    }

    //

    public String getText(Context context) {
        return getResourceString(context, text);
    }

    public boolean hasText() {
        return !TextUtils.isEmpty(text);
    }

    public String getProcessedText() {
        return processedText;
    }

    public void setProcessedText(String processedText) {
        this.processedText = processedText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(BotMessageType type) {
        this.type = type.name();
    }

    public String[] getTextParams() {
        return textParams == null ? new String[0] : textParams;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassType() {
        return classType;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getCallback() {
        return callback;
    }

    public boolean hasCallback() {
        return callback != null && !TextUtils.isEmpty(callback);
    }

    public String getAsyncCall() {
        return asyncCall;
    }

    public boolean hasAsyncCall() {
        return !TextUtils.isEmpty(asyncCall);
    }

    public void finish() {
        immutable = true;
    }

    public boolean isImmutable() {
        return immutable;
    }
}
