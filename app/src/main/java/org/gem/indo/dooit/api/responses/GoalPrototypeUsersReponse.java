package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Reinhardt on 2017/01/23.
 */

public class GoalPrototypeUsersReponse {
    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    @SerializedName("user_id__count")
    private int usersCount;
}
