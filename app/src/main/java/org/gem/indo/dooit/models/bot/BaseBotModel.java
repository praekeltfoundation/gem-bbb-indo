package org.gem.indo.dooit.models.bot;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.gem.indo.dooit.helpers.ValueMap;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.enums.BotCallType;
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
    protected String next;
    protected BotCallType call;
    protected BotCallType asyncCall;
    protected String[] textParams = new String[0];
    protected boolean immutable = false;

    public ValueMap values = new ValueMap();

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
            Log.d(TAG, "Model had an unknown resource as its 'text' attribute: " + jsonResourceName);
            throw ex;
        }
    }

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

    public boolean hasProcessedText() {
        return !TextUtils.isEmpty(processedText);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        if (type == null) {
            CrashlyticsHelper.log(TAG, "getType", "Type is null.");
        }
        return type;
    }

    public BotMessageType getMessageType() {
        if (!TextUtils.isEmpty(type))
            try {
                return BotMessageType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return BotMessageType.UNDEFINED;
            }
        return BotMessageType.UNDEFINED;
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

    public boolean hasNext() {
        return !TextUtils.isEmpty(next);
    }

    //////////////////////
    // Synchronous Call //
    //////////////////////

    public BotCallType getCall() {
        return call;
    }

    public boolean hasCall() {
        return call != null;
    }

    ///////////////////////
    // Asynchronous Call //
    ///////////////////////

    public BotCallType getAsyncCall() {
        return asyncCall;
    }

    public boolean hasAsyncCall() {
        return asyncCall != null;
    }

    // Mutability

    public void finish() {
        immutable = true;
    }

    public boolean isImmutable() {
        return immutable;
    }
}
