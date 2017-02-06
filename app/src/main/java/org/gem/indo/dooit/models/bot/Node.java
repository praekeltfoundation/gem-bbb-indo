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

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    public void setAutoAnswer(String autoAnswer) {
        this.autoAnswer = autoAnswer;
    }


    public String getAutoNextScreen() {
        return autoNextScreen;
    }

    public void setAutoNextScreen(String autoNextScreen) {
        this.autoNextScreen = autoNextScreen;
    }

    public Node copy() {
        Node n = new Node();

        //The BaseBotModel variables
        n.text = this.text != null ? new String(this.text) : null;
        n.processedText = this.processedText != null ? new String(this.processedText) : null;
        n.name = this.name != null ? new String(this.name) : null;
        n.type = this.type != null ? new String(this.type) : null;
        n.next = this.next != null ? new String(this.next) : null;
        n.call = this.call;
        n.asyncCall = this.asyncCall;
        //  Note: this.textParams is not longer used but if it becomes used again it will also need to be deep copied
        n.values = this.values.deepCopy();


        //The Node Local variables
        n.setAutoNext(this.autoNext != null ? new String(this.autoNext) : null);
        n.setAutoAnswer(this.autoAnswer != null ? new String(this.autoAnswer) : null);
        n.setAnswerName(this.answerName != null ? new String(this.answerName) : null);
        n.setAutoNextScreen(this.autoNextScreen != null ? new String(this.autoNextScreen): null);
        n.setIconHidden(this.iconHidden);

        for (int i = 0; i < this.answers.size(); i++)
//            newAnswers.add(answers.get(i).copy());
             n.addAnswer(answers.get(i).copy());

//        n.answers = newAnswers;
        // Doing a shallow copy here since you don't want to call autoNextNode.copy()
        // and end up copying the entire conversation tree.

        n.autoNextNode = this.autoNextNode;
        return n;
    }

    @Override
    public String toString() {
        return "Node{" + name + "}";
    }
}
