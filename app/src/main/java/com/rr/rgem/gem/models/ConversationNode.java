package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 9/14/2016.
 */
public class ConversationNode {
    public String name;
    public String text;
    public NodeType type;
    public String error;
    public String next;
    public AnswerNode[] answers;

    public enum NodeType {
        @SerializedName("choice") choice,
        @SerializedName("currency") currency,
        @SerializedName("date") date,
        @SerializedName("info") info,
        @SerializedName("number") number,
        @SerializedName("text") text,
        @SerializedName("initials") initials,
        @SerializedName("mobile") mobile,
        @SerializedName("password") password,
        @SerializedName("passwordTwo") passwordTwo
    }

    public class AnswerNode {
        public String name;
        public String value;
        public String text;
        public String next;
        public String response;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
