package org.gem.indo.dooit.models.bot;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotParamType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {

    private String inlineEditHint;
    private String value;
    // Input is currently unused
    @SerializedName("onAnswerInput")
    private String inputKey;
    private String nextOnFinish;
    private String removeOnSelect;
    private String[] changeOnSelect;
    private String typeOnFinish;
    private String parentName;

    public Answer() {
        super(Answer.class.toString());
        type = BotMessageType.ANSWER.name();
        typeOnFinish = type;
    }

    @Override
    public String getText(Context context) {
        if (TextUtils.isEmpty(text))
            return getValue();
        return super.getText(context);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BotParamType getInputKey() {
        if (!TextUtils.isEmpty(inputKey)) {
            inputKey = inputKey.replace("$(", "").replace(")", "");
            if (hasInputKey())
                return BotParamType.byKey(inputKey);
        }
        return null;
    }

    public void setInputKey(BotParamType key) {
        inputKey = key.getKey();
    }

    public boolean hasInputKey() {
        return !TextUtils.isEmpty(inputKey);
    }

    public String getRemoveOnSelect() {
        return removeOnSelect;
    }

    public void setRemoveOnSelect(String removeOnSelect) {
        this.removeOnSelect = removeOnSelect;
    }

    public Pair<String, String> getChangeOnSelect() {
        if (changeOnSelect != null && changeOnSelect.length == 2)
            return new Pair<String, String>(changeOnSelect[0], changeOnSelect[1]);
        return null;
    }

    public String getInlineEditHint(Context context) {
        return getResourceString(context, inlineEditHint);
    }

    public String getNextOnFinish() {
        return nextOnFinish;
    }

    public void setNextOnFinish(String nextOnFinish) {
        this.nextOnFinish = nextOnFinish;
    }

    public String getTypeOnFinish() {
        return typeOnFinish;
    }

    public void setTypeOnFinish(String typeOnFinish) {
        this.typeOnFinish = typeOnFinish;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parent) {
        this.parentName = parent;
    }

    public boolean hasParentName() {
        return !TextUtils.isEmpty(parentName);
    }

    @Override
    public String toString() {
        return "Answer{" + name + "}";
    }
}
