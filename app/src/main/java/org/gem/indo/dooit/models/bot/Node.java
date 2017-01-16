package org.gem.indo.dooit.models.bot;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

//    {
//        "name": "askGoalImage",
//            "type": "choice",
//            "text": "$(askGoalImage)",
//            "answers": [
//        { "name": "Capture", "value": " ", "text":"$(Capture)", "next": "askGoalImageTake"},
//        { "name": "Skip", "value": "Skip", "text":"$(Skip)", "next": "askKnowGoalAmount"}
//        ]
//    },

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Node extends BaseBotModel {

    private String autoNext;
    // Multiple choice answers
    private List<Answer> answers = new ArrayList<>();
    private boolean iconHidden;
    private String answerName;
    private String autoAnswer;
    // Allows a conversation to open a different screen
    private String autoNextScreen;

    /**
     * Explicit reference to the next model in the conversation. Used when programmatically building
     * Nodes that are not in the feed.
     */
    private Node autoNextNode;

    public Node() {
        super(Node.class.toString());
    }

    public ArrayList<Answer> getAnswers() {
        if (answers == null)
            return new ArrayList<>();
        return new ArrayList<>(answers);
    }

    public void addAnswer(Answer answer) {
        answer.setParentName(name);
        answers.add(answer);
    }

    public String getAutoNext() {
        return autoNext;
    }

    public Node getAutoNextNode() {
        return autoNextNode;
    }

    public void setAutoNext(String autoNext) {
        this.autoNext = autoNext;
    }

    public void setAutoNext(Node autoNextModel) {
        this.autoNextNode = autoNextModel;
    }

    public boolean hasAutoNext() {
        return !TextUtils.isEmpty(autoNext);
    }

    public boolean hasAutoNextNode() {
        return autoNextNode != null;
    }

    public boolean hasAnyNext() {
        return hasNext() || hasAutoNext() || hasAutoNextNode();
    }

    public boolean isIconHidden() {
        return iconHidden;
    }

    public void setIconHidden(boolean iconHidden) {
        this.iconHidden = iconHidden;
    }

    public CharSequence getAutoAnswer() {
        return autoAnswer;
    }

    public String getAnswerName() {
        return TextUtils.isEmpty(answerName) ? name : answerName;
    }

    public String getNextScreen() {
        return autoNextScreen;
    }

    public void setNextScreen(String nextScreen) {
        this.autoNextScreen = nextScreen;
    }

    public boolean hasNextScreen() {
        return !TextUtils.isEmpty(autoNextScreen);
    }

    @Override
    public String toString() {
        return "Node{" + name + "}";
    }
}
