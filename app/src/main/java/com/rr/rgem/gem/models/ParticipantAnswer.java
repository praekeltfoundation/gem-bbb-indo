package com.rr.rgem.gem.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.rr.rgem.gem.Persisted;

import java.lang.reflect.Type;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Rudolph Jacobs on 2016-10-28.
 */

public class ParticipantAnswer {
    private Long participant;

    private Long question;

    @SerializedName("selected_option")
    private Long selectedOption;

    @SerializedName("date_answered")
    Date dateAnswered = new Date();

    public ParticipantAnswer(Question question, Answer selectedOption)
    {
        this.question = question.getId();
        this.selectedOption = selectedOption.getId();
        this.dateAnswered = new Date();
    }

    public ParticipantAnswer(long question, long selectedOption)
    {
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new Date();
    }

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public long getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(long selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Date getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(Date dateAnswered) {
        this.dateAnswered = dateAnswered;
    }

    public String toJson() {
        return new GsonBuilder().registerTypeAdapter(ParticipantAnswer.class, new ParticipantAnswerSerializer()).create().toJson(this);
    }

    public static class ParticipantAnswerSerializer implements JsonSerializer<ParticipantAnswer> {
        @Override
        public JsonElement serialize(ParticipantAnswer participantAnswer, Type type, JsonSerializationContext context) {
            JsonElement participantAnswerJson = new Gson().toJsonTree(participantAnswer);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            participantAnswerJson.getAsJsonObject().add("date_answered", new JsonPrimitive(ft.format(participantAnswer.dateAnswered)));
            return participantAnswerJson;
        }
    }
}
