package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.Badge;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/12/10.
 */

public class TransactionResponse {

    @SerializedName("new_badges")
    List<Badge> newBadges;

    public List<Badge> getNewBadges() {
        return newBadges;
    }

    public boolean hasNewBadges() {
        return newBadges != null && !newBadges.isEmpty();
    }
}
