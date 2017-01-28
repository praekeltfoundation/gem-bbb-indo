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

    public boolean hasValue() {
        return !TextUtils.isEmpty(value);
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

        if (key != null)
            inputKey = key.getKey();
        else
            inputKey = null;
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

    public void setChangeOnSelect(Pair<String, String> pair) {
        if (pair != null)
            changeOnSelect = new String[]{pair.first, pair.second};
        else
            changeOnSelect = null;
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

    public Answer copy() {
        Answer answer = new Answer();

        //The BaseBotModel variables
        answer.text = this.text != null ? new String(this.text) : null;
        answer.processedText = this.processedText != null ? new String(this.processedText) : null;
        answer.name = this.name != null ? new String(this.name) : null;
        answer.type = this.type != null ? new String(this.type) : null;
        answer.next = this.next != null ? new String(this.next) : null;
        answer.call = this.call;
        answer.asyncCall = this.asyncCall;
        //  Note: this.textParams is not longer used but if it becomes used again it will also need to be deep copied
        answer.values = this.values.deepCopy();


        //Copy Answer local variables
        answer.inlineEditHint = this.inlineEditHint != null ? new String(this.inlineEditHint) : null;
        answer.setValue(this.value != null ? new String(value) : null);
        answer.setInputKey(BotParamType.byKey(inputKey));
        answer.setNextOnFinish(this.nextOnFinish != null ? new String(nextOnFinish) : null);
        answer.setRemoveOnSelect(this.removeOnSelect != null ? new String(removeOnSelect) : null);
        answer.setChangeOnSelect(getChangeOnSelect());
        answer.setTypeOnFinish(getTypeOnFinish() != null ? new String(getTypeOnFinish()) : null);
        answer.setParentName(getParentName() != null ? new String(getParentName()) : null);

        //copy parent atttributes

        return answer;
    }

    @Override
    public String toString() {
        return "Answer{" + name + "}";
    }
}
