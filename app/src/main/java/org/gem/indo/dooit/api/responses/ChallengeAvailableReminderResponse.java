package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.goal.Goal;

/**
 * Created by Chad Garrett on 2017/02/16.
 */

public class ChallengeAvailableReminderResponse {

    @SerializedName("available")
    private boolean available;

    public boolean showChallengeAvailableReminder() {
        return available;
    }
}
