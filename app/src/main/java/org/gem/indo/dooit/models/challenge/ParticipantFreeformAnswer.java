package org.gem.indo.dooit.models.challenge;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-14.
 */

public class ParticipantFreeformAnswer extends BaseParticipantAnswer {
    private String text;

    public ParticipantFreeformAnswer() {
        // Mandatory empty constructor
        super();
    }

    public ParticipantFreeformAnswer(long user, long challenge, long question, String text) {
        this.user = user;
        this.challenge = challenge;
        this.question = question;
        this.text = text;
        this.dateAnswered = new DateTime();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
