package com.nike.dooit.models.bot;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.nike.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {
    private String value;
    private String next;
    private String nextOnFinish;
    private String removeOnSelect;
    private String[] changeOnSelect;
    protected String inlineEditHint;
    private String typeOnFinish;

    public Answer() {
        super(Answer.class.toString());
        type = BotMessageType.ANSWER.name();
        typeOnFinish = type;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
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

    public String getRemoveOnSelect() {
        return removeOnSelect;
    }

    public Pair<String, String> getChangeOnSelect() {
        if (changeOnSelect != null && changeOnSelect.length == 2)
            return new Pair<String, String>(changeOnSelect[0], changeOnSelect[1]);
        return null;
    }

    public String getInlineEditHint(Context context) {
        return getResourceString(context, inlineEditHint);
    }

    public void setRemoveOnSelect(String removeOnSelect) {
        this.removeOnSelect = removeOnSelect;
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
}
