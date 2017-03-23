package org.gem.indo.dooit.models;

import org.gem.indo.dooit.models.enums.FeedbackType;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-12-06.
 */

public class UserFeedback implements Serializable {
    /*************
     * Variables *
     *************/

    private String text = null;
    private FeedbackType type;
    private Long user = null;


    /****************
     * Constructors *
     ****************/

    public UserFeedback() {
        // Mandatory empty constructor
    }

    public UserFeedback(String text, FeedbackType type) {
        this();
        this.text = text;
        this.type = type;
    }

    public UserFeedback(String text, FeedbackType type, long user) {
        this(text, type);
        this.user = user;
    }


    /***********
     * Getters *
     ***********/

    public String getText() {
        return text;
    }

    public FeedbackType getType() {
        return type;
    }

    public Long getUser() {
        return user;
    }


    /***********
     * Setters *
     ***********/

    public void setText(String text) {
        this.text = text;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
