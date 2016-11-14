package com.nike.dooit.models.bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Node extends BaseBotModel {
    //    {
//        "name": "askGoalImage",
//            "type": "choice",
//            "text": "$(askGoalImage)",
//            "answers": [
//        { "name": "Capture", "value": " ", "text":"$(Capture)", "next": "askGoalImageTake"},
//        { "name": "Skip", "value": "Skip", "text":"$(Skip)", "next": "askKnowGoalAmount"}
//        ]
//    },
    private String autoNext;
    private List<Answer> answers;
    private boolean iconHidden;
    private String autoAnswer;

    public ArrayList<Answer> getAnswers() {
        if (answers == null)
            return new ArrayList<>();
        return new ArrayList<>(answers);
    }

    public String getAutoNext() {
        return autoNext;
    }

    public void setAutoNext(String autoNext) {
        this.autoNext = autoNext;
    }

    public void setIconHidden(boolean iconHidden) {
        this.iconHidden = iconHidden;
    }

    public boolean isIconHidden() {
        return iconHidden;
    }

    public CharSequence getAutoAnswer() {
        return autoAnswer;
    }
}
