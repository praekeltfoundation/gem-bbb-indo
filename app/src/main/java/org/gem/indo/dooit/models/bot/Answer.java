package org.gem.indo.dooit.models.bot;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.gem.indo.dooit.models.enums.BotMessageType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {
    protected String inlineEditHint;
    private String value;
    private String nextOnFinish;
    private String removeOnSelect;
    private String[] changeOnSelect;
    private String typeOnFinish;
    private Map<String, String> valueMap = new LinkedHashMap<>();

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

    public void put(String key, String value) {
        valueMap.put(key, value);
    }

    public String get(String key) {
        return valueMap.get(key);
    }

    public boolean contains(String key) {
        return valueMap.containsKey(key);
    }

    @Override
    public String toString() {
        return "Answer{" + name + "}";
    }
}
