package com.rr.rgem.gem.answers;

import android.view.View;
import android.widget.TextView;

import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.ConversationNode;

import java.util.Map;

/**
 * Created by chris on 9/26/2016.
 */
public interface AnswerInterface {
    public boolean textAnswer(ConversationNode.AnswerNode answer, String response);
    public boolean mobileAnswer(ConversationNode.AnswerNode answer, String response);
    public boolean currencyAnswer(ConversationNode.AnswerNode answer, String response);
    public boolean choiceAnswer(ConversationNode.AnswerNode answer, String response);
    public boolean dateAnswer(ConversationNode.AnswerNode answer, String response);
    public boolean passwordAnswer(ConversationNode.AnswerNode answer, String response);
    public void start();
    public String complete(Map<String, String> vars, Map<String, String> responses);
    public void save(String json);
    public void load();
    public String getName();
}
