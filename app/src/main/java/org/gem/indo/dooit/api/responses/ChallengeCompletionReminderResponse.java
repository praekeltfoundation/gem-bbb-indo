package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chad Garrett on 2017/02/16.
 */

public class ChallengeCompletionReminderResponse {

    @SerializedName("available")
    private boolean available;

    public boolean isChallengeIncomplete() {
        return available;
    }
}
