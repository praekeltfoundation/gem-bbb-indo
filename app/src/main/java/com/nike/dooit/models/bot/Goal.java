package com.nike.dooit.models.bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Goal extends BaseBotModel {
    //    {
//        "name": "askGoalImage",
//            "type": "choice",
//            "text": "$(askGoalImage)",
//            "answers": [
//        { "name": "Capture", "value": " ", "text":"$(Capture)", "next": "askGoalImageTake"},
//        { "name": "Skip", "value": "Skip", "text":"$(Skip)", "next": "askKnowGoalAmount"}
//        ]
//    },
    private String type;
    private List<Answer> answers;

    public String getType() {
        return type;
    }

    public ArrayList<Answer> getAnswers() {
        if (answers == null)
            return new ArrayList<>();
        return new ArrayList<>(answers);
    }
}
