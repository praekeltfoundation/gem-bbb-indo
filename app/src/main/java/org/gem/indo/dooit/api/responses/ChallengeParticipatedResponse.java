package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.challenge.BaseChallenge;

/**
 * Created by Chad Garrett on 2017/04/28.
 */

public class ChallengeParticipatedResponse {
    @SerializedName("available")
    private boolean available;

    @SerializedName("challenge")
    private BaseChallenge challenge;

    public boolean showChallengePopup() { return available; }

    public BaseChallenge getChallenge() { return challenge; }
}
